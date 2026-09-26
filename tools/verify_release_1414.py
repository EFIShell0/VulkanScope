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
    if not skip_version and ('versionCode = 1414' not in build_text or 'versionName = "1.4.14"' not in build_text):
        errors.append('release identity is not 1.4.14/1414')
    if not rules.is_file():
        errors.append('PROJECT_RULES.md missing')

    nav_items = section(text, 'private fun navigationItems()', 'private fun pageIcon')
    for token in [
        'NavigationItem(Page.Overview, "Overview", R.drawable.ic_home)',
        'NavigationItem(Page.Vulkan, "Vulkan", R.drawable.ic_vulkan)',
        'NavigationItem(Page.Surface, "Surface", R.drawable.ic_surface)',
        'NavigationItem(Page.Display, "Display", R.drawable.ic_tablet)',
        'NavigationItem(Page.Extensions, "Extensions", R.drawable.ic_extensions)',
    ]:
        if token not in nav_items:
            errors.append(f'primary navigation destination missing: {token}')
    if nav_items.count('NavigationItem(') != 5:
        errors.append('primary navigation must expose exactly five visible destinations')

    bottom = section(text, 'private fun CompactBottomNavigationBar', '@Composable\nprivate fun CompactNavigationRail')
    for token in [
        'ComposeColor(0xCC17171B)',
        'RoundedCornerShape(32.dp)',
        '.padding(start = 20.dp, end = 20.dp, bottom = 7.dp)',
        '.height(64.dp)',
        '.weight(1f)',
        '.selectable(',
        'role = Role.Tab',
        'navigationItems().forEach',
        'VulkanAccentContainer.copy(alpha = 0.84f)',
    ]:
        if token not in bottom:
            errors.append(f'floating bottom navigation contract missing: {token}')
    if 'ShortNavigationBar(' in bottom or 'ShortNavigationBarItem(' in bottom:
        errors.append('floating bottom navigation reverted to the predecessor component path that dropped visible destinations on the reported device')

    shell = section(text, 'CompositionLocalProvider(LocalValidatedNetwork provides validatedNetworkAvailable)', 'if (directUpdatesConsentVisible)')
    if 'bottomBar =' in shell:
        errors.append('floating bottom navigation is still laid out as a Scaffold bottomBar and therefore cannot overlay scrolling page content')
    if 'modifier = Modifier.align(Alignment.BottomCenter)' not in shell:
        errors.append('floating bottom navigation is not overlaid at the bottom center')

    lazy = section(text, 'private fun VulkanLazyPage', '@Composable\nprivate fun ScrollBoundaryIndicators')
    for token in ['bottomContentPadding', 'if (useRail) 0.dp else 82.dp', 'bottom = bottomContentPadding']:
        if token not in lazy:
            errors.append(f'scroll-behind safe-area contract missing: {token}')

    rail = section(text, 'private fun CompactNavigationRail', '@Composable\nprivate fun ExploreDestinationTile')
    for token in [
        'ComposeColor(0xCC17171B)',
        '.width(86.dp)',
        '.height(62.dp)',
        '.selectable(',
        'role = Role.Tab',
        'navigationItems().forEachIndexed',
        'VulkanAccentContainer.copy(alpha = 0.84f)',
        'desktopVerticalPointerScroll',
    ]:
        if token not in rail:
            errors.append(f'floating navigation rail contract missing: {token}')

    selection = section(text, 'private fun selectedNavigationPage', '@Composable\nprivate fun AnimatedNavigationIcon')
    if 'Page.Profiles' not in selection or '-> Page.Overview' not in selection:
        errors.append('Profiles no longer retains Overview as the selected primary destination')

    header = section(text, 'private fun SectionHeaderIcon', '@Composable\nprivate fun SectionVectorBadgeIcon')
    for token in ['title.equals("Opening animation", true)', 'R.drawable.ic_opening_animation_toggle', 'tint = VulkanAccentSoft', 'Modifier.size(20.dp)']:
        if token not in header:
            errors.append(f'opening animation header treatment missing: {token}')

    for relative in ['app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'BUILD_AUDIT.md', 'changelog.md', 'rules/PROJECT_RULES.md']:
        path = root / relative
        if path.is_file():
            value = path.read_text(encoding='utf-8')
            for name in ['Whats' + 'App', 'Tele' + 'gram']:
                if name in value:
                    errors.append(f'forbidden comparison-product name leaked into packaged source/metadata: {relative}')
                    break
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
    print('VulkanScope 1.4.14 floating navigation verifier: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
