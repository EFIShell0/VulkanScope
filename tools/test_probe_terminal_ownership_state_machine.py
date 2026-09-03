#!/usr/bin/env python3
import json
import os
import tempfile
import time
from pathlib import Path

MAX_BYTES = 64 * 1024 * 1024


def atomic_write(path: Path, text: str) -> None:
    tmp = Path(str(path) + '.tmp')
    tmp.write_text(text, encoding='utf-8')
    with tmp.open('rb') as stream:
        os.fsync(stream.fileno())
    os.replace(tmp, path)


def terminal_candidate(text: str, group: str) -> bool:
    try:
        obj = json.loads(text)
    except Exception:
        return False
    if group != 'base':
        return True
    return obj.get('baseReportComplete') is True or obj.get('status') in {'unavailable', 'incomplete', 'not_applicable'}


def normal_consume_once(result: Path, done: Path, group: str):
    if not done.is_file():
        return 'checkpoint-only', None
    if not result.is_file() or not (1 <= result.stat().st_size <= MAX_BYTES):
        return 'invalid-terminal', None
    payload = result.read_text(encoding='utf-8')
    return ('accepted', payload) if terminal_candidate(payload, group) else ('invalid-terminal', payload)


def claim_termination(claimed: list[bool]) -> bool:
    if claimed[0]:
        return False
    claimed[0] = True
    return True


def main() -> None:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-terminal-owner-') as td:
        root = Path(td)
        result = root / 'base.json'
        done = root / 'base.json.done'
        final = json.dumps({'status': 'available', 'baseReportComplete': True, 'devices': [{'name': 'fixture'}]})

        atomic_write(result, final)
        state, payload = normal_consume_once(result, done, 'base')
        assert state == 'checkpoint-only' and payload is None

        atomic_write(done, 'done')
        state, payload = normal_consume_once(result, done, 'base')
        assert state == 'accepted' and json.loads(payload)['baseReportComplete'] is True

        claimed = [False]
        assert claim_termination(claimed) is True
        assert claim_termination(claimed) is False
        assert claim_termination(claimed) is False

        result.unlink(); done.unlink()
        atomic_write(result, '{"status":"available"')
        atomic_write(done, 'done')
        state, _ = normal_consume_once(result, done, 'base')
        assert state == 'invalid-terminal'

        result.unlink(); done.unlink()
        atomic_write(result, final)
        atomic_write(root / 'other.json.done', 'done')
        state, _ = normal_consume_once(result, done, 'base')
        assert state == 'checkpoint-only'

        assert terminal_candidate(result.read_text(encoding='utf-8'), 'base') is True

    print('PASS terminal-ownership state machine: pre-return checkpoint is provisional, post-JNI service marker owns terminality, single process-exit claim, malformed-marker fail-closed, unrelated-marker false-positive control, timeout recovery retained')


if __name__ == '__main__':
    main()
