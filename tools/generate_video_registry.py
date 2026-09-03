import argparse
import hashlib
import json
import xml.etree.ElementTree as ET
from pathlib import Path

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--video', default='registry/upstream/video.xml')
parser.add_argument('--registry', default='registry/upstream/vk.xml')
parser.add_argument('--lock', default='registry/video_registry_lock.json')
parser.add_argument('--out', default='app/src/main/cpp/video_registry_generated.h')
args = parser.parse_args()

video_path = (root / args.video).resolve()
registry_path = (root / args.registry).resolve()
lock = json.loads((root / args.lock).read_text(encoding='utf-8'))
video_bytes = video_path.read_bytes()
registry_bytes = registry_path.read_bytes()
if hashlib.sha256(video_bytes).hexdigest() != lock['sha256']:
    raise SystemExit('video.xml SHA-256 does not match the locked source')
if hashlib.sha256(registry_bytes).hexdigest() != lock['vulkanRegistrySha256']:
    raise SystemExit('vk.xml SHA-256 does not match the locked Vulkan registry source')
video_root = ET.fromstring(video_bytes)
registry_root = ET.fromstring(registry_bytes)

def enum_entries(type_name):
    group = next((x for x in video_root.findall('enums') if x.get('name') == type_name), None)
    if group is None:
        raise SystemExit(f'missing video enum group {type_name}')
    out = []
    for entry in group.findall('enum'):
        name = entry.get('name') or ''
        value = entry.get('value') or ''
        if name.endswith('_INVALID'):
            continue
        out.append((name, int(value, 0)))
    if not out:
        raise SystemExit(f'empty video enum group {type_name}')
    return out

profile_groups = {
    'H264': ('StdVideoH264ProfileIdc', 'STD_VIDEO_H264_PROFILE_IDC_'),
    'H265': ('StdVideoH265ProfileIdc', 'STD_VIDEO_H265_PROFILE_IDC_'),
    'VP9': ('StdVideoVP9Profile', 'STD_VIDEO_VP9_PROFILE_'),
    'AV1': ('StdVideoAV1Profile', 'STD_VIDEO_AV1_PROFILE_')
}
level_groups = {
    'H264': 'StdVideoH264LevelIdc',
    'H265': 'StdVideoH265LevelIdc',
    'VP9': 'StdVideoVP9Level',
    'AV1': 'StdVideoAV1Level'
}
profiles = {key: enum_entries(group) for key, (group, _) in profile_groups.items()}
levels = {key: enum_entries(group) for key, group in level_groups.items()}
video_codecs = registry_root.find('videocodecs')
if video_codecs is None:
    raise SystemExit('vk.xml videocodecs section is missing')
codec_map = {}
for codec in video_codecs.findall('videocodec'):
    profiles_node = codec.find('videoprofiles')
    if profiles_node is None:
        continue
    members = []
    for member in profiles_node.findall('videoprofilemember'):
        values = [(x.get('name') or '', x.get('value') or '') for x in member.findall('videoprofile')]
        members.append((member.get('name') or '', values))
    codec_map[codec.get('name') or ''] = (profiles_node.get('struct') or '', members)
required_codecs = ['H.264 Decode', 'H.265 Decode', 'VP9 Decode', 'AV1 Decode', 'H.264 Encode', 'H.265 Encode', 'AV1 Encode']
for codec_name in required_codecs:
    if codec_name not in codec_map:
        raise SystemExit(f'missing Vulkan video codec profile definition {codec_name}')
std_tokens = {name for entries in profiles.values() for name, _ in entries}
for codec_name in required_codecs:
    _, members = codec_map[codec_name]
    for member_name, values in members:
        if member_name in {'stdProfileIdc', 'stdProfile'}:
            for _, token in values:
                if token not in std_tokens:
                    raise SystemExit(f'{codec_name} references StdVideo profile token absent from video.xml: {token}')

def enum_map(type_name):
    return dict(enum_entries(type_name))

