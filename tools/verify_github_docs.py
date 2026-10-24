#!/usr/bin/env python3
"""Source-only GitHub documentation sanity checks; no image modification."""
import re
from pathlib import Path
from urllib.parse import unquote

ROOT = Path(__file__).resolve().parents[1]
DOCS = [ROOT / 'README.md', ROOT / 'release.md', ROOT / 'DATABASE_SETUP.md', ROOT / 'changelog.md', ROOT / 'rules/PROJECT_RULES.md', *sorted((ROOT / 'docs').glob('*.md'))]
assert all(path.is_file() for path in DOCS), 'Missing current documentation file'
assert not (ROOT / 'docs/CLEANUP.md').exists(), 'Obsolete cleanup history shipped'
assert not (ROOT / 'docs/RELEASE_4_0_15.md').exists(), 'Redundant versioned release file shipped'
readme = (ROOT / 'README.md').read_text(encoding='utf-8')
assert '**Current version: 4.0.15**' in readme
assert 'Vulkan-Headers 1.4.366' in readme
assert 'On this device' in readme
assert '4015' in readme
assert '[release.md](release.md)' in readme
assert (ROOT / 'release.md').is_file()
assert '**479 registered extensions**' in readme
assert '**6,274**' in readme and '**2,467**' in readme
screenshot_names = [
    'overview-7.jpg', 'properties-7.jpg', 'vulkan-6.jpg', 'surface-6.jpg',
    'display-6.jpg', 'extensions-7.jpg', 'androidstudio-landscape-new-3.png',
    'database_new-8.png',
]
for name in screenshot_names:
    expected = f'https://raw.githubusercontent.com/EFIShell0/VulkanScope/main/screenshots/{name}'
    assert expected in readme, f'Screenshot URL changed: {name}'

for md in DOCS:
    text = md.read_text(encoding='utf-8')
    assert not re.search(r'[ÇĞİÖŞÜçğıöşü]', text), f'Non-English characters in {md.relative_to(ROOT)}'
    for stale in ('VulkanScope 3.0.12', 'VulkanScope 2.0.6', 'Vulkan-Headers 1.4.364', 'schemaVersion 1', 'versionCode 2006'):
        assert stale not in text, f'Stale current-version claim {stale} in {md.relative_to(ROOT)}'
    for url in re.findall(r'(?<!!)\[[^\]]+\]\(([^)]+)\)', text):
        link = unquote(url.split('#', 1)[0].strip())
        if not link or re.match(r'^(?:https?://|mailto:|data:)', link, re.I):
            continue
        assert (md.parent / link).exists(), f'Broken local link in {md.relative_to(ROOT)}: {link}'
print('PASS GitHub documentation: current release details, local links, image references and English-only content')
