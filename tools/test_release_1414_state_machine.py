#!/usr/bin/env python3

def item_geometry(total_width: float, side_margin: float, inner_padding: float, gap: float, count: int):
    usable = total_width - side_margin * 2 - inner_padding * 2 - gap * (count - 1)
    width = usable / count
    return [(side_margin + inner_padding + i * (width + gap), width) for i in range(count)]


def main():
    for total_width in (320.0, 360.0, 393.0, 480.0, 600.0):
        items = item_geometry(total_width, 20.0, 6.0, 2.0, 5)
        if len(items) != 5:
            raise SystemExit('all five destinations must be present')
        if any(width <= 48.0 for _, width in items) and total_width >= 360.0:
            raise SystemExit('navigation destinations lost minimum practical pointer width on normal phone widths')
        for index in range(1, 5):
            prev_x, prev_w = items[index - 1]
            x, _ = items[index]
            if x <= prev_x + prev_w:
                raise SystemExit('navigation destinations overlap')
    alpha = 0xCC / 255.0
    if not 0.70 <= alpha < 1.0:
        raise SystemExit('floating surface must remain visibly translucent')
    print('VulkanScope 1.4.14 floating navigation state machine: PASS')


if __name__ == '__main__':
    main()
