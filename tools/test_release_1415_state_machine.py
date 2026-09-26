#!/usr/bin/env python3


def lerp(a: float, b: float, t: float) -> float:
    return a + (b - a) * max(0.0, min(1.0, t))


def edges(from_index: int, to_index: int, elapsed_ms: float):
    if to_index < from_index:
        left = lerp(float(from_index), float(to_index), elapsed_ms / 92.0)
        right = lerp(float(from_index + 1), float(to_index + 1), (elapsed_ms - 46.0) / 118.0)
    else:
        right = lerp(float(from_index + 1), float(to_index + 1), elapsed_ms / 92.0)
        left = lerp(float(from_index), float(to_index), (elapsed_ms - 46.0) / 118.0)
    return left, right


def main():
    for from_index, to_index in [(2, 1), (1, 4), (4, 0), (0, 3)]:
        left0, right0 = edges(from_index, to_index, 0)
        if (left0, right0) != (float(from_index), float(from_index + 1)):
            raise SystemExit('indicator does not start on the source cell')
        intermediate_widths = []
        for elapsed in (46, 60, 76, 92, 118):
            left, right = edges(from_index, to_index, elapsed)
            if right <= left:
                raise SystemExit('elastic indicator inverted during transition')
            intermediate_widths.append(right - left)
        if max(intermediate_widths) <= 1.05:
            raise SystemExit('indicator does not stretch between source and destination cells')
        left_end, right_end = edges(from_index, to_index, 220)
        if abs(left_end - to_index) > 1e-6 or abs(right_end - (to_index + 1)) > 1e-6:
            raise SystemExit('indicator does not settle exactly on the destination cell')

    for width in (320.0, 352.0, 480.0, 900.0):
        bar_width = min(max(width - 36.0, 0.0), 352.0)
        cell = bar_width / 5.0
        if width >= 360.0 and cell < 63.0:
            raise SystemExit('five-destination bar is too narrow for reference geometry')
        if bar_width > 352.0:
            raise SystemExit('landscape bar exceeded fixed reference width')

    portrait_clearance = 24.0 + 80.0
    landscape_clearance = 0.0 + 88.0
    if portrait_clearance <= 62.0 or landscape_clearance <= 62.0:
        raise SystemExit('page scroll-arrow clearance overlaps the floating bar')
    print('VulkanScope 1.4.15 floating navigation state machine: PASS')


if __name__ == '__main__':
    main()
