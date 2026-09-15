#!/usr/bin/env python3
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
icon_path = root / 'app/src/main/res/drawable/ic_expand_more.xml'
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

if not args.skip_version:
    need('versionCode = 1301' in gradle and 'versionName = "1.3.1"' in gradle, '1.3.1 release identity missing')
need('private fun ExpressiveSingleFilterSelector(' in main, 'single-select dropdown selector missing')
need('val pageSize = 50' in main, 'filter page size is not bounded to 50')
need('label.contains(query, ignoreCase = true)' in main, 'case-insensitive filter search missing')
need('labels.mapIndexed { index, label -> index to label }' in main, 'filtered choices do not retain original callback indices')
need('indexed.drop(page * pageSize).take(pageSize)' in main, 'paged filter slice missing')
need('cleaned.toIntOrNull()?.let { requested ->' in main and 'if (requested in 1..pageCount) page = requested - 1' in main, 'direct bounded page-number jump missing')
need('contentDescription = "Previous filter page"' in main, 'previous filter page control missing')
need('contentDescription = "Next filter page"' in main, 'next filter page control missing')
need('PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true)' in main, 'accessible dismissible dropdown popup contract missing')
need('label = { Text("Search filters") }' in main, 'filter search field missing')
need('"${indexed.size} result${if (indexed.size == 1) "" else "s"} · up to $pageSize per page"' in main, 'bounded result/page feedback missing')
need('ExpressiveSwitch(checked = allEnabled, onCheckedChange = { enabled -> onSelected(if (enabled) 0 else 1) })' in main, 'All switch behavior drifted')
need('enabled = !allEnabled' in main, 'specific dropdown is not disabled while All is active')
need('labels = labels.drop(1)' in main and 'onSelected = { onSelected(it + 1) }' in main, 'All-first original index mapping drifted')
filter_start = main.find('private fun ExpressiveFilterBar(')
filter_end = main.find('@Composable\nprivate fun ExpressiveMultiFilterBar', filter_start)
filter_block = main[filter_start:filter_end] if filter_start >= 0 and filter_end > filter_start else ''
need(bool(filter_block), 'ExpressiveFilterBar block missing')
need('ExpressiveFilterCarousel(' not in filter_block, 'single-select ExpressiveFilterBar still uses horizontal carousel')
need('ExpressiveFilterCarousel(labels, null' in main, 'multi-select filter behavior was unintentionally removed')
need(icon_path.is_file(), 'dropdown indicator resource missing')
if icon_path.is_file():
    icon = icon_path.read_text(encoding='utf-8')
    need('android:pathData="M7.41,8.59 12,13.17 16.59,8.59 18,10 12,16 6,10z"' in icon, 'dropdown indicator geometry drifted')

hdr_text = 'The official logos below represent HDR types detected by Android on this device. HLG and HLG+ are shown as text because no official logo is defined by the authoritative standards sources used by VulkanScope.'
need(hdr_text in main, 'HDR official-logo/device-detection disclosure missing')
need('3 -> "HLG"' in main, 'Android HLG type mapping drifted')
need('6 -> "HLG+"' in main, 'Android API-37 HLG+ type mapping drifted')
need('"hdr10" -> R.drawable.hdr_hdr10' in main, 'HDR10 logo mapping missing')
need('"hdr10+" -> R.drawable.hdr_hdr10_plus' in main, 'HDR10+ logo mapping missing')
need('"dolby vision" -> R.drawable.hdr_dolby_vision' in main, 'Dolby Vision logo mapping missing')
hdr_card_start = main.find('private fun HdrTypeCard(type: String)')
hdr_card_end = main.find('\n@Composable', hdr_card_start + 1)
hdr_card = main[hdr_card_start:hdr_card_end] if hdr_card_start >= 0 and hdr_card_end > hdr_card_start else ''
need('"hlg" ->' not in hdr_card.lower(), 'HLG was assigned a logo instead of remaining text')
need('"hlg+" ->' not in hdr_card.lower(), 'HLG+ was assigned a logo instead of remaining text')
need('else -> null' in hdr_card, 'unknown/text HDR fallback missing')
need('HdrCapabilitiesCarousel(display.hdrTypes)' in main, 'HDR cards are no longer driven by detected display HDR types')

manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
need('MANAGE_EXTERNAL_STORAGE' not in manifest, 'unrequested storage permission introduced')
need('OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' in main, 'Database endpoint drifted')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.3.1 searchable paged single-select filter and HDR disclosure contract')
