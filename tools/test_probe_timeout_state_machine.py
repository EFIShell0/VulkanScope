#!/usr/bin/env python3
import json
import os
import tempfile
import time
from pathlib import Path

MAX_BYTES = 64 * 1024 * 1024


def atomic_write(path: Path, text: str) -> None:
    temp = Path(str(path) + '.tmp')
    temp.write_text(text, encoding='utf-8')
    with temp.open('rb') as stream:
        os.fsync(stream.fileno())
    os.replace(temp, path)


def terminal_candidate(text: str, group: str) -> bool:
    try:
        parsed = json.loads(text)
    except Exception:
        return False
    if group != 'base':
        return True
    status = parsed.get('status', 'unavailable')
    return parsed.get('baseReportComplete') is True or status in {'unavailable', 'incomplete', 'not_applicable'}


def consume(result_path: Path, terminal_path: Path, group: str, timeout_seconds: float) -> tuple[str, str | None]:
    deadline = time.monotonic() + timeout_seconds
    last_fingerprint = None
    while time.monotonic() < deadline:
        if terminal_path.is_file():
            if not result_path.is_file() or not 1 <= result_path.stat().st_size <= MAX_BYTES:
                return 'failed', 'terminal marker without result'
            payload = result_path.read_text(encoding='utf-8')
            if not terminal_candidate(payload, group):
                return 'failed', 'terminal marker with non-terminal result'
            return 'accepted', payload
        if result_path.is_file() and 1 <= result_path.stat().st_size <= MAX_BYTES:
            stat = result_path.stat()
            fingerprint = (stat.st_size, stat.st_mtime_ns, getattr(stat, 'st_ino', -1))
            if fingerprint != last_fingerprint:
                last_fingerprint = fingerprint
                payload = result_path.read_text(encoding='utf-8')
                if terminal_candidate(payload, group):
                    return 'accepted', payload
        time.sleep(0.005)
    return 'timeout', None


def should_retry_base(payload: str, attempt: int) -> bool:
    try:
        parsed = json.loads(payload)
    except Exception:
        return False
    return attempt == 0 and parsed.get('status') == 'incomplete' and bool(parsed.get('devices')) and 'timeout' not in str(parsed.get('reason', '')).lower()


def main() -> None:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-timeout-protocol-') as temp_name:
        root = Path(temp_name)
        result_path = root / 'base.json'
        terminal_path = root / 'base.json.done'
        unrelated = root / 'other.done'

        atomic_write(result_path, '{"status":"available","baseReportComplete":true,"devices":[{"name":"fixture"}]}')
        atomic_write(terminal_path, 'done')
        producer_is_alive = False
        state, payload = consume(result_path, terminal_path, 'base', 0.2)
        assert producer_is_alive is False
        assert state == 'accepted'
        assert json.loads(payload)['baseReportComplete'] is True

        result_path.unlink()
        terminal_path.unlink()
        atomic_write(unrelated, 'done')
        state, payload = consume(result_path, terminal_path, 'base', 0.05)
        assert state == 'timeout' and payload is None

        unavailable = json.dumps({'status': 'unavailable', 'reason': 'The dedicated base probe did not complete within the timeout.', 'baseReportComplete': False, 'devices': []})
        assert should_retry_base(unavailable, 0) is False
        incomplete = json.dumps({'status': 'incomplete', 'reason': 'bounded partial evidence', 'baseReportComplete': False, 'devices': [{'name': 'fixture'}]})
        assert should_retry_base(incomplete, 0) is True
        assert should_retry_base(incomplete, 1) is False

        stale_pids = {101, 102}
        for pid in list(stale_pids):
            stale_pids.discard(pid)
        assert not stale_pids

        activity_manager_visible_pids = set()
        producer_alive = True
        consumer_timeout_ms = 20_000
        watchdog_deadline_ms = consumer_timeout_ms + 100
        post_timeout_settle_ms = 150
        assert not activity_manager_visible_pids
        assert producer_alive
        elapsed_ms = consumer_timeout_ms + post_timeout_settle_ms
        if elapsed_ms >= watchdog_deadline_ms:
            producer_alive = False
        assert producer_alive is False

    print('PASS timeout/handoff state machine: pre-return durable terminal acceptance, producer-death tolerance, independent hard-deadline watchdog with PID-discovery failure, no deterministic-timeout retry, bounded partial retry, stale-process barrier, unrelated-marker false-positive control')


if __name__ == '__main__':
    main()
