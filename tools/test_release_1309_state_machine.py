#!/usr/bin/env python3


def back_action(ime_visible: bool, popup_open: bool) -> str:
    if not popup_open:
        return 'screen-back'
    return 'hide-ime' if ime_visible else 'close-popup'


def fixed_export_name(base: str, extension: str) -> str:
    base = ''.join(ch for ch in base[:160] if ch not in '/\\' and ord(ch) >= 0x20).strip()
    if not base:
        raise ValueError('blank base')
    return f'{base}.{extension}'


def apply_live_labels(popup_open: bool, query: str, page: int, labels):
    # Transient state is intentionally independent of label-list identity.
    hits = [x for x in labels if not query or query.lower() in x.lower()]
    page_count = max(1, (len(hits) + 49) // 50)
    return popup_open, query, min(page, page_count - 1), hits


def main():
    assert back_action(True, True) == 'hide-ime'
    assert back_action(False, True) == 'close-popup'
    assert back_action(False, False) == 'screen-back'

    opened, query, page, hits = apply_live_labels(True, 'memory', 0, ['Core 1.0', 'VK_EXT_memory_budget'])
    assert opened and query == 'memory' and hits == ['VK_EXT_memory_budget']
    opened2, query2, page2, hits2 = apply_live_labels(opened, query, page, ['Core 1.0', 'VK_EXT_memory_budget', 'VK_EXT_memory_priority'])
    assert opened2 and query2 == 'memory' and page2 == 0 and len(hits2) == 2

    assert fixed_export_name('report', 'html') == 'report.html'
    assert fixed_export_name('report.txt', 'html') == 'report.txt.html'  # requested type remains HTML
    assert fixed_export_name('report', 'json') == 'report.json'
    assert fixed_export_name('../report', 'txt') == '..report.txt'  # separators are removed from the editable base

    modes = {'LIST', 'COMPACT', 'GRID', 'DETAILS'}
    assert len(modes) == 4
    print('release_1309 state machine: PASS')


if __name__ == '__main__':
    main()
