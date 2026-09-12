import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument("--root")
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
native = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

need('versionCode = 1200' in gradle and 'versionName = "1.2.0"' in gradle, '1.2.0 release identity missing')
need('const uint32_t vendorId = properties.vendorID' in native and 'const std::string deviceName(properties.deviceName)' in native, 'base GPU identity is not sourced from VkPhysicalDeviceProperties')
need('getDevicePropertiesPrimary(api, devices[i], physicalProperties)' in native and 'physicalProperties.vendorID' in native and 'physicalProperties.deviceName' in native, 'detail GPU identity path does not use Vulkan physical-device properties')
need('GPU name, vendor ID and device ID are read from VkPhysicalDeviceProperties' in main, 'Overview GPU provenance explanation missing')
need('SystemDriverDetailsDialog(systemDriverSummary, "Current Vulkan report", true)' in main and 'TurnipDriverDetailsDialog(activeTurnipDriver!!)' in main, 'Overview Details does not reuse Driver manager detail dialogs')
need('private enum class SettingsSection' in main and 'INFO("Info"' in main and 'REPORTS("Reports & Database"' in main, 'Settings nested section model missing')
need('Page.Info -> SettingsPage(' in main and 'initialSection = SettingsSection.INFO' in main, 'Info destination is not nested under Settings')
need('showReporting = false' in main and 'showInfo = false' in main, 'Info/report content separation missing')
need('ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete' in main, 'Watch/driver remove action is not boxed with trash icon')
need('labels.firstOrNull()?.equals("All", true) == true' in main and 'ExpressiveSwitch(checked = allEnabled' in main, 'All filter switch missing')
need('enabled = !allEnabled' in main and 'specific filters are locked' in main, 'specific filters are not locked while All is enabled')
need('private fun TransientActionButton' in main and 'delay(3000)' in main, 'three-second transient action state missing')
need('2 -> R.drawable.ic_check' in main and '3 -> R.drawable.ic_close' in main, 'success/failure action glyphs missing')
need('ComposeColor(0xFF73C991)' in main and 'ComposeColor(0xFFFF6B6B)' in main, 'green success/red failure colors missing')
need('AnimatedContent(targetState = trailingIcon' in main, 'action result icon animation missing')
need('TransientActionButton("Copy name + value"' in main and 'idleTrailingIcon = R.drawable.ic_add' in main, 'evidence copy/watch transient controls missing')
need('color = VulkanAccentContainer.copy(alpha = 0.96f)' in main and 'tint = VulkanTextPrimary' in main, 'page scroll arrows are not white on Vulkan red')
need('Copy permalink to clipboard' in main and 'VulkanQrCode' in main and 'RoundedCornerShape(18.dp)' in main, 'QR presentation/copy action contract missing')
need('idleTrailingIcon = R.drawable.ic_upload' in main, 'Database submit upload glyph missing')
need('R.drawable.ic_receive' in main, 'update receive glyph missing')
need('reports from older app versions are rejected by the server' in main and 'submissionState' in main, 'Database compatibility/rejection notice missing')
need('title.equals("VkSurfaceKHR", true)' in main and 'Text("KHR"' in main, 'VkSurfaceKHR KHR badge missing')
need('title.equals("Search surface formats / color spaces", true)' in main and 'R.drawable.ic_search' in main, 'surface-format search magnifier badge missing')
need('title.equals("HDR capabilities", true)' in main and 'DisplaySectionBadgeIcon("HDR")' in main, 'HDR icon contract missing')
need('private fun DisplayPage' in main and 'VulkanLazyPage' in main, 'Display/HDR page does not use shared scroll indicator host')
need('finally {\n                            submissionInFlight = false' in main, 'Database submission in-flight cleanup missing')
need('Vulkan 1.4.362' in main and 'vulkanRegistryVersion' in native and '1.4.362' in native, 'Vulkan 1.4.362 baseline drifted')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.2.0 release/UI/reporting contract')
