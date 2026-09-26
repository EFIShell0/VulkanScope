#!/usr/bin/env python3
import argparse
from pathlib import Path


def section(text: str, start: str, end: str) -> str:
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    return text[a:b] if a >= 0 and b > a else ''


def verify(root: Path, skip_version: bool = False) -> list[str]:
    errors: list[str] = []
    main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    build = root / 'app/build.gradle.kts'
    rules = root / 'rules/PROJECT_RULES.md'
    if not main.is_file():
        return ['MainActivity.kt missing']
    text = main.read_text(encoding='utf-8')
    build_text = build.read_text(encoding='utf-8') if build.is_file() else ''
    if not skip_version and ('versionCode = 1501' not in build_text or 'versionName = "1.5.1"' not in build_text):
        errors.append('release identity is not 1.5.1/1501')
    if not rules.is_file() or '## Release 1.5.1 profile evidence, reference-sized navigation and opening refinement requirements' not in rules.read_text(encoding='utf-8'):
        errors.append('1.5.1 release rules are missing')

    constants = section(text, 'private val VulkanOutlineVariant', 'private val LocalDetailKeyValuePresentation')
    for token in [
        'PrimaryNavigationMaxWidth = 310.dp',
        'PrimaryNavigationHeight = 54.dp',
        'PrimaryNavigationIndicatorHeight = 42.dp',
        'PrimaryNavigationIndicatorHorizontalInset = 8.dp',
        'PrimaryNavigationBottomGap = 4.dp',
        'PrimaryNavigationContentGap = 10.dp',
    ]:
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

    scaffold = section(text, 'Scaffold(', 'TransientStatusOverlayHost(')
    if 'contentWindowInsets = WindowInsets(0, 0, 0, 0)' not in scaffold:
        errors.append('Scaffold duplicate safe-drawing inset suppression is missing')

    bottom = section(text, 'private fun CompactBottomNavigationBar', '@Composable\nprivate fun ExploreDestinationTile')
    for token in [
        '.widthIn(max = PrimaryNavigationMaxWidth)',
        '.height(PrimaryNavigationHeight)',
        '.height(PrimaryNavigationIndicatorHeight)',
        '.padding(horizontal = PrimaryNavigationIndicatorHorizontalInset)',
        'MutableInteractionSource()',
        'interactionSource = interactionSource',
        'indication = null',
        'role = Role.Tab',
        '.clip(RoundedCornerShape(999.dp))',
        '.size(20.dp)',
        'fontSize = 9.sp',
        'bottom = PrimaryNavigationBottomGap',
        'targetValue = if (selected) 1f else 0.86f',
    ]:
        if token not in bottom:
            errors.append(f'local compact navigation contract missing: {token}')
    for forbidden in ['indicatorCenter', 'indicatorStretch', 'Animatable(', '.offset {', '.offset(']:
        if forbidden in bottom:
            errors.append(f'moving/global selected indicator returned: {forbidden}')
    if 'calculateBottomPadding()' in bottom or 'WindowInsets.navigationBars' in bottom:
        errors.append('floating navigation applies a second platform navigation inset')
    if 'CompactNavigationRail' in text:
        errors.append('navigation rail reappeared')

    lazy = section(text, 'private fun VulkanLazyPage', '@Composable\nprivate fun ScrollBoundaryIndicators')
    if 'PrimaryNavigationBottomGap + PrimaryNavigationHeight + PrimaryNavigationContentGap' not in lazy:
        errors.append('scroll content/hint clearance is not coupled to navigation geometry')
    if 'calculateBottomPadding() + PrimaryNavigation' in lazy:
        errors.append('scroll content double-applies platform navigation inset')

    opening = section(text, 'private fun VulkanScopeOpeningAnimation', '@Composable\nprivate fun VulkanScopeApp')
    for token in [
        'Brush.verticalGradient(',
        'Brush.radialGradient(',
        'openingLogoScale',
        'openingHaloAlpha',
        'openingLineScale',
        'openingAccentPulse',
        'delay(520L)',
        'delay(500L)',
        'delay(300L)',
        'delay(280L)',
        '.size(4.dp)',
        'onFinished()',
    ]:
        if token not in opening:
            errors.append(f'opening refinement contract missing: {token}')
    if 'Surface(' in opening or 'Card(' in opening:
        errors.append('opening animation returned to a large panel/card composition')

    startup = section(text, 'private fun completeOpeningSequence()', 'private fun persistOpeningAnimationPreference')
    for token in ['private fun armOpeningSequenceWatchdog()', 'delay(900L)', 'platformSplashExited = true', 'delay(2_100L)', 'completeOpeningSequence()']:
        if token not in startup:
            errors.append(f'startup watchdog contract missing: {token}')

    profiles = section(text, 'private data class ProfileCheckCounts', 'private val PROFILE_INSTANCE_EXTENSIONS')
    for token in [
        'val total: Int = 0',
        'val met: Int = 0',
        'val failed: Int = 0',
        'val unknown: Int = 0',
        'val metRequirementCount: Int = 0',
        'val failedRequirementCount: Int = 0',
        'val unknownRequirementCount: Int = 0',
        'val extensionChecks: ProfileCheckCounts',
        'val featureChecks: ProfileCheckCounts',
        'val propertyChecks: ProfileCheckCounts',
        'val formatChecks: ProfileCheckCounts',
        'val groupChecks: ProfileCheckCounts',
        'fun record(state: String)',
    ]:
        if token not in profiles:
            errors.append(f'profile tally model missing: {token}')

    capability = section(text, 'private fun evaluateProfileCapability', 'private fun profileEvidenceState')
    for token in [
        'recordCheck(evidence.extensionChecks, "PASS")',
        'if (complete) evidence.missingExtensions += extension else evidence.unknownExtensions += extension',
        'recordCheck(evidence.extensionChecks, if (complete) "FAIL" else "UNKNOWN")',
        'recordCheck(evidence.featureChecks, "PASS")',
        'recordCheck(evidence.featureChecks, "FAIL")',
        'recordCheck(evidence.featureChecks, "UNKNOWN")',
        'recordCheck(evidence.propertyChecks, state.first)',
        'recordCheck(evidence.formatChecks, "PASS")',
        'recordCheck(evidence.formatChecks, "FAIL")',
        'recordCheck(evidence.formatChecks, "UNKNOWN")',
    ]:
        if token not in capability:
            errors.append(f'profile evidence tally path missing: {token}')

    evaluator = section(text, 'private fun evaluateProfile(report:', 'private fun vulkanProfileEvaluations')
    for token in [
        'checkedRequirementCount = total',
        'unknownRequirementCount = total',
        'evidence.recordCheck(evidence.groupChecks, groupState)',
        'selectedAlternativeIndex',
        'metRequirementCount = evidence.totalChecks.met',
        'failedRequirementCount = evidence.totalChecks.failed',
        'unknownRequirementCount = evidence.totalChecks.unknown',
    ]:
        if token not in evaluator:
            errors.append(f'profile aggregate tally path missing: {token}')

    reporting = section(text, 'private fun profileCheckSummary', '@Composable\nprivate fun ProfilesPage')
    for token in [
        '${counts.met}/${counts.total} met',
        '${counts.failed} unmet',
        '${counts.unknown} unknown',
        'put("metRequirementCount", profile.metRequirementCount)',
        'put("failedRequirementCount", profile.failedRequirementCount)',
        'put("unknownRequirementCount", profile.unknownRequirementCount)',
        'put("checkBreakdown"',
        'putCounts("extensions"',
        'putCounts("features"',
        'putCounts("properties"',
        'putCounts("formats"',
        'putCounts("inheritedProfiles"',
        'private fun ProfileCheckMetricGrid',
        'private fun ProfileCheckBreakdown',
        'CapabilityKeyValue("Extensions"',
        'CapabilityKeyValue("Features"',
        'CapabilityKeyValue("Properties / limits"',
        'CapabilityKeyValue("Formats"',
        'CapabilityKeyValue("Inherited profiles"',
    ]:
        if token not in reporting:
            errors.append(f'profile reporting contract missing: {token}')

    profile_page = section(text, 'private fun ProfilesPage', '@Composable\nprivate fun InfoPage')
    for token in [
        'visibleMappedChecks',
        'visibleMetChecks',
        'visibleFailedChecks',
        'visibleUnknownChecks',
        'Mapped requirement checks across visible profiles',
        'ProfileCheckMetricGrid(',
        'ProfileCheckBreakdown(result)',
    ]:
        if token not in profile_page:
            errors.append(f'profile UI accounting contract missing: {token}')

    forbidden = ['Whats' + 'App', 'Tele' + 'gram']
    for relative in [
        'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',
        'BUILD_AUDIT.md',
        'changelog.md',
        'rules/PROJECT_RULES.md',
        'rules/1.5.1_PROFILE_NAVIGATION_OPENING_AUDIT.md',
    ]:
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
    print('VulkanScope 1.5.1 profile/navigation/opening verifier: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
