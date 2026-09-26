#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path


def digest(path: Path) -> str:
    value = hashlib.sha256()
    with path.open('rb') as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b''):
            value.update(chunk)
    return value.hexdigest()


def files(root: Path) -> dict[str, str]:
    result = {}
    for path in root.rglob('*'):
        if path.is_file() and '.gradle' not in path.parts and 'build' not in path.parts:
            result[path.relative_to(root).as_posix()] = digest(path)
    return result


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument('--predecessor', required=True)
    parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
    args = parser.parse_args()
    predecessor = Path(args.predecessor).resolve()
    root = Path(args.root).resolve()
    before = files(predecessor)
    after = files(root)
    production_prefixes = ('app/', 'registry/')
    allowed = {
        'app/build.gradle.kts',
        'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',
    }
    errors = []
    for relative in sorted(set(before) | set(after)):
        if not relative.startswith(production_prefixes):
            continue
        if before.get(relative) != after.get(relative) and relative not in allowed:
            errors.append(relative)
    if errors:
        for relative in errors:
            print('FAIL unauthorized production-byte change:', relative)
        return 1
    print('VulkanScope 1.5.1 predecessor production-boundary regression: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
