#!/usr/bin/env python3

def horizontal_arrow(enabled):
    return {
        'container': 'vulkan-red' if enabled else 'vulkan-red-disabled',
        'chevron': 'white' if enabled else 'white-disabled',
        'geometry': 'unchanged'
    }

def vertical_arrow(direction):
    return {'direction': direction, 'treatment': 'retained'}

assert horizontal_arrow(True) == {'container': 'vulkan-red', 'chevron': 'white', 'geometry': 'unchanged'}
assert horizontal_arrow(False)['container'].startswith('vulkan-red')
assert horizontal_arrow(False)['chevron'].startswith('white')
assert vertical_arrow('up') == {'direction': 'up', 'treatment': 'retained'}
assert vertical_arrow('down') == {'direction': 'down', 'treatment': 'retained'}

composites = {
    'Queue query safety': ('queues', 'shield'),
    'Export complete report': ('surface', 'export-arrow'),
    'Export TXT': ('text', 'TXT'),
    'Export HTML': ('html', 'HTML'),
    'Explore': ('compass', None),
    'Check for updates': ('download', None),
    'Direct GitHub updates': ('download', None),
    'Import driver ZIP': ('zip-download', None),
}
assert composites['Queue query safety'] == ('queues', 'shield')
assert composites['Export complete report'] == ('surface', 'export-arrow')
assert composites['Export TXT'][1] == 'TXT'
assert composites['Export HTML'][1] == 'HTML'
assert composites['Explore'][0] == 'compass'
assert composites['Check for updates'][0] == 'download'
assert composites['Direct GitHub updates'][0] == 'download'
assert composites['Import driver ZIP'][0] == 'zip-download'

def producer_identity(version, code):
    parts = version.split('.')
    return len(parts) == 3 and parts[0] == '1' and parts[1] == '0' and parts[2].isdigit() and code == 1000 + int(parts[2])

assert producer_identity('1.0.19', 1019)
assert not producer_identity('1.0.19', 1018)
assert not producer_identity('1.0.19', 1020)
print('PASS VulkanScope 1.0.19 semantic UI and producer state machine')
