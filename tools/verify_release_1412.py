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
    if not main.is_file():
        return ['MainActivity.kt missing']
    text = main.read_text(encoding='utf-8')
    build_text = build.read_text(encoding='utf-8') if build.is_file() else ''
    if 'versionCode = 1412' not in build_text or 'versionName = "1.4.12"' not in build_text:
        errors.append('release identity is not 1.4.12/1412')

    html_helper = slice_between(text, 'private fun profileHtmlDetails', '@Composable\nprivate fun ProfilesPage')
    if not html_helper:
        errors.append('profileHtmlDetails helper missing')
    elif 'statusBadge(' in html_helper:
        errors.append('profile HTML helper references report-local statusBadge and would recreate the 9164 unresolved-reference failure')
    report_html = slice_between(text, 'private fun reportToHtml', 'private enum class SupportFilter')
    if 'fun statusBadge(value: String): String' not in report_html:
        errors.append('report-local HTML statusBadge helper missing')
    if '"${statusBadge(evaluation.status)} ${profileHtmlDetails(evaluation)}"' not in report_html:
        errors.append('profile HTML rows do not compose the local status badge with the global detail helper')

    nav_selection = slice_between(text, 'private fun selectedNavigationPage', '@Composable\nprivate fun AnimatedNavigationIcon')
    if 'Page.Profiles' not in nav_selection or '-> Page.Overview' not in nav_selection:
        errors.append('Profiles does not retain Overview as the selected primary navigation destination')

    bottom = slice_between(text, 'private fun CompactBottomNavigationBar', '@Composable\nprivate fun CompactNavigationRail')
    if not bottom:
        errors.append('compact bottom navigation missing')
    else:
        required = [
            'shape = RoundedCornerShape(31.dp)',
            'color = if (selected) VulkanAccentContainer else ComposeColor.Transparent',
            'navigationItems().forEach',
            'AnimatedNavigationIcon',
            'fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium',
        ]
        for token in required:
            if token not in bottom:
                errors.append(f'bottom navigation missing requested floating-tab treatment: {token}')
        if 'color = if (selected) ComposeColor(0xFF1E1516)' in bottom:
            errors.append('bottom navigation still paints a full selected item tile instead of icon-pill selection')

    rail = slice_between(text, 'private fun CompactNavigationRail', '@Composable\nprivate fun ExploreDestinationTile')
    if not rail:
        errors.append('compact navigation rail missing')
    else:
        for token in ['shape = RoundedCornerShape(34.dp)', 'color = if (selected) VulkanAccentContainer else ComposeColor.Transparent', 'AnimatedNavigationIcon']:
            if token not in rail:
                errors.append(f'navigation rail missing requested floating-tab treatment: {token}')
        if 'color = if (selected) ComposeColor(0xFF1E1516)' in rail:
            errors.append('navigation rail still paints a full selected item tile instead of icon-pill selection')

    header = slice_between(text, 'private fun SectionHeaderIcon', '@Composable\nprivate fun SectionVectorBadgeIcon')
    if 'title.equals("Opening animation", true)' not in header:
        errors.append('Opening animation header icon specialization missing')
    if 'painter = painterResource(R.drawable.ic_opening_animation_toggle)' not in header:
        errors.append('Opening animation header is not using the supplied animation glyph')
    if 'tint = VulkanAccentSoft' not in header or 'modifier = Modifier.size(20.dp)' not in header:
        errors.append('Opening animation glyph is not normalized to the VulkanScope header tint/size treatment')

    tech_json = slice_between(text, 'private fun technicalReportJson', 'private fun databaseSubmissionJson')
    if 'put("profileEvaluation"' not in tech_json or 'profileEvaluationJson(profile)' not in tech_json:
        errors.append('technical JSON does not retain detailed profile evaluation objects')
    profile_json = slice_between(text, 'private fun profileEvaluationJson', 'private fun profileHtmlDetails')
    detail_keys = [
        'checkedRequirementCount', 'missingExtensions', 'unknownExtensions', 'missingFeatures', 'unknownFeatures',
        'failingLimits', 'unknownLimits', 'failingFormats', 'unknownFormats', 'failingRequirementGroups', 'unknownRequirementGroups'
    ]
    for key in detail_keys:
        if f'put("{key}"' not in profile_json:
            errors.append(f'profile JSON detail missing: {key}')

    txt = slice_between(text, 'private fun reportToText', 'private fun htmlEscape')
    if 'profileDetailPairs(p).forEach' not in txt:
        errors.append('TXT report does not emit detailed profile requirement evidence')
    if 'profileHtmlDetails(evaluation)' not in report_html:
        errors.append('HTML report does not emit detailed profile requirement evidence')

    database = slice_between(text, 'private fun databaseSubmissionJson', 'private data class DatabaseSubmissionResult')
    if 'put("technicalReport", technicalReportJson(context, report, display, mode))' not in database:
        errors.append('Database submission no longer carries the detailed technicalReport profile evaluation')
    if 'put("reportText", reportToText(context, report, display, mode))' not in database:
        errors.append('Database submission no longer carries the detailed TXT-equivalent profile evidence')

    profiles_page = slice_between(text, 'private fun ProfilesPage', 'private data class LibraryVersionInfo')
    for token in ['"Visible" to filtered.size.toString()', '"Pass" to filtered.count', '"Fail" to filtered.count', '"Unknown" to filtered.count']:
        if token not in profiles_page:
            errors.append(f'Profiles UI summary missing: {token}')
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
    print('VulkanScope 1.4.12 compile/navigation/profile-report verifier: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
