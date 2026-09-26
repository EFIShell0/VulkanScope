#!/usr/bin/env python3
import argparse
import struct
from pathlib import Path


def verify(root: Path) -> list[str]:
    errors = []
    main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    build = root / 'app/build.gradle.kts'
    asset = root / 'app/src/main/res/drawable-nodpi/ic_opening_animation_toggle.png'
    if not main.is_file():
        return ['MainActivity.kt missing']
    text = main.read_text(encoding='utf-8')
    build_text = build.read_text(encoding='utf-8') if build.is_file() else ''
    if 'versionCode = 1410' not in build_text or 'versionName = "1.4.10"' not in build_text:
        errors.append('release identity is not 1.4.10/1410')
    mapping = 'title.equals("Opening animation", true) -> R.drawable.ic_opening_animation_toggle'
    if mapping not in text:
        errors.append('Opening animation header does not map to the supplied animation icon')
    header_start = text.find('private fun SectionHeaderIcon')
    header_end = text.find('@Composable\nprivate fun SectionVectorBadgeIcon', header_start)
    header = text[header_start:header_end] if header_start >= 0 and header_end > header_start else ''
    if 'title.equals("Opening animation", true)' not in header or 'painterResource(R.drawable.ic_opening_animation_toggle)' not in header or 'Image(' not in header:
        errors.append('Opening animation section header does not render the supplied icon as an image')
    card_start = text.find('CapabilitySectionCard("Opening animation")')
    card_end = text.find('CapabilitySectionCard("Driver manager")', card_start)
    card = text[card_start:card_end] if card_start >= 0 and card_end > card_start else ''
    if not card:
        errors.append('Opening animation preference card not found')
    elif 'R.drawable.ic_opening_animation_toggle' in card:
        errors.append('Opening animation preference row still contains a duplicate animation icon')
    if not asset.is_file():
        errors.append('supplied opening animation icon asset is missing')
    else:
        data = asset.read_bytes()
        if len(data) < 33 or data[:8] != b'\x89PNG\r\n\x1a\n':
            errors.append('opening animation icon is not a valid PNG')
        else:
            width, height, bit_depth, color_type = struct.unpack('>IIBB', data[16:26])
            if width < 32 or height < 32:
                errors.append('opening animation icon dimensions are unexpectedly small')
            if bit_depth != 8 or color_type not in (4, 6):
                errors.append('opening animation icon must preserve an alpha-capable PNG format')
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
    print('VulkanScope 1.4.10 opening-animation icon placement verifier: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
