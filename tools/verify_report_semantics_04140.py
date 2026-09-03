#!/usr/bin/env python3
import argparse
import re
import xml.etree.ElementTree as ET
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
errors = []

def require(condition, message):
    if not condition:
        errors.append(message)

cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
parity = (root / 'app/src/main/cpp/extension_field_coverage_parity.inc').read_text(encoding='utf-8')
generated = (root / 'app/src/main/cpp/extension_field_coverage_generated.inc').read_text(encoding='utf-8')
kt = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
generator = (root / 'tools/generate_extension_field_coverage.py').read_text(encoding='utf-8')
registry = ET.parse(root / 'registry/upstream/vk.xml').getroot()

aliases = {}
struct_members = {}
for node in registry.findall('./types/type'):
    if node.get('category') != 'struct':
        continue
    name = node.get('name') or node.findtext('name')
    if not name:
        continue
    alias = node.get('alias')
    if alias:
        aliases[name] = alias
        continue
    members = {}
    for member in node.findall('member'):
        member_name = member.findtext('name')
        member_type = member.findtext('type')
        if member_name and member_type:
            members[member_name] = member_type
    struct_members[name] = members

def canonical(name):
    seen = set()
    while name in aliases and name not in seen:
        seen.add(name)
        name = aliases[name]
    return name

def members_for(name):
    return struct_members.get(canonical(name), {})

def feature_struct(name):
    return 'Features' in canonical(name)

block_re = re.compile(r'const auto& value = \*reinterpret_cast<const (VkPhysicalDevice\w+)\*>\(ptr\);(.*?)(?:break;|return;)', re.S)
call_re = re.compile(r'generatedEmit(Bool|Auto|Numeric|String|HexTyped|Array)\(dst,\s*section,\s*"([^"]+)"')
parity_bool = 0
parity_auto = 0
property_bool = 0
for struct_name, body in block_re.findall(parity):
    members = members_for(struct_name)
    for emitter, field in call_re.findall(body):
        typ = members.get(field)
        if emitter == 'Bool':
            parity_bool += 1
            require(typ == 'VkBool32', f'generatedEmitBool type mismatch: {struct_name}.{field} is {typ}')
            if not feature_struct(struct_name):
                property_bool += 1
        elif emitter == 'Auto':
            parity_auto += 1
            require(not feature_struct(struct_name), f'generatedEmitAuto remains in feature struct: {struct_name}.{field}')
            require(typ != 'VkBool32', f'VkBool32 still routed through generatedEmitAuto: {struct_name}.{field}')

for struct_name, body in block_re.findall(generated):
    members = members_for(struct_name)
    for emitter, field in call_re.findall(body):
        typ = members.get(field)
        if emitter == 'Bool':
            require(typ == 'VkBool32', f'generated include Bool type mismatch: {struct_name}.{field} is {typ}')

require('generatedSectionIsFeatureStruct' in cpp, 'struct-role feature/property classifier is missing')
require('const bool featureStruct = generatedSectionIsFeatureStruct(section);' in cpp, 'generatedEmitBool does not use struct-role semantics')
require('dst.push_back({featureStruct,' in cpp, 'generatedEmitBool does not preserve property VkBool32 as a property')
auto_match = re.search(r'template <typename T> void generatedEmitAuto\(.*?\n\}', cpp, re.S)
require(auto_match is not None, 'generatedEmitAuto is missing')
if auto_match:
    auto_body = auto_match.group(0)
    require('std::is_same_v<D, VkBool32>' not in auto_body, 'generatedEmitAuto still treats VkBool32 typedef identity as semantic type evidence')
    require('generatedSectionIsFeatureStruct(section)' not in auto_body, 'generatedEmitAuto still attempts runtime feature casting')
    require('std::is_same_v<D, VkExtent2D>' in auto_body and 'std::is_same_v<D, VkExtent3D>' in auto_body, 'extent properties are not formatted structurally')
require(property_bool > 0, 'no boolean property coverage was observed; property-bool regression oracle is ineffective')
require(parity_bool > 0 and parity_auto > 0, 'parity field-emitter census is unexpectedly empty')
require("elif typ == 'VkConformanceVersion':" in generator, 'generator does not preserve VkConformanceVersion components')
require('generatedEmitHexTyped(dst, section, "conformanceVersion"' not in generated, 'checked-in coverage still serializes conformanceVersion as opaque bytes')
require(generated.count('generatedEmitString(dst, section, "conformanceVersion"') >= 2, 'checked-in conformanceVersion semantic serialization is missing')

css_expect = {
    '.yes{background:#133b28;color:#74e2a6}': 'positive status CSS missing',
    '.available{background:#182f52;color:#a9c9ff}': 'Available distinct status CSS missing',
    '.no{background:#49171c;color:#ff8f98}': 'negative status CSS missing',
    '.unavailable{background:#493019;color:#ffc27a}': 'Unavailable must remain distinct from Unsupported',
    '.neutral{background:#30313a;color:#d0d0d6}': 'Not applicable neutral status CSS missing',
    '.incomplete{background:#403713;color:#ffd76b}': 'Incomplete status CSS missing',
    '.unknown{background:#292a2f;color:#c6c6cc}': 'Unknown status CSS missing'
}
for token, message in css_expect.items():
    require(token in kt, message)
status_match = re.search(r'fun statusBadge\(value: String\): String \{(.*?)\n    \}', kt, re.S)
require(status_match is not None, 'HTML statusBadge mapping is missing')
if status_match:
    status = status_match.group(1)
    for token in ['lower == "pass"', 'lower == "fail"', 'lower.contains("unavailable")', 'lower.contains("not applicable")', 'lower.contains("incomplete")', 'lower.contains("unknown")']:
        require(token in status, f'HTML status semantic missing: {token}')
    require('lower == "available" -> "available"' in status, 'Available mapping is not explicit')
    require('lower == "fail"' in status and '-> "no"' in status, 'FAIL is not mapped to the negative class')
require('Device extension enumeration reason" to htmlEscape(d.deviceExtensionReason.ifBlank { "None" })' in kt, 'blank device-extension reason is not rendered explicitly as None')
require('Vulkan 1.4 reason" to htmlEscape(d.vulkan14Reason.ifBlank { "None" })' in kt, 'blank Vulkan 1.4 reason is not rendered explicitly as None')
require('d.detailedProperties.size} evidence rows; $htmlDetailedPropertyCount property/query rows; $htmlDetailedSafetyCount safety diagnostics;' in kt, 'HTML evidence-count decomposition is missing')
require('d.detailedProperties.count { it.section == "Vulkan Query Safety" }' in kt and 'd.detailedProperties.size - detailedSafetyCount' in kt and 'd.detailedProperties.size - htmlDetailedSafetyCount' in kt, 'property/query count does not exclude safety diagnostics')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print(f'PASS report semantics 0.41.40: parityBool={parity_bool} propertyBool={property_bool} parityAutoProperties={parity_auto}')
