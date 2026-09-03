#!/usr/bin/env python3
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--source', default='.')
parser.add_argument('--package-root')
args = parser.parse_args()
source = Path(args.source).resolve()
errors = []


def listed(root):
    path = root / 'files.txt'
    if not path.is_file():
        return None
    return [line.strip() for line in path.read_text(encoding='utf-8').splitlines() if line.strip()]


def forbidden(relative):
    parts = relative.lower().split('/')
    name = parts[-1]
    return name in {'readme.md', 'release.md'} or 'fastlane' in parts or '__pycache__' in parts or name.endswith('.pyc')


source_list = listed(source)
if source_list is None:
    errors.append('source files.txt missing')
    source_list = []
if source_list != sorted(set(source_list)):
    errors.append('source files.txt must be unique and sorted')
for relative in source_list:
    if forbidden(relative):
        errors.append(f'forbidden release path listed: {relative}')
    if not (source / relative).is_file():
        errors.append(f'listed source file missing: {relative}')
actual_source = sorted(path.relative_to(source).as_posix() for path in source.rglob('*') if path.is_file())
if actual_source != source_list:
    missing = sorted(set(source_list) - set(actual_source))
    extra = sorted(set(actual_source) - set(source_list))
    errors.append(f'source file census mismatch: missing={missing[:12]} extra={extra[:12]}')

if args.package_root:
    package = Path(args.package_root).resolve()
    package_list = listed(package)
    if package_list != source_list:
        errors.append('package files.txt differs from source files.txt')
    actual_package = sorted(
        path.relative_to(package).as_posix()
        for path in package.rglob('*')
        if path.is_file()
    )
    if actual_package != source_list:
        missing = sorted(set(source_list) - set(actual_package))
        extra = sorted(set(actual_package) - set(source_list))
        errors.append(f'package file census mismatch: missing={missing[:12]} extra={extra[:12]}')
    for relative in source_list:
        source_file = source / relative
        package_file = package / relative
        if source_file.is_file() and package_file.is_file() and source_file.read_bytes() != package_file.read_bytes():
            errors.append(f'package byte mismatch: {relative}')
else:
    for relative in source_list:
        if forbidden(relative):
            errors.append(f'package hygiene violation: {relative}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
if args.package_root:
    print(f'PASS package reproducibility: files={len(source_list)} source/package byte-identical')
else:
    print(f'PASS source package manifest/hygiene: files={len(source_list)}')
