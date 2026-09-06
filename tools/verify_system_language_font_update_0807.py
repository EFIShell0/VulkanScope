#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve()
main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle = root / 'app/build.gradle.kts'
manifest = root / 'app/src/main/AndroidManifest.xml'
styles = [root / 'app/src/main/res/values/styles.xml', root / 'app/src/main/res/values-v27/styles.xml']
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

kt = main.read_text(encoding='utf-8')
gd = gradle.read_text(encoding='utf-8')
mn = manifest.read_text(encoding='utf-8')

def body(name):
    match = re.search(r'private fun (?:[A-Za-z0-9_<>?.]+\.)?' + re.escape(name) + r'\s*\(', kt)
    if not match:
        return ''
    start = kt.find('{', match.start())
    depth = 0
    in_string = False
    escaped = False
    for index in range(start, len(kt)):
        char = kt[index]
        if in_string:
            if escaped:
                escaped = False
            elif char == '\\':
                escaped = True
            elif char == '"':
                in_string = False
            continue
        if char == '"':
            in_string = True
        elif char == '{':
            depth += 1
        elif char == '}':
            depth -= 1
            if depth == 0:
                return kt[match.start():index + 1]
    return ''

if not args.skip_version:
    version_match = re.search(r'versionName\s*=\s*"0\.(\d+)\.(\d+)"', gd)
    code_match = re.search(r'versionCode\s*=\s*(\d+)', gd)
    version_ok = bool(version_match and (int(version_match.group(1)), int(version_match.group(2))) >= (80, 7))
    need(version_ok and bool(code_match), '0.80.7+ identity missing')
need('android:supportsRtl="true"' in mn, 'Android RTL support is disabled')
need('import androidx.compose.material3.Typography' in kt, 'system-font Typography baseline import missing')
need('import androidx.compose.ui.text.style.TextDirection' in kt, 'content-aware bidi TextDirection import missing')
need('private val VulkanBaseTypography = Typography()' in kt, 'system-default typography baseline missing')
need('private val VulkanTypography = Typography(' in kt, 'Vulkan content-aware typography missing')
need('typography = VulkanTypography' in kt, 'Material theme does not install the content-aware typography')
for style in ['displayLarge','displayMedium','displaySmall','headlineLarge','headlineMedium','headlineSmall','titleLarge','titleMedium','titleSmall','bodyLarge','bodyMedium','bodySmall','labelLarge','labelMedium','labelSmall']:
    needle = f'{style} = VulkanBaseTypography.{style}.copy(textDirection = TextDirection.ContentOrLtr)'
    need(needle in kt, f'{style} does not use content-aware LTR fallback')
need('TextAlign.Left' not in kt and 'TextAlign.Right' not in kt, 'absolute left/right text alignment remains in Compose UI')
need('fontFamily = FontFamily.' not in kt, 'Compose UI hardcodes a non-system font family')
need('androidx.compose.ui.text.font.FontFamily' not in kt, 'Compose UI imports an explicit font family override')
for path in styles:
    text = path.read_text(encoding='utf-8')
    need('<item name="android:fontFamily">sans</item>' in text, f'{path.name} no longer uses generic system sans')
font_dir = root / 'app/src/main/res/font'
need(not font_dir.exists(), 'bundled app font resources override system font fallback')
for suffix in ['*.ttf','*.otf','*.woff','*.woff2']:
    need(not any((root / 'app/src/main').rglob(suffix)), f'bundled font binary found: {suffix}')

info = body('UpdateInfoIcon')
need(info, 'blue update information icon component missing')
need('R.drawable.ic_info' in info, 'update information icon does not use the Info glyph')
need('ComposeColor(0xFF16344F)' in info, 'update information icon blue container drifted')
need('ComposeColor(0xFF5CA9FF)' in info, 'update information icon blue tint drifted')
need('contentDescription = null' in info, 'decorative update info icon duplicates live-region text')
update = body('UpdateStatusBanner')
need('is UpdateStatus.Available -> { UpdateInfoIcon();' in update, 'update-available banner does not show the blue Info icon')
need('UpdateStatusBadge("UPDATE")' not in update, 'update-available banner still uses the old status badge instead of the requested Info icon')
need('color = ComposeColor(0xFF9CCBFF)' in update, 'update-available copy does not align with the blue information state')
need('liveRegion = LiveRegionMode.Polite' in update, 'update-available information state lost TalkBack live-region behavior')

if errors:
    for error in errors:
        print('FAIL ' + error)
    raise SystemExit(1)
print('PASS 0.80.7 system-language/bidi, system-font fallback and blue update-info source contract')
