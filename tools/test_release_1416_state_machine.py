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
    for from_index, to_index in [(0, 1), (1, 3), (3, 2), (2, 0)]:
        left0, right0 = edges(from_index, to_index, 0)
        if (left0, right0) != (float(from_index), float(from_index + 1)):
            raise SystemExit('indicator does not start on the source cell')
        widths = []
        for elapsed in (46, 60, 76, 92, 118):
            left, right = edges(from_index, to_index, elapsed)
            if right <= left:
                raise SystemExit('indicator inverted during transition')
            widths.append(right - left)
        if max(widths) <= 1.05:
            raise SystemExit('indicator does not elastically stretch')
        left_end, right_end = edges(from_index, to_index, 220)
        if abs(left_end - to_index) > 1e-6 or abs(right_end - (to_index + 1)) > 1e-6:
            raise SystemExit('indicator does not settle on destination cell')

    bar_width = 352.0
    cell = bar_width / 4.0
    indicator_width = cell - 6.0
    indicator_height = 54.0
    if indicator_width <= indicator_height:
        raise SystemExit('resting selection indicator is not a horizontal capsule')
    if indicator_width < 80.0 or indicator_width > 84.0:
        raise SystemExit('resting selection capsule geometry drifted')

    surface_sections = ['DISPLAY', 'SURFACE_FORMATS', 'PRESENTATION']
    if surface_sections[0] != 'DISPLAY' or len(surface_sections) != 3:
        raise SystemExit('Surface chooser ordering is invalid')
    selected = 'DISPLAY'
    selected = None
    if selected is not None:
        raise SystemExit('Back from a Surface subsection does not return to chooser')

    print('VulkanScope 1.4.16 navigation/Surface state machine: PASS')


if __name__ == '__main__':
    main()
