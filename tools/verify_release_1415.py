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
    if not skip_version and ('versionCode = 1415' not in build_text or 'versionName = "1.4.15"' not in build_text):
        errors.append('release identity is not 1.4.15/1415')
    if not rules.is_file():
        errors.append('PROJECT_RULES.md missing')

    nav_items = section(text, 'private fun navigationItems()', 'private fun pageIcon')
    expected = [
        'NavigationItem(Page.Overview, "Overview", R.drawable.ic_home)',
        'NavigationItem(Page.Vulkan, "Vulkan", R.drawable.ic_vulkan)',
        'NavigationItem(Page.Surface, "Surface", R.drawable.ic_surface)',
        'NavigationItem(Page.Display, "Display", R.drawable.ic_tablet)',
        'NavigationItem(Page.Extensions, "Extensions", R.drawable.ic_extensions)',
    ]
    for token in expected:
        if token not in nav_items:
            errors.append(f'primary navigation destination missing: {token}')
    if nav_items.count('NavigationItem(') != 5:
        errors.append('primary navigation must expose exactly five destinations')

    shell = section(text, 'CompositionLocalProvider(LocalValidatedNetwork provides validatedNetworkAvailable)', 'if (directUpdatesConsentVisible)')
    if 'CompactNavigationRail(' in shell or 'useRail' in shell:
        errors.append('landscape/expanded layouts still route through a navigation rail')
    if shell.count('CompactBottomNavigationBar(') != 1:
        errors.append('shared bottom navigation must be overlaid exactly once for every orientation')
    if 'modifier = Modifier.align(Alignment.BottomCenter)' not in shell:
        errors.append('shared bottom navigation is not bottom-center overlaid')
    if 'bottomBar =' in shell:
        errors.append('navigation reverted to Scaffold bottomBar instead of overlaying content')

    if 'private fun CompactNavigationRail' in text:
        errors.append('obsolete navigation rail implementation remains in production source')

    bottom = section(text, 'private fun CompactBottomNavigationBar', '@Composable\nprivate fun ExploreDestinationTile')
    required_bottom = [
        '.widthIn(max = 352.dp)',
        '.height(62.dp)',
        'RoundedCornerShape(31.dp)',
        'val bottomOffset = navigationBottom + if (landscape) 16.dp else 6.dp',
        'val leftEdge = remember { androidx.compose.animation.core.Animatable',
        'val rightEdge = remember { androidx.compose.animation.core.Animatable',
        'delay(46)',
        'durationMillis = 92',
        'durationMillis = 118',
        '.offset(x = cellWidth * leftEdge.value)',
        '.width(cellWidth * (rightEdge.value - leftEdge.value).coerceAtLeast(0.01f))',
        'indicatorAnimating && index == previousActiveIndex',
        'animateColorAsState(',
        '.weight(1f)',
        'role = Role.Tab',
        'VulkanAccentContainer.copy(alpha = 0.88f)',
    ]
    for token in required_bottom:
        if token not in bottom:
            errors.append(f'floating navigation geometry/animation contract missing: {token}')
    if 'AnimatedNavigationIcon(' in bottom:
        errors.append('per-destination wiggle animation remains in shared navigation instead of the reference elastic indicator transition')
    if 'navigationBarsPadding()' in bottom:
        errors.append('side system-navigation inset can shift the centered landscape bar; only bottom inset may affect vertical placement')

    lazy = section(text, 'private fun VulkanLazyPage', '@Composable\nprivate fun ScrollBoundaryIndicators')
    for token in [
        'navigationOverlayClearance = navigationPadding.calculateBottomPadding() + if (landscape) 88.dp else 80.dp',
        'bottom = navigationOverlayClearance',
        'bottom = navigationOverlayClearance + 4.dp',
    ]:
        if token not in lazy:
            errors.append(f'page/scroll-arrow clearance contract missing: {token}')

    selection = section(text, 'private fun selectedNavigationPage', 'private fun navigationItems')
    if 'Page.Profiles' not in selection or '-> Page.Overview' not in selection:
        errors.append('Profiles no longer retains Overview as selected primary navigation')

    header = section(text, 'private fun SectionHeaderIcon', '@Composable\nprivate fun SectionVectorBadgeIcon')
    for token in ['title.equals("Opening animation", true)', 'R.drawable.ic_opening_animation_toggle', 'tint = VulkanAccentSoft', 'Modifier.size(20.dp)']:
        if token not in header:
            errors.append(f'opening-animation header icon contract missing: {token}')

    for relative in [
        'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',
        'BUILD_AUDIT.md',
        'changelog.md',
    ]:
        path = root / relative
        if path.is_file():
            value = path.read_text(encoding='utf-8')
            forbidden = ['Whats' + 'App', 'Tele' + 'gram']
            if any(name in value for name in forbidden):
                errors.append(f'comparison-product name leaked into release source/metadata: {relative}')
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
    print('VulkanScope 1.4.15 unified floating navigation verifier: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
