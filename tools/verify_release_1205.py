#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def between(start, end):
    a = main.find(start)
    b = main.find(end, a + 1) if a >= 0 else -1
    return main[a:b] if a >= 0 and b > a else ''

if not args.skip_version:
    need('versionCode = 1205' in gradle and 'versionName = "1.2.5"' in gradle, '1.2.5 release identity missing')
need(re.search(r'\bminSdk\s*=\s*31\b', gradle) is not None, 'Android 12 minimum drifted')
settings_page = between('private fun SettingsPage(', '@Composable\nprivate fun DriverUpdatePreferencesPage')
need('AnimatedContent(' in settings_page, 'Settings destination navigation is not animated')
need('targetState = selectedSection' in settings_page, 'Settings destination transition is not driven by selectedSection')
need('modifier = Modifier.fillMaxSize()' in settings_page, 'Settings destination transition no longer owns the destination viewport')
need('initialState == null && targetState != null' in settings_page, 'chooser-to-destination transition branch missing')
need('slideInHorizontally(tween(durationMillis = 280)) { it / 8 }' in settings_page, 'destination entrance slide missing or unbounded')
need('fadeIn(tween(durationMillis = 220))' in settings_page, 'destination entrance fade missing or unbounded')
need('slideOutHorizontally(tween(durationMillis = 180)) { -it / 12 }' in settings_page, 'chooser exit slide missing or unbounded')
need('fadeOut(tween(durationMillis = 150))' in settings_page, 'chooser exit fade missing or unbounded')
need('initialState != null && targetState == null' in settings_page, 'destination-to-chooser transition branch missing')
need('label = "settingsSectionTransition"' in settings_page, 'Settings destination transition label missing')
need('when (targetSection)' in settings_page, 'AnimatedContent target value is not consumed by destination rendering')
need('SettingsSection.INFO -> InfoPage(' in settings_page, 'Info destination is outside the Settings transition')
need('SettingsSection.REPORTS -> InfoPage(' in settings_page, 'Reports destination is outside the Settings transition')
need('SettingsSection.DRIVER_UPDATES -> DriverUpdatePreferencesPage(' in settings_page, 'Driver/update destination is outside the Settings transition')
need('if (selectedSection == SettingsSection.INFO)' not in settings_page, 'abrupt pre-transition Settings Info branch remains')
need('if (selectedSection == SettingsSection.REPORTS)' not in settings_page, 'abrupt pre-transition Settings Reports branch remains')
driver_page = between('private fun DriverUpdatePreferencesPage(', '@Composable\nprivate fun SystemDriverManagerRow')
need('CapabilitySectionCard("Update preferences")' in driver_page, 'Driver/update content was lost during transition extraction')
need('CapabilitySectionCard("Driver manager")' in driver_page, 'Driver manager content was lost during transition extraction')
settings_cards = between('private fun SettingsSectionCards(', '@Composable\nprivate fun DatabaseSubmissionFailureDialog')
need('fadeIn(tween(durationMillis = 260, delayMillis = index * 45))' in settings_cards, 'existing chooser-card entrance motion drifted')
need('ExpressiveDestinationCard(section.label, section.description, section.icon)' in settings_cards, 'Settings chooser stopped using shared expressive destination cards')
need('OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' in main, 'Database endpoint drifted')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.2.5 Settings destination transition contract')
