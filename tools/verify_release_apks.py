from pathlib import Path
import json
import re
import sys
import zipfile

root = Path(__file__).resolve().parents[1]
config = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
match = re.search(r'val\s+releaseVersionName\s*=\s*"(\d+\.\d+\.\d+)"', config)
if match is None:
    raise SystemExit('FAIL: Release version constant not found')
version = match.group(1)
expected = {
    f'vulkanscope-{version}-{abi}.apk'
    for abi in ('universal', 'arm64-v8a', 'x86_64')
}
release_dir = root / 'app/build/outputs/apk/release'
actual = {p.name for p in release_dir.glob('*.apk')} if release_dir.exists() else set()
if actual != expected:
    raise SystemExit(f'FAIL: Release APK filenames differ. Expected {sorted(expected)}; found {sorted(actual)}')
for filename in sorted(expected):
    path = release_dir / filename
    if path.stat().st_size == 0 or not zipfile.is_zipfile(path):
        raise SystemExit(f'FAIL: Invalid or empty APK: {filename}')
metadata_path = release_dir / 'output-metadata.json'
if not metadata_path.is_file():
    raise SystemExit('FAIL: AGP output-metadata.json missing')
metadata = json.loads(metadata_path.read_text(encoding='utf-8'))
entries = metadata.get('elements', [])
if {entry.get('outputFile') for entry in entries} != expected:
    raise SystemExit('FAIL: AGP output metadata does not match release filenames')
for entry in entries:
    apk_name = entry['outputFile']
    filters = entry.get('filters', [])
    abi = next((item.get('value') for item in filters if item.get('filterType') == 'ABI'), 'universal')
    if apk_name != f'vulkanscope-{version}-{abi}.apk':
        raise SystemExit(f'FAIL: Wrong ABI mapping for {apk_name}')
print(f'PASS release APK outputs: {", ".join(sorted(expected))}')
