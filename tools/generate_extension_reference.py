from __future__ import annotations
import argparse
import json
import xml.etree.ElementTree as ET
from pathlib import Path


def extension_spec_version(ext):
    for req in ext.findall('require'):
        for enum in req.findall('enum'):
            name = enum.get('name') or ''
            if name.endswith('_SPEC_VERSION') and enum.get('value'):
                return enum.get('value') or ''
    return ''


def q(value):
    return json.dumps(value, ensure_ascii=False)


def kotlin_entry(e):
    vals = [
        e['name'], e.get('authorTag') or e.get('author', ''), e.get('specVersion', ''),
        e.get('type', ''), e.get('platform', ''), e.get('promotedTo', ''), e.get('depends', ''),
        e.get('requires', ''), e.get('deprecatedBy', ''), e.get('obsoletedBy', '')
    ]
    commands = ', '.join(q(x) for x in e.get('commands', []))
    enums = ', '.join(q(x) for x in e.get('enums', []))
    provisional = 'true' if e.get('provisional') else 'false'
    return 'VulkanExtensionReference(' + ', '.join(q(x) for x in vals) + f', {provisional}, listOf({commands}), listOf({enums}), {q(e.get("queryGroup", ""))}, {q(e["specUrl"])})'


def write_kotlin(path, entries):
    chunk_size = 16
    lines = [
        'package com.efishell.vulkanscope',
        '',
        'internal data class VulkanExtensionReference(val name: String, val author: String, val specVersion: String, val type: String, val platform: String, val promotedTo: String, val depends: String, val requires: String, val deprecatedBy: String, val obsoletedBy: String, val provisional: Boolean, val commands: List<String>, val enums: List<String>, val queryGroup: String, val specUrl: String)',
        ''
    ]
    chunk_names = []
    for index in range(0, len(entries), chunk_size):
        chunk_name = f'vulkanExtensionReferenceChunk{index // chunk_size}'
        chunk_names.append(chunk_name)
        lines.append(f'private fun {chunk_name}(): List<VulkanExtensionReference> = listOf(')
        for entry in entries[index:index + chunk_size]:
            lines.append(f'    {kotlin_entry(entry)},')
        lines.append(')')
        lines.append('')
    lines.append('internal val VULKAN_EXTENSION_REFERENCE: Map<String, VulkanExtensionReference> = buildMap {')
    for chunk_name in chunk_names:
        lines.append(f'    {chunk_name}().forEach {{ put(it.name, it) }}')
    lines.append('}')
    lines.append('')
    lines.append('internal fun vulkanExtensionReference(name: String): VulkanExtensionReference {')
    lines.append('    val author = name.removePrefix("VK_").substringBefore("_").takeIf { it.isNotBlank() } ?: "Unknown"')
    lines.append('    return VULKAN_EXTENSION_REFERENCE[name] ?: VulkanExtensionReference(name, author, "", "", "", "", "", "", "", "", false, emptyList(), emptyList(), "", "https://registry.khronos.org/vulkan/specs/latest/man/html/${name}.html")')
    lines.append('}')
    Path(path).write_text('\n'.join(lines) + '\n', encoding='utf-8')


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--registry', required=True)
    ap.add_argument('--output', required=True)
    ap.add_argument('--kotlin-output')
    ap.add_argument('--baseline', default='Vulkan 1.4.361')
    args = ap.parse_args()
    root = ET.parse(args.registry).getroot()
    authors = {x.get('name', ''): x.get('author', '') for x in root.findall('./tags/tag')}
    existing_query_groups = {}
    output_path = Path(args.output)
    if output_path.is_file():
        try:
            previous = json.loads(output_path.read_text(encoding='utf-8'))
            existing_query_groups = {
                str(e.get('name', '')): str(e.get('queryGroup', ''))
                for e in previous.get('entries', [])
                if isinstance(e, dict) and e.get('name')
            }
        except (OSError, ValueError, TypeError):
            existing_query_groups = {}
    entries = []
    for ext in root.findall('./extensions/extension'):
        name = ext.get('name') or ''
        supported = {x.strip() for x in (ext.get('supported') or '').split(',') if x.strip()}
        if not name.startswith('VK_') or not ({'vulkan', 'vulkanbase'} & supported):
            continue
        commands = []
        enums = []
        for req in ext.findall('require'):
            commands.extend(x.get('name', '') for x in req.findall('command') if x.get('name'))
            enums.extend(x.get('name', '') for x in req.findall('enum') if x.get('name'))
        tag = name.removeprefix('VK_').split('_', 1)[0]
        entries.append({
            'name': name,
            'authorTag': tag,
            'author': authors.get(tag, ''),
            'specVersion': extension_spec_version(ext),
            'type': ext.get('type') or '',
            'platform': ext.get('platform') or '',
            'promotedTo': ext.get('promotedto') or '',
            'depends': ext.get('depends') or '',
            'requires': ext.get('requires') or '',
            'deprecatedBy': ext.get('deprecatedby') or '',
            'obsoletedBy': ext.get('obsoletedby') or '',
            'provisional': ext.get('provisional', '').lower() == 'true',
            'commands': sorted(set(commands)),
            'enums': sorted(set(enums)),
            'queryGroup': existing_query_groups.get(name, ''),
            'specUrl': f'https://registry.khronos.org/vulkan/specs/latest/man/html/{name}.html'
        })
    entries.sort(key=lambda x: x['name'])
    data = {'schema': 2, 'baseline': args.baseline, 'source': 'Khronos vk.xml', 'entries': entries}
    output_path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + '\n', encoding='utf-8')
    if args.kotlin_output:
        write_kotlin(args.kotlin_output, entries)
    print(f'PASS extension reference: entries={len(entries)} baseline={args.baseline}')


if __name__ == '__main__':
    main()
