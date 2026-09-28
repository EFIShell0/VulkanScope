#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
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

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'missing block: {start}')
    return text[a:b] if a >= 0 and b > a else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.2_EXPRESSIVE_LOADING_COMPILE_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 3002' in gradle and 'versionName = "3.0.2"' in gradle, '3.0.2 release identity missing')
need('import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi' in main, 'expressive opt-in type import missing')
need('import androidx.compose.material3.LoadingIndicator' in main, 'expressive loading indicator import missing')
listing = block(main, '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserListing(', '@Composable\nprivate fun SharedStorageFolderRow')
need('LoadingIndicator(color = VulkanAccentSoft)' in listing, 'shared-storage loading indicator changed or missing')
need(main.count('@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserListing(') == 1, 'shared-storage listing must have exactly one explicit expressive opt-in')

for marker, message in [
    ('phase >= 2 -> 1f', '3.0.1 opening-rule stability regressed'),
    ('private val PageChromeSeparation = 12.dp', '3.0.1 page/header separation regressed'),
    ('val landscapeLayout = maxWidth > maxHeight && maxWidth >= 700.dp', '3.0.1 landscape file-manager switch regressed'),
    ('.weight(0.42f)', '3.0.1 landscape controls allocation regressed'),
    ('.weight(0.58f).fillMaxHeight()', '3.0.1 landscape browser allocation regressed'),
    ('private fun Modifier.consumeDesktopSecondaryMouseInput(enabled: Boolean): Modifier', '3.0.1 desktop secondary-input guard regressed'),
    ('awaitPointerEvent(PointerEventPass.Initial)', '3.0.1 initial-pass secondary interception regressed'),
    ('val pressBorderAlpha by animateFloatAsState(if (pressed) 0.46f else 0f', '3.0.1 animated hold border regressed'),
    ('(direct.offset - layoutInfo.viewportStartOffset).toFloat()', 'pager viewport-coordinate repair regressed'),
]:
    need(marker in main, message)

need('PixelCopy' not in main and 'toImageBitmap(' not in main, 'forbidden framebuffer/bitmap capture path present')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')
need('## Release 3.0.2 Material 3 Expressive loading compile restoration' in rules, '3.0.2 project rules missing')
need('# VulkanScope 3.0.2 Expressive Loading Compile Audit' in audit, '3.0.2 audit heading missing')
need(changelog.startswith('## 3.0.2\n'), '3.0.2 changelog entry missing or not first')

protected = {
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'missing protected file: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.2 expressive loading compile verifier')
