#!/usr/bin/env python3
import argparse, sys
from pathlib import Path

def fail(message): raise AssertionError(message)
def need(text, token, message=None):
    if token not in text: fail(message or f"missing {token!r}")
def absent(text, token, message=None):
    if token in text: fail(message or f"forbidden {token!r}")

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); args=ap.parse_args()
    root=Path(args.root).resolve()
    main=(root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text()
    build=(root/'app/build.gradle.kts').read_text()
    manifest=(root/'app/src/main/AndroidManifest.xml').read_text()
    styles=(root/'app/src/main/res/values/styles.xml').read_text()
    rules=(root/'rules/PROJECT_RULES.md').read_text()
    if not args.skip_version:
        need(build,'versionCode = 1404'); need(build,'versionName = "1.4.4"')
        need(rules,'## Release 1.4.4 ChromeOS runtime evidence, adaptive dialogs, Expressive navigation and launch motion')
    for token in [
        'private const val CHROMEOS_ARC_FEATURE = "org.chromium.arc"',
        'context.packageManager.hasSystemFeature(CHROMEOS_ARC_FEATURE)',
        '"ChromeOS Android Runtime (ARC)"',
        '"Unavailable through Android public APIs"',
        'put("chromeOsArcRuntime", isChromeOsRuntime(context))',
        'appendLine("ChromeOS ARC runtime: ${if (isChromeOsRuntime(context)) "Detected" else "Not detected"}")'
    ]: need(main, token, 'ChromeOS evidence contract drift: '+token)
    for token in [
        'val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE',
        'val dialogHeight = if (landscape) availableHeight else minOf(720.dp, availableHeight)',
        'val dialogWidth = if (landscape) 720.dp else 640.dp',
        'modifier = Modifier.fillMaxWidth().widthIn(max = dialogWidth).height(dialogHeight)',
        'Box(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 14.dp, vertical = 12.dp))'
    ]: need(main, token, 'adaptive dialog contract drift: '+token)
    for token in [
        'NavigationRail(', 'NavigationRailItem(', 'NavigationRailItemDefaults.colors(',
        'ShortNavigationBar(containerColor = ComposeColor(0xFF0A0A0A))',
        'alwaysShowLabel = true'
    ]: need(main, token, 'navigation contract drift: '+token)
    for token in [
        'implementation("androidx.core:core-splashscreen:1.2.0")',
    ]: need(build, token, 'splash dependency drift')
    for token in [
        'android:theme="@style/AppTheme.Starting"'
    ]: need(manifest, token, 'starting theme not applied')
    for token in [
        '<style name="AppTheme.Starting" parent="Theme.SplashScreen">',
        '<item name="windowSplashScreenAnimatedIcon">@drawable/vulkanscope_logo_foreground</item>',
        '<item name="postSplashScreenTheme">@style/AppTheme</item>'
    ]: need(styles, token, 'splash theme drift: '+token)
    for token in [
        'val splashScreen = installSplashScreen()',
        'splashScreen.setOnExitAnimationListener',
        '.setDuration(360L)',
        '.withEndAction { provider.remove() }'
    ]: need(main, token, 'splash motion drift: '+token)
    absent(main, 'class SplashActivity', 'dedicated splash Activity is forbidden')
    need(rules, 'Vulkan 1.4.363 dated 2026-09-18')
    need(main, 'CapabilityKeyValue("Current published specification", "Vulkan® 1.4.363 · 2026-09-18")')
    need(main, 'CapabilityKeyValue("Collection baseline", registryCoverage.baseline)')
    print('release_1404 verifier: PASS')

if __name__=='__main__':
    try: main()
    except Exception as exc:
        print('release_1404 verifier: FAIL:',exc,file=sys.stderr); sys.exit(1)
