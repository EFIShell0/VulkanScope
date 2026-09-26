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
    if not skip_version and ('versionCode = 1416' not in build_text or 'versionName = "1.4.16"' not in build_text):
        errors.append('release identity is not 1.4.16/1416')
    if not rules.is_file():
        errors.append('PROJECT_RULES.md missing')

    nav_items = section(text, 'private fun navigationItems()', 'private fun pageIcon')
    expected = [
        'NavigationItem(Page.Overview, "Overview", R.drawable.ic_home)',
        'NavigationItem(Page.Vulkan, "Vulkan", R.drawable.ic_vulkan)',
        'NavigationItem(Page.Surface, "Surface", R.drawable.ic_surface)',
        'NavigationItem(Page.Extensions, "Extensions", R.drawable.ic_extensions)',
    ]
    for token in expected:
        if token not in nav_items:
            errors.append(f'primary navigation destination missing: {token}')
    if nav_items.count('NavigationItem(') != 4:
        errors.append('primary navigation must expose exactly four destinations')
    if 'Page.Display' in nav_items:
        errors.append('Display remains a standalone primary navigation destination')

    selection = section(text, 'private fun selectedNavigationPage', 'private fun navigationItems')
    if 'Page.Display -> Page.Surface' not in selection:
        errors.append('Display no longer maps to Surface as the selected primary destination')
    if 'Page.Profiles' not in selection or '-> Page.Overview' not in selection:
        errors.append('Profiles no longer retains Overview as selected primary navigation')

    bottom = section(text, 'private fun CompactBottomNavigationBar', '@Composable\nprivate fun ExploreDestinationTile')
    for token in [
        '.widthIn(max = 352.dp)',
        '.height(62.dp)',
        'val indicatorInset = 3.dp',
        '.height(54.dp)',
        '.clip(RoundedCornerShape(999.dp))',
        'delay(46)',
        'durationMillis = 92',
        'durationMillis = 118',
        'role = Role.Tab',
    ]:
        if token not in bottom:
            errors.append(f'four-tab capsule navigation contract missing: {token}')
    if 'Page.Display' in bottom:
        errors.append('Display-specific primary tab logic leaked into bottom navigation')

    shell = section(text, 'CompositionLocalProvider(LocalValidatedNetwork provides validatedNetworkAvailable)', 'if (directUpdatesConsentVisible)')
    if shell.count('TransientStatusOverlayHost(') != 1:
        errors.append('transient status banners are not hosted exactly once as an overlay')
    if 'modifier = Modifier.align(Alignment.TopCenter)' not in shell:
        errors.append('transient status overlay is not top-center aligned')
    topbar = section(shell, 'topBar = {', ')\n                }\n            ) { padding')
    for token in ['CollectionStatusBanner(', 'ConnectivityStatusHost(', 'UpdateStatusBanner(']:
        if token in topbar:
            errors.append(f'transient status banner still participates in top-bar layout: {token}')

    status = section(text, 'private fun FloatingStatusSurface', '@Composable\nprivate fun UpdateDialogKeyValue')
    for token in [
        'ComposeColor(0xD61A1A1F)',
        'RoundedCornerShape(28.dp)',
        'widthIn(max = 680.dp)',
        'CollectionStatusBanner(collectionStatus)',
        'ConnectivityStatusHost(collectionStatus, networkStateKnown, networkAvailable, networkBannerState)',
        'UpdateStatusBanner(updateStatus, onInstallUpdate)',
    ]:
        if token not in status:
            errors.append(f'floating transient-status contract missing: {token}')

    surface_enum = section(text, 'private enum class SurfaceSection', 'private enum class DriverMode')
    order = [
        'DISPLAY("Display & HDR"',
        'SURFACE_FORMATS("Surface & color spaces"',
        'PRESENTATION("Presentation"',
    ]
    positions = [surface_enum.find(token) for token in order]
    if any(pos < 0 for pos in positions) or positions != sorted(positions):
        errors.append('Surface destination order is not Display, Surface/color spaces, Presentation')

    surface_page = section(text, 'private fun SurfaceDestinationPage', 'private fun buildSurfaceCatalog')
    for token in [
        'null -> SurfaceSectionCards',
        'SurfaceSection.DISPLAY -> DisplayPage(display, device)',
        'SurfaceSection.SURFACE_FORMATS -> SurfaceFormatsPage(device)',
        'SurfaceSection.PRESENTATION -> SurfacePresentationPage(device)',
    ]:
        if token not in surface_page:
            errors.append(f'Surface nested destination contract missing: {token}')

    app_state = section(text, 'var page by rememberSaveable', 'MaterialExpressiveTheme(')
    if 'var surfaceSection by rememberSaveable' not in app_state:
        errors.append('Surface nested destination state is not saveable')
    back = section(text, 'BackHandler(', 'CompositionLocalProvider(LocalValidatedNetwork')
    if 'page == Page.Surface && surfaceSection != null -> surfaceSection = null' not in back:
        errors.append('Back does not return Surface subsection to the Surface chooser')

    if 'private fun CompactNavigationRail' in text:
        errors.append('navigation rail reappeared')

    forbidden = ['Whats' + 'App', 'Tele' + 'gram']
    for relative in ['app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'BUILD_AUDIT.md', 'changelog.md']:
        path = root / relative
        if path.is_file():
            value = path.read_text(encoding='utf-8')
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
    print('VulkanScope 1.4.16 Surface navigation and floating status verifier: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
