#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def read(rel):
    path = root / rel
    need(path.is_file(), f'missing file: {rel}')
    return path.read_text(encoding='utf-8') if path.is_file() else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/2.1.11_GRAPHICS_LAYER_IMPORT_COMPILE_FIX_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 2111' in gradle and 'versionName = "2.1.11"' in gradle, '2.1.11 release identity missing')

correct_remember = 'import androidx.compose.ui.graphics.rememberGraphicsLayer'
correct_draw = 'import androidx.compose.ui.graphics.layer.drawLayer'
wrong_remember = 'import androidx.compose.ui.graphics.layer.rememberGraphicsLayer'
wrong_draw = 'import androidx.compose.ui.graphics.drawscope.drawLayer'
need(main.count(correct_remember) == 1, 'correct rememberGraphicsLayer import missing or duplicated')
need(main.count(correct_draw) == 1, 'correct drawLayer import missing or duplicated')
need(wrong_remember not in main, 'compile-breaking rememberGraphicsLayer import remains')
need(wrong_draw not in main, 'compile-breaking drawLayer import remains')
need(main.count('rememberGraphicsLayer()') >= 2, 'retained GraphicsLayer allocation contract missing')
need(main.count('drawLayer(') >= 3, 'retained GraphicsLayer draw contract missing')
need('sourceLayer.record { this@drawWithContent.drawContent() }' in main, '2.1.10 retained source-layer recording missing')
need('blurredLayer.record { drawLayer(sourceLayer) }' in main, '2.1.10 retained blur-layer recording missing')
need('AndroidRenderEffect.createBlurEffect(blurRadiusPx, blurRadiusPx, Shader.TileMode.CLAMP).asComposeRenderEffect()' in main, '2.1.10 bounded blur render effect missing')
need('implementation("androidx.compose.ui:ui:1.12.0")' in gradle, 'Compose UI 1.12.0 pin changed')
need('minSdk = 31' in gradle, 'Android API 31 minimum changed')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

protected = {
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'protected file missing: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

need('Release 2.1.11 Compose GraphicsLayer import compile restoration requirements' in rules, 'PROJECT_RULES missing 2.1.11 contract')
for token in [
    'unresolved `drawLayer`',
    'unresolved `rememberGraphicsLayer`',
    'androidx.compose.ui.graphics.rememberGraphicsLayer',
    'androidx.compose.ui.graphics.layer.drawLayer',
    'Compose UI 1.12.0',
]:
    need(token in audit or token in rules, f'2.1.11 evidence missing: {token}')
need(changelog.startswith('## 2.1.11\n'), '2.1.11 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.11 GraphicsLayer import compile contract')
