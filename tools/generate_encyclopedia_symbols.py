#!/usr/bin/env python3
import argparse
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path

HEADER = '''package com.efishell.vulkanscope

internal enum class VulkanRegistrySymbolKind { COMMAND, TOKEN, TYPE }

internal data class VulkanRegistrySymbolReference(
    val name: String,
    val owner: String,
    val providers: String
)
'''

def supports_vulkan(value: str | None) -> bool:
    if not value:
        return True
    parts = {x.strip() for x in value.split(',')}
    return 'vulkan' in parts and 'disabled' not in parts

def esc(value: str) -> str:
    return value.replace('\\', '\\\\').replace('"', '\\"').replace('\t', ' ').replace('\n', ' ')

def provider_maps(root):
    providers = defaultdict(set)
    for feature in root.findall('./feature'):
        if not supports_vulkan(feature.get('api')):
            continue
        provider = feature.get('name', '')
        for require in feature.findall('require'):
            if not supports_vulkan(require.get('api')):
                continue
            for tag in ('command', 'enum', 'type'):
                for node in require.findall(tag):
                    name = node.get('name')
                    if name:
                        providers[(tag, name)].add(provider)
    for extension in root.findall('./extensions/extension'):
        if not supports_vulkan(extension.get('supported')):
            continue
        provider = extension.get('name', '')
        for require in extension.findall('require'):
            if not supports_vulkan(require.get('api')):
                continue
            for tag in ('command', 'enum', 'type'):
                for node in require.findall(tag):
                    name = node.get('name')
                    if name:
                        providers[(tag, name)].add(provider)
    return providers

def collect(root):
    providers = provider_maps(root)

    command_names = set()
    for cmd in root.findall('./commands/command'):
        name = cmd.get('name') or cmd.findtext('proto/name')
        if name and name.startswith('vk') and providers.get(('command', name)):
            command_names.add(name)
    commands = []
    for name in sorted(command_names):
        prov = ', '.join(sorted(providers[('command', name)]))
        commands.append((name, 'Vulkan command', prov))

    token_owner = {}
    for group in root.findall('./enums'):
        owner = group.get('name') or group.get('type') or 'Vulkan enum/token'
        for enum in group.findall('enum'):
            name = enum.get('name')
            if name and name.startswith('VK_'):
                token_owner.setdefault(name, owner)
    for enum in root.findall('.//enum'):
        name = enum.get('name')
        if not name or not name.startswith('VK_'):
            continue
        extends = enum.get('extends')
        if extends:
            token_owner.setdefault(name, extends)
        elif name.endswith('_EXTENSION_NAME'):
            token_owner.setdefault(name, 'Extension name macro')
        elif name.endswith('_SPEC_VERSION'):
            token_owner.setdefault(name, 'Extension revision macro')
        else:
            token_owner.setdefault(name, 'Vulkan enum/token')
    tokens = []
    for name in sorted(token_owner):
        provset = providers.get(('enum', name), set())
        if not provset and token_owner[name] == 'Vulkan enum/token':
            continue
        prov = ', '.join(sorted(provset)) if provset else 'Core/registry declaration'
        tokens.append((name, token_owner[name], prov))

    type_meta = {}
    for node in root.findall('./types/type'):
        name = node.get('name') or node.findtext('name')
        if not name or not name.startswith('Vk'):
            continue
        if not providers.get(('type', name)):
            continue
        category = node.get('category') or 'type'
        type_meta[name] = category
    types = []
    for name in sorted(type_meta):
        prov = ', '.join(sorted(providers[('type', name)]))
        types.append((name, type_meta[name], prov))
    return commands, tokens, types

