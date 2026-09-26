#!/usr/bin/env python3
import argparse
from pathlib import Path


def slice_between(text: str, start: str, end: str) -> str:
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    return text[a:b] if a >= 0 and b > a else ''


def verify(root: Path) -> list[str]:
    errors = []
    main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    build = root / 'app/build.gradle.kts'
    rules = root / 'rules/PROJECT_RULES.md'
    if not main.is_file():
        return ['MainActivity.kt missing']
    text = main.read_text(encoding='utf-8')
    build_text = build.read_text(encoding='utf-8') if build.is_file() else ''
    if 'versionCode = 1413' not in build_text or 'versionName = "1.4.13"' not in build_text:
        errors.append('release identity is not 1.4.13/1413')
    if not rules.is_file():
        errors.append('PROJECT_RULES.md missing')

    bottom = slice_between(text, 'private fun CompactBottomNavigationBar', '@Composable\nprivate fun CompactNavigationRail')
    required_bottom = [
        'ShortNavigationBar(',
        'ShortNavigationBarItem(',
        'NavigationItemIconPosition.Top',
        'windowInsets = WindowInsets(0, 0, 0, 0)',
        'shape = RoundedCornerShape(28.dp)',
        'navigationBarsPadding()',
        'size = 24.dp',
    ]
    for token in required_bottom:
        if token not in bottom:
            errors.append(f'compact bottom navigation missing researched navigation contract: {token}')
    if 'Surface(\n                                shape = RoundedCornerShape(999.dp)' in bottom:
        errors.append('compact bottom navigation still hand-builds the selected indicator instead of using the Material navigation item indicator')
    if '.clickable {' in bottom:
        errors.append('compact bottom navigation bypasses ShortNavigationBarItem selection semantics with a manual clickable item')

    rail = slice_between(text, 'private fun CompactNavigationRail', '@Composable\nprivate fun ExploreDestinationTile')
    required_rail = [
        'NavigationRail(',
        'NavigationRailItem(',
        'alwaysShowLabel = true',
        'NavigationRailItemDefaults.colors(',
        'indicatorColor = VulkanAccentContainer',
        'shape = RoundedCornerShape(28.dp)',
        'desktopVerticalPointerScroll',
    ]
    for token in required_rail:
        if token not in rail:
            errors.append(f'navigation rail missing researched navigation contract: {token}')
    if 'Surface(\n                                shape = RoundedCornerShape(999.dp)' in rail:
        errors.append('navigation rail still hand-builds the selected indicator instead of using NavigationRailItem')

    selection = slice_between(text, 'private fun selectedNavigationPage', '@Composable\nprivate fun AnimatedNavigationIcon')
    if 'Page.Profiles' not in selection or '-> Page.Overview' not in selection:
        errors.append('Profiles no longer retains Overview as the selected primary destination')

    header = slice_between(text, 'private fun SectionHeaderIcon', '@Composable\nprivate fun SectionVectorBadgeIcon')
    for token in ['title.equals("Opening animation", true)', 'R.drawable.ic_opening_animation_toggle', 'tint = VulkanAccentSoft', 'Modifier.size(20.dp)']:
        if token not in header:
            errors.append(f'opening animation header treatment missing: {token}')

    html_helper = slice_between(text, 'private fun profileHtmlDetails', '@Composable\nprivate fun ProfilesPage')
    if 'statusBadge(' in html_helper:
        errors.append('profile HTML helper recreates report-local statusBadge scope regression')
    tech_json = slice_between(text, 'private fun technicalReportJson', 'private fun databaseSubmissionJson')
    if 'profileEvaluationJson(profile)' not in tech_json:
        errors.append('detailed profile evaluation is missing from technical JSON')
    txt = slice_between(text, 'private fun reportToText', 'private fun htmlEscape')
    if 'profileDetailPairs(p).forEach' not in txt:
        errors.append('detailed profile evaluation is missing from TXT')
    report_html = slice_between(text, 'private fun reportToHtml', 'private enum class SupportFilter')
    if 'profileHtmlDetails(evaluation)' not in report_html:
        errors.append('detailed profile evaluation is missing from HTML')
    database = slice_between(text, 'private fun databaseSubmissionJson', 'private data class DatabaseSubmissionResult')
    if 'technicalReportJson(context, report, display, mode)' not in database or 'reportToText(context, report, display, mode)' not in database:
        errors.append('Database payload no longer retains complete profile-bearing report surfaces')

    forbidden_names = ['Whats' + 'App', 'Tele' + 'gram']
    for relative in ['app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'BUILD_AUDIT.md', 'changelog.md', 'rules/PROJECT_RULES.md']:
        path = root / relative
        if path.is_file():
            value = path.read_text(encoding='utf-8')
            for name in forbidden_names:
                if name in value:
                    errors.append(f'forbidden third-party comparison-product name leaked into packaged source/metadata: {relative}')
                    break
    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
    args = parser.parse_args()
    errors = verify(Path(args.root).resolve())
    if errors:
        for error in errors:
            print('FAIL', error)
        return 1
    print('VulkanScope 1.4.13 researched navigation contract verifier: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
