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


def scoped(root: Path) -> dict[str, str]:
    result = {}
    root_files = {'build.gradle.kts', 'settings.gradle.kts', 'gradle.properties', 'gradlew', 'gradlew.bat'}
    for path in root.rglob('*'):
        if not path.is_file() or '.gradle' in path.parts or 'build' in path.parts:
            continue
        relative = path.relative_to(root).as_posix()
        if relative in root_files or relative.startswith(('app/', 'registry/', 'gradle/')):
            result[relative] = digest(path)
    return result


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument('--predecessor', required=True)
    parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
    args = parser.parse_args()
    predecessor = Path(args.predecessor).resolve()
    root = Path(args.root).resolve()
    before = scoped(predecessor)
    after = scoped(root)
    allowed = {'build.gradle.kts', 'app/build.gradle.kts'}
    errors = []
    changed = []
    for relative in sorted(set(before) | set(after)):
        if before.get(relative) != after.get(relative):
            changed.append(relative)
            if relative not in allowed:
                errors.append(relative)
    if errors:
        for relative in errors:
            print('FAIL unauthorized production/build-chain byte change:', relative)
        return 1
    if set(changed) != allowed:
        print('FAIL expected exactly root AGP and app release-identity changes, got:', changed)
        return 1
    print('VulkanScope 1.5.2 immutable-predecessor production/build-chain boundary: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
