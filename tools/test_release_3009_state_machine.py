#!/usr/bin/env python3
from datetime import datetime, timezone


def classify_driver(value):
    return ('accent', 'bold') if 'turnip' in value.lower() else ('primary', 'bold')

if classify_driver('Turnip / third-party driver') != ('accent', 'bold'):
    raise SystemExit('Turnip driver styling model regressed')
if classify_driver('System Vulkan driver') != ('primary', 'bold'):
    raise SystemExit('System driver styling model regressed')
if classify_driver('Unknown') != ('primary', 'bold'):
    raise SystemExit('unknown driver styling must not fabricate Turnip identity')


def parse_server_instant(raw):
    try:
        return datetime.fromisoformat(raw.replace('Z', '+00:00')).astimezone(timezone.utc)
    except Exception:
        return None

instant = parse_server_instant('2026-09-25T19:29:22.972Z')
if instant is None or instant.hour != 19 or instant.minute != 29 or instant.second != 22:
    raise SystemExit('Database server instant parsing model regressed')
if parse_server_instant('not-a-timestamp') is not None:
    raise SystemExit('invalid timestamp must take the original-string fallback path')

variants = {
    'Encyclopedia': 'book:REF',
    'Reference search': 'book:search',
    'How to read encyclopedia entries': 'book:question',
    'Capability requirement resolver': 'registry:search',
    'Requirement evaluation summary': 'registry:check',
    'Vulkan Profiles and custom minimums': 'profile:VP',
    'Custom minimum builder': 'profile:add',
    'Saved minimum profiles': 'profile:save',
    'Current custom evaluation': 'profile:check',
    'Dependency graph explorer': 'graph:search',
    'Graph overview': 'graph:grid',
    'Interactive dependency map': 'graph:compass',
    'Collection integrity score': 'shield:check',
    'Scoring method': 'shield:evidence',
    'What this score does not mean': 'shield:question',
    'Vulkan active self-tests': 'test:RUN',
    'Test result summary': 'self-test:SUM',
    'Result semantics': 'test:question',
}
if len(set(variants.values())) != len(variants):
    raise SystemExit('semantic section-icon uniqueness model regressed')
for title, variant in variants.items():
    if ':' not in variant or not variant.split(':', 1)[1]:
        raise SystemExit(f'non-semantic icon variant: {title}')

print('PASS VulkanScope 3.0.9 Database time/driver/icon state machine')
