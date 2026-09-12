#!/usr/bin/env python3
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

need('versionCode = 1201' in gradle and 'versionName = "1.2.1"' in gradle, '1.2.1 release identity missing')
settings_block = main[main.find('private enum class SettingsSection'):main.find('private enum class DriverMode')]
need(settings_block.find('INFO("Info"') < settings_block.find('REPORTS("Reports & Database"') < settings_block.find('DRIVER_UPDATES("Driver & Update Preferences"'), 'Settings sections are not ordered Info, Reports & Database, Driver & Update Preferences')
need('DRIVER_UPDATES("Driver & Update Preferences", "Vulkan driver management, built-in GitHub update preference and Obtainium guidance."' in settings_block, 'Driver/update Settings destination label or explanatory copy missing')
need('initialSection: SettingsSection = SettingsSection.DRIVER_UPDATES' in main, 'Settings does not default to Driver & Update Preferences')
need('CapabilitySectionCard("Update preferences")' in main, 'update preference content is not grouped under the third Settings destination')
need('private var updateCheckInFlight by mutableStateOf(false)' in main, 'dedicated update-check in-flight state missing')
need('updateStatusHideJob?.cancel()\n        updateStatusHideJob = null\n        updateCheckInFlight = true\n        updateCheckJob = activityScope.launch' in main, 'update check does not cancel stale feedback and become busy before asynchronous launch')
need('val result = try {\n                withContext(Dispatchers.IO) { fetchLatestCompatibleUpdateResult() }\n            } finally {\n                updateCheckInFlight = false\n            }' in main, 'update-check busy state is not cleared immediately after result/cancellation')
need('updateCheckInFlight: Boolean' in main, 'update-check busy state is not propagated to Compose')
need('updateCheckInFlight = updateCheckInFlight' in main, 'update-check busy state is not passed into the UI')
need('!updateCheckInFlight -> "Official EFIShell0/VulkanScope GitHub release channel"' in main and 'else -> "Checking official release channel…"' in main, 'Check for updates does not expose its disabled checking state')
need('enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight' in main, 'Check for updates remains enabled while a result is pending')
need('if (updateCheckInFlight || updateStatus is UpdateStatus.Downloading) return' in main, 'duplicate update checks are not guarded by explicit in-flight state')
need('private var updateStatusHideJob: Job? = null' in main and 'updateStatus == displayedStatus' in main, 'result feedback is not isolated from subsequent update checks')
need('SettingsSection.entries.forEach' in main and 'Open ${section.label}' in main, 'shared expressive Settings destination-card interaction drifted')
need('showReporting = false' in main and 'showInfo = false' in main, 'Info and Reports/Database Settings destinations lost content separation')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.2.1 Settings/update-check contract')
