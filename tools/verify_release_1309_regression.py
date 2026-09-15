#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path


def sha(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--root', default='.')
    ap.add_argument('--predecessor', required=True)
    args = ap.parse_args()
    root = Path(args.root).resolve()
    pred = Path(args.predecessor).resolve()
    contract = json.loads((root / 'tests/golden/1.3.8_ui_storage_refinement_contract.json').read_text(encoding='utf-8'))

    if sum(1 for p in pred.rglob('*') if p.is_file()) != contract['predecessor_file_count']:
        raise AssertionError('predecessor file census mismatch')
    pred_main = pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    if sha(pred_main) != contract['predecessor_main_activity_sha256']:
        raise AssertionError('predecessor MainActivity mismatch')

    # Existing app/src/main files other than MainActivity stay byte-identical. Four new vectors are the only resource additions.
    for rel, expected in contract['immutable_app_src_main_sha256'].items():
        pp = pred / rel
        rp = root / rel
        if not pp.is_file() or sha(pp) != expected:
            raise AssertionError(f'predecessor protected byte mismatch: {rel}')
        if not rp.is_file() or sha(rp) != expected:
            raise AssertionError(f'current protected production drift: {rel}')
    for rel in contract['allowed_new_resources']:
        if (pred / rel).exists():
            raise AssertionError(f'view-mode resource unexpectedly existed in predecessor: {rel}')
        if not (root / rel).is_file():
            raise AssertionError(f'missing allowed new resource: {rel}')

    pb = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8')
    cb = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
    normalized = cb.replace('versionCode = 1309', 'versionCode = 1308').replace('versionName = "1.3.9"', 'versionName = "1.3.8"')
    if normalized != pb:
        raise AssertionError('build.gradle changed beyond release identity')

    print('release_1309 regression boundary: PASS')


if __name__ == '__main__':
    try:
        main()
    except Exception as exc:
        print(f'release_1309 regression boundary: FAIL: {exc}', file=sys.stderr)
        sys.exit(1)
