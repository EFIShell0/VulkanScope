#!/usr/bin/env python3
import argparse
from pathlib import Path


def section(text: str, start: str, end: str) -> str:
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    return text[a:b] if a >= 0 and b > a else ''


def verify(root: Path, skip_version: bool = False) -> list[str]:
    errors = []
    main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    build = root / 'app/build.gradle.kts'
    rules = root / 'rules/PROJECT_RULES.md'
    if not main.is_file():
        return ['MainActivity.kt missing']
    text = main.read_text(encoding='utf-8')
    build_text = build.read_text(encoding='utf-8') if build.is_file() else ''
    if not skip_version and ('versionCode = 1500' not in build_text or 'versionName = "1.5.0"' not in build_text):
        errors.append('release identity is not 1.5.0/1500')
    if not rules.is_file() or '## Release 1.5.0 compact floating navigation and startup watchdog requirements' not in rules.read_text(encoding='utf-8'):
        errors.append('1.5.0 release rules are missing')

    constants = section(text, 'private val VulkanOutlineVariant', 'private val LocalDetailKeyValuePresentation')
    required_constants = [
        'PrimaryNavigationMaxWidth = 326.dp',
        'PrimaryNavigationHeight = 54.dp',
        'PrimaryNavigationIndicatorHeight = 44.dp',
        'PrimaryNavigationIndicatorHorizontalInset = 7.dp',
        'PrimaryNavigationBottomGap = 10.dp',
        'PrimaryNavigationContentGap = 10.dp',
    ]
    for token in required_constants:
        if token not in constants:
            errors.append(f'navigation metric missing: {token}')

    nav_items = section(text, 'private fun navigationItems()', 'private fun pageIcon')
    expected = [
        'NavigationItem(Page.Overview, "Overview", R.drawable.ic_home)',
        'NavigationItem(Page.Vulkan, "Vulkan", R.drawable.ic_vulkan)',
        'NavigationItem(Page.Surface, "Surface", R.drawable.ic_surface)',
        'NavigationItem(Page.Extensions, "Extensions", R.drawable.ic_extensions)',
    ]
    if nav_items.count('NavigationItem(') != 4:
        errors.append('primary navigation must contain exactly four destinations')
    for token in expected:
        if token not in nav_items:
            errors.append(f'primary navigation destination missing: {token}')
    if 'Page.Display' in nav_items:
        errors.append('Display returned as a standalone primary navigation destination')

    bottom = section(text, 'private fun CompactBottomNavigationBar', '@Composable\nprivate fun ExploreDestinationTile')
    required_bottom = [
        '.widthIn(max = PrimaryNavigationMaxWidth)',
        '.height(PrimaryNavigationHeight)',
        '.height(PrimaryNavigationIndicatorHeight)',
        'indicatorStretch.animateTo(1.24f',
        'indicatorStretch.animateTo(0.96f',
        'indicatorStretch.animateTo(1f',
        'indicatorCenter.animateTo(',
        'durationMillis = 180',
        '.size(20.dp)',
        'fontSize = 9.sp',
        'role = Role.Tab',
        'bottom = PrimaryNavigationBottomGap',
    ]
    for token in required_bottom:
        if token not in bottom:
            errors.append(f'compact floating navigation contract missing: {token}')
    if 'navigationBottom +' in bottom or 'calculateBottomPadding()' in bottom:
        errors.append('floating navigation double-applies the system navigation bottom inset')
    if 'leftEdge' in bottom or 'rightEdge' in bottom:
        errors.append('unbounded two-edge indicator transition returned')
    if 'CompactNavigationRail' in text:
        errors.append('navigation rail reappeared')

    lazy = section(text, 'private fun VulkanLazyPage', '@Composable\nprivate fun ScrollBoundaryIndicators')
    clearance = 'PrimaryNavigationBottomGap + PrimaryNavigationHeight + PrimaryNavigationContentGap'
    if clearance not in lazy:
        errors.append('scroll content/hint clearance is not coupled to floating navigation geometry')
    if 'calculateBottomPadding() + PrimaryNavigation' in lazy:
        errors.append('scroll content double-applies system navigation bottom inset')

    status = section(text, 'private fun FloatingStatusSurface', '@Composable\nprivate fun UpdateDialogKeyValue')
    for token in ['widthIn(max = 520.dp)', 'RoundedCornerShape(22.dp)', 'shadowElevation = 8.dp']:
        if token not in status:
            errors.append(f'compact transient status contract missing: {token}')

    shell = section(text, 'CompositionLocalProvider(LocalValidatedNetwork provides validatedNetworkAvailable)', 'if (directUpdatesConsentVisible)')
    if shell.count('TransientStatusOverlayHost(') != 1:
        errors.append('transient status host count drifted')
    if 'modifier = Modifier.align(Alignment.TopCenter)' not in shell:
        errors.append('transient status overlay is no longer isolated from bottom navigation')

    startup = section(text, 'private fun completeOpeningSequence()', 'private fun persistOpeningAnimationPreference')
    for token in ['private fun armOpeningSequenceWatchdog()', 'delay(900L)', 'platformSplashExited = true', 'delay(2_100L)', 'completeOpeningSequence()']:
        if token not in startup:
            errors.append(f'startup watchdog contract missing: {token}')
    create_tail = section(text, 'override fun onCreate(savedInstanceState: Bundle?)', 'private fun showDirectUpdatesDisabledIntroIfFirstInstall')
    if 'armOpeningSequenceWatchdog()' not in create_tail:
        errors.append('startup watchdog is not armed from onCreate')

    forbidden = ['Whats' + 'App', 'Tele' + 'gram']
    for relative in ['app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'BUILD_AUDIT.md', 'changelog.md', 'rules/PROJECT_RULES.md']:
        path = root / relative
        if path.is_file():
            value = path.read_text(encoding='utf-8')
            if any(name in value for name in forbidden):
                errors.append(f'comparison-product name leaked into source/metadata: {relative}')
    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
    parser.add_argument('--skip-version', action='store_true')
    args = parser.parse_args()
    errors = verify(Path(args.root).resolve(), args.skip_version)
    if errors:
        for error in errors:
            print('FAIL', error)
        return 1
    print('VulkanScope 1.5.0 compact navigation and startup watchdog verifier: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