profile_enum_values = {key: enum_map(group) for key, (group, _) in profile_groups.items()}
operation_specs = [
    ('H264Decode', 'H.264 Decode', 'stdProfileIdc', 'H264'),
    ('H265Decode', 'H.265 Decode', 'stdProfileIdc', 'H265'),
    ('VP9Decode', 'VP9 Decode', 'stdProfile', 'VP9'),
    ('AV1Decode', 'AV1 Decode', 'stdProfile', 'AV1'),
    ('H264Encode', 'H.264 Encode', 'stdProfileIdc', 'H264'),
    ('H265Encode', 'H.265 Encode', 'stdProfileIdc', 'H265'),
    ('AV1Encode', 'AV1 Encode', 'stdProfile', 'AV1')
]

def member_values(codec_name, member_name):
    for candidate_name, values in codec_map[codec_name][1]:
        if candidate_name == member_name:
            return values
    raise SystemExit(f'{codec_name} is missing required video profile member {member_name}')

lines = ['#pragma once', '#include <cstddef>', '#include <cstdint>', '', 'struct VideoRegistryValue { const char* displayName; const char* token; int32_t value; };', '']
for array_suffix, codec_name, member_name, key in operation_specs:
    values = member_values(codec_name, member_name)
    if not values:
        raise SystemExit(f'{codec_name} has no values for {member_name}')
    lines.append(f'inline constexpr VideoRegistryValue kVideo{array_suffix}Profiles[] = {{')
    assertions = []
    seen = set()
    for display, token in values:
        if token in seen:
            raise SystemExit(f'duplicate {codec_name} profile token {token}')
        seen.add(token)
        if token not in profile_enum_values[key]:
            raise SystemExit(f'{codec_name} references StdVideo profile token absent from video.xml: {token}')
        value = profile_enum_values[key][token]
        lines.append(f'    {{"{display}", "{token}", {token}}},')
        assertions.append(f'static_assert(static_cast<int32_t>({token}) == {value});')
    lines.append('};')
    lines.extend(assertions)
    lines.append('')

for key, entries in levels.items():
    for token, number in entries:
        lines.append(f'static_assert(static_cast<int32_t>({token}) == {number});')
    lines.append('')
    function_name = {'H264':'videoH264LevelName','H265':'videoH265LevelName','VP9':'videoVP9LevelName','AV1':'videoAV1LevelName'}[key]
    lines.append(f'inline const char* {function_name}(int32_t value) {{')
    lines.append('    switch (value) {')
    for token, number in entries:
        lines.append(f'        case {number}: return "{token}";')
    lines.append('        default: return "UNKNOWN_STD_VIDEO_LEVEL";')
    lines.append('    }')
    lines.append('}')
    lines.append('')

h264_members = dict(codec_map['H.264 Decode'][1])
av1_members = dict(codec_map['AV1 Decode'][1])
lines.append('inline constexpr VideoRegistryValue kVideoH264PictureLayouts[] = {')
for display, token in h264_members.get('pictureLayout', []):
    lines.append(f'    {{"{display}", "{token}", static_cast<int32_t>({token})}},')
lines.append('};')
lines.append('')
lines.append('inline constexpr VideoRegistryValue kVideoAV1FilmGrainModes[] = {')
for display, token in av1_members.get('filmGrainSupport', []):
    lines.append(f'    {{"{display}", "{token}", static_cast<int32_t>({token})}},')
lines.append('};')
lines.append('')
lines.append(f'inline constexpr const char* kVideoRegistrySha256 = "{lock["sha256"]}";')
lines.append(f'inline constexpr const char* kVideoRegistryVulkanSha256 = "{lock["vulkanRegistrySha256"]}";')
lines.append('')
output = '\n'.join(lines)
(root / args.out).write_text(output, encoding='utf-8')
counts = []
for suffix, codec_name, member_name, _ in operation_specs:
    counts.append(f'{suffix}={len(member_values(codec_name, member_name))}')
print('PASS generated Vulkan Video registry catalog: ' + ' '.join(counts))
