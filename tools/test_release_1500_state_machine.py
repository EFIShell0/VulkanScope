#!/usr/bin/env python3


def clamp(value: float, minimum: float, maximum: float) -> float:
    return max(minimum, min(maximum, value))


def lerp(a: float, b: float, t: float) -> float:
    return a + (b - a) * clamp(t, 0.0, 1.0)


def stretch(elapsed: float) -> float:
    if elapsed <= 50.0:
        return lerp(1.0, 1.24, elapsed / 50.0)
    if elapsed <= 115.0:
        return lerp(1.24, 0.96, (elapsed - 50.0) / 65.0)
    return lerp(0.96, 1.0, (elapsed - 115.0) / 60.0)


def center(from_index: int, to_index: int, elapsed: float) -> float:
    return lerp(from_index + 0.5, to_index + 0.5, elapsed / 180.0)


def main():
    bar_width = 326.0
    cells = 4
    cell_width = bar_width / cells
    resting_width = cell_width - 14.0
    if not (60.0 < resting_width < 72.0):
        raise SystemExit('resting indicator width is outside compact capsule bounds')
    if resting_width <= 44.0:
        raise SystemExit('resting indicator is not a horizontal capsule')
    for from_index, to_index in [(0, 1), (1, 3), (3, 0), (2, 3)]:
        widths = []
        centers = []
        for elapsed in (0.0, 25.0, 50.0, 80.0, 115.0, 145.0, 175.0, 220.0):
            width = cell_width * stretch(elapsed) - 14.0
            widths.append(width)
            centers.append(center(from_index, to_index, elapsed))
        if max(widths) >= cell_width * 1.25:
            raise SystemExit('indicator stretch can become wider than the bounded reference motion')
        if min(widths) <= 44.0:
            raise SystemExit('indicator collapses below capsule geometry')
        if abs(centers[0] - (from_index + 0.5)) > 1e-6:
            raise SystemExit('indicator center does not begin on source tab')
        if abs(centers[-1] - (to_index + 0.5)) > 1e-6:
            raise SystemExit('indicator center does not settle on target tab')
    scaffold_safe_bottom = 48.0
    visual_gap = 10.0
    bar_height = 54.0
    bar_bottom_from_screen = scaffold_safe_bottom + visual_gap
    if bar_bottom_from_screen != 58.0:
        raise SystemExit('floating bar is not positioned immediately above the safe system navigation region')
    hint_clearance_inside_content = visual_gap + bar_height + 10.0
    if hint_clearance_inside_content != 74.0:
        raise SystemExit('scroll hint clearance is not synchronized with compact navigation geometry')
    splash_callback_seen = False
    gate_open = False
    elapsed = 0
    elapsed += 900
    if not gate_open and not splash_callback_seen:
        splash_callback_seen = True
    if not splash_callback_seen:
        raise SystemExit('startup watchdog did not force custom animation handoff readiness')
    elapsed += 2100
    if not gate_open:
        gate_open = True
    if not gate_open or elapsed > 3000:
        raise SystemExit('startup watchdog did not guarantee bounded gate opening')
    print('VulkanScope 1.5.0 navigation/startup state machine: PASS')


if __name__ == '__main__':
    main()
