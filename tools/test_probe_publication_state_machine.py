#!/usr/bin/env python3
import json
import os
import tempfile
import time
from pathlib import Path

MAX_BYTES = 64 * 1024 * 1024


def atomic_write(path, text):
    temp = Path(str(path) + '.tmp')
    temp.write_text(text, encoding='utf-8')
    with temp.open('rb') as stream:
        os.fsync(stream.fileno())
    os.replace(temp, path)


def terminal_candidate(text, group):
    try:
        parsed = json.loads(text)
    except Exception:
        return False
    if group != 'base':
        return True
    status = parsed.get('status', 'unavailable')
    return parsed.get('baseReportComplete') is True or status in {'unavailable', 'incomplete', 'not_applicable'}


def read_bounded(path):
    if not path.is_file():
        return None
    size = path.stat().st_size
    if size < 1 or size > MAX_BYTES:
        return None
    return path.read_text(encoding='utf-8')


def consume(result_path, terminal_path, group, timeout_seconds, frozen_fingerprint=None):
    deadline = time.monotonic() + timeout_seconds
    last_fingerprint = frozen_fingerprint
    while time.monotonic() < deadline:
        if terminal_path.is_file():
            candidate = read_bounded(result_path)
            if candidate is None:
                return 'failed', 'terminal marker without readable bounded result'
            if not terminal_candidate(candidate, group):
                return 'failed', 'terminal marker with malformed or non-terminal JSON'
            return 'accepted', candidate
        if result_path.is_file():
            stat = result_path.stat()
            fingerprint = (stat.st_size, stat.st_mtime_ns, getattr(stat, 'st_ino', -1))
            if fingerprint != last_fingerprint:
                last_fingerprint = fingerprint
                candidate = read_bounded(result_path)
                if candidate is not None and terminal_candidate(candidate, group):
                    return 'accepted', candidate
        time.sleep(0.005)
    return 'timeout', None


def main():
    with tempfile.TemporaryDirectory(prefix='vulkanscope-probe-protocol-') as temp_name:
        root = Path(temp_name)
        result_path = root / 'base.json'
        terminal_path = root / 'base.json.done'
        unrelated_path = root / 'other.done'

        atomic_write(result_path, '{"status":"available","baseReportComplete":false')
        atomic_write(result_path, '{"status":"available","baseReportComplete":true,"devices":[{"name":"fixture"}]}')
        stat = result_path.stat()
        frozen = (stat.st_size, stat.st_mtime_ns, getattr(stat, 'st_ino', -1))
        atomic_write(terminal_path, 'done')
        state, payload = consume(result_path, terminal_path, 'base', 0.2, frozen)
        assert state == 'accepted'
        assert json.loads(payload)['baseReportComplete'] is True

        terminal_path.unlink()
        atomic_write(result_path, '{"status":"available","baseReportComplete":true')
        frozen = (result_path.stat().st_size, result_path.stat().st_mtime_ns, getattr(result_path.stat(), 'st_ino', -1))
        atomic_write(terminal_path, 'done')
        started = time.monotonic()
        state, reason = consume(result_path, terminal_path, 'base', 0.2, frozen)
        elapsed = time.monotonic() - started
        assert state == 'failed'
        assert 'malformed' in reason
        assert elapsed < 0.1

        terminal_path.unlink()
        result_path.unlink()
        atomic_write(unrelated_path, 'done')
        state, payload = consume(result_path, terminal_path, 'base', 0.05)
        assert state == 'timeout'
        assert payload is None

        unrelated_path.unlink()
        atomic_write(result_path, '{"status":"unavailable","reason":"fixture","baseReportComplete":false,"devices":[]}')
        frozen = (result_path.stat().st_size, result_path.stat().st_mtime_ns, getattr(result_path.stat(), 'st_ino', -1))
        atomic_write(terminal_path, 'done')
        state, payload = consume(result_path, terminal_path, 'base', 0.2, frozen)
        assert state == 'accepted'
        assert json.loads(payload)['status'] == 'unavailable'

    print('PASS probe publication state machine: terminal forced-reread, malformed-terminal fast-fail, unrelated-marker false-positive control, unavailable terminal acceptance')


if __name__ == '__main__':
    main()