def emit_chunks(lines, prefix, rows, chunk_size=180):
    names = []
    for idx in range(0, len(rows), chunk_size):
        chunk = rows[idx:idx+chunk_size]
        name = f'{prefix}Chunk{idx // chunk_size}'
        names.append(name)
        lines.append(f'private fun {name}(): Array<String> = arrayOf(')
        for symbol, owner, providers in chunk:
            lines.append(f'    "{esc(symbol)}\\t{esc(owner)}\\t{esc(providers)}",')
        lines.append(')')
        lines.append('')
    return names

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--registry', default='registry/upstream/vk.xml')
    parser.add_argument('--output', default='app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt')
    args = parser.parse_args()
    registry = Path(args.registry)
    root = ET.parse(registry).getroot()
    commands, tokens, types = collect(root)
    lines = [HEADER.rstrip(), '']
    command_chunks = emit_chunks(lines, 'vulkanCommandSymbol', commands)
    token_chunks = emit_chunks(lines, 'vulkanTokenSymbol', tokens)
    type_chunks = emit_chunks(lines, 'vulkanTypeSymbol', types)

    def emit_lazy(name, chunks):
        lines.append(f'private val {name}: List<String> by lazy(LazyThreadSafetyMode.PUBLICATION) {{')
        lines.append('    buildList {')
        for chunk in chunks:
            lines.append(f'        addAll({chunk}())')
        lines.append('    }')
        lines.append('}')
        lines.append('')

    emit_lazy('VULKAN_COMMAND_SYMBOLS', command_chunks)
    emit_lazy('VULKAN_TOKEN_SYMBOLS', token_chunks)
    emit_lazy('VULKAN_TYPE_SYMBOLS', type_chunks)
    lines += [
        f'internal const val VULKAN_COMMAND_SYMBOL_COUNT = {len(commands)}',
        f'internal const val VULKAN_TOKEN_SYMBOL_COUNT = {len(tokens)}',
        f'internal const val VULKAN_TYPE_SYMBOL_COUNT = {len(types)}',
        '',
        'private fun decodeVulkanRegistrySymbol(encoded: String): VulkanRegistrySymbolReference? {',
        "    val first = encoded.indexOf('\\t')",
        "    val second = encoded.indexOf('\\t', first + 1)",
        '    if (first <= 0 || second <= first + 1) return null',
        '    return VulkanRegistrySymbolReference(',
        '        name = encoded.substring(0, first),',
        '        owner = encoded.substring(first + 1, second),',
        '        providers = encoded.substring(second + 1)',
        '    )',
        '}',
        '',
        'internal fun searchVulkanRegistrySymbols(kind: VulkanRegistrySymbolKind, rawQuery: String, limit: Int = 24): List<VulkanRegistrySymbolReference> {',
        '    val query = rawQuery.trim()',
        '    if (query.length < 2 || limit <= 0) return emptyList()',
        '    val source = when (kind) {',
        '        VulkanRegistrySymbolKind.COMMAND -> VULKAN_COMMAND_SYMBOLS',
        '        VulkanRegistrySymbolKind.TOKEN -> VULKAN_TOKEN_SYMBOLS',
        '        VulkanRegistrySymbolKind.TYPE -> VULKAN_TYPE_SYMBOLS',
        '    }',
        '    val out = ArrayList<VulkanRegistrySymbolReference>(minOf(limit, 24))',
        '    val seen = HashSet<String>(minOf(limit * 2, 64))',
        '    fun collect(predicate: (String) -> Boolean) {',
        '        if (out.size >= limit) return',
        '        for (encoded in source) {',
        '            val tab = encoded.indexOf(\'\\t\')',
        '            if (tab <= 0) continue',
        '            val name = encoded.substring(0, tab)',
        '            if (name in seen || !predicate(name)) continue',
        '            seen += name',
        '            val decoded = decodeVulkanRegistrySymbol(encoded) ?: continue',
        '            out += decoded',
        '            if (out.size >= limit) return',
        '        }',
        '    }',
        '    collect { it.equals(query, ignoreCase = true) }',
        '    collect { it.startsWith(query, ignoreCase = true) }',
        '    collect { it.contains(query, ignoreCase = true) }',
        '    return out',
        '}',
        ''
    ]
    Path(args.output).write_text('\n'.join(lines), encoding='utf-8')
    print(f'generated {args.output}: commands={len(commands)} tokens={len(tokens)} types={len(types)}')

if __name__ == '__main__':
    main()
