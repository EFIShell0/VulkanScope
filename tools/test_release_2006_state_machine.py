#!/usr/bin/env python3

def scroll_contract(viewport_height, card_heights, spacing, top_inset, bottom_inset):
    content_height = top_inset + sum(card_heights) + spacing * max(0, len(card_heights) - 1) + bottom_inset
    max_scroll = max(0, content_height - viewport_height)
    return content_height, max_scroll

content, scroll = scroll_contract(420, [112, 112, 112], 10, 8, 82)
assert content == 446
assert scroll == 26

content, scroll = scroll_contract(700, [112, 112, 112], 10, 8, 82)
assert content == 446
assert scroll == 0

content_with_banner, scroll_with_banner = scroll_contract(420, [112, 112, 112], 10, 96, 82)
assert content_with_banner == 534
assert scroll_with_banner == 114

content_side_insets_are_height_neutral, _ = scroll_contract(420, [112, 112, 112], 10, 8, 82)
assert content_side_insets_are_height_neutral == content

print('PASS VulkanScope 2.0.6 Surface chooser scroll state machine')
