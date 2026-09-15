#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path

def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', default='.')
    parser.add_argument('--predecessor', required=True)
    args = parser.parse_args()
    root = Path(args.root).resolve()
    pred = Path(args.predecessor).resolve()
    contract = json.loads((root / 'tests/golden/1.3.13_filter_unification_quick_access_contract.json').read_text())
    if sum(1 for path in pred.rglob('*') if path.is_file()) != contract['predecessor_file_count']:
        raise AssertionError('predecessor census mismatch')
    if sha(pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt') != contract['predecessor_main_activity_sha256']:
        raise AssertionError('predecessor MainActivity mismatch')
    for rel, expected in contract['immutable_app_src_main_sha256'].items():
        if sha(pred / rel) != expected or sha(root / rel) != expected:
            raise AssertionError('protected app/src/main drift: ' + rel)
    if (pred / contract['quick_access_icon_relative']).exists():
        raise AssertionError('new Quick access icon unexpectedly exists in predecessor')
    if not (root / contract['quick_access_icon_relative']).is_file():
        raise AssertionError('new Quick access icon missing from successor')
    predecessor_build = (pred / 'app/build.gradle.kts').read_text()
    current_build = (root / 'app/build.gradle.kts').read_text()
    normalized = current_build.replace('versionCode = 1400', 'versionCode = 1313').replace('versionName = "1.4.0"', 'versionName = "1.3.13"')
    if normalized != predecessor_build:
        raise AssertionError('build.gradle changed beyond release identity')
    print('release_1400 regression boundary: PASS')

if __name__ == '__main__':
    try:
        main()
    except Exception as exc:
        print('release_1400 regression boundary: FAIL:', exc, file=sys.stderr)
        sys.exit(1)
