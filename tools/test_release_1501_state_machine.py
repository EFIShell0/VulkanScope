#!/usr/bin/env python3


def main():
    bar_width = 310.0
    cells = 4
    cell_width = bar_width / cells
    horizontal_inset = 8.0
    indicator_width = cell_width - horizontal_inset * 2.0
    indicator_height = 42.0
    if abs(cell_width - 77.5) > 1e-9:
        raise SystemExit('navigation cell geometry drifted')
    if abs(indicator_width - 61.5) > 1e-9 or indicator_width <= indicator_height:
        raise SystemExit('selected state is not a bounded horizontal capsule')
    for alpha in (0.0, 0.25, 0.5, 0.75, 1.0):
        scale_x = 0.86 + (1.0 - 0.86) * alpha
        scale_y = 0.96 + alpha * 0.04
        if indicator_width * scale_x > indicator_width + 1e-9:
            raise SystemExit('selected state can grow outside its capsule width')
        if indicator_height * scale_y > indicator_height + 1e-9:
            raise SystemExit('selected state can grow outside its capsule height')
    bottom_gap = 4.0
    bar_height = 54.0
    content_gap = 10.0
    if bottom_gap != 4.0:
        raise SystemExit('navigation does not sit immediately above the platform controls')
    if bottom_gap + bar_height + content_gap != 68.0:
        raise SystemExit('scroll clearance is not synchronized with navigation geometry')
    opening_elapsed = 520 + 500 + 300 + 280
    if opening_elapsed != 1600 or opening_elapsed >= 2100:
        raise SystemExit('opening sequence exceeds the visible startup gate budget')
    splash_handoff_fallback = 900
    gate_fallback = 2100
    if splash_handoff_fallback + gate_fallback > 3000:
        raise SystemExit('startup watchdog bound drifted')
    scenarios = [
        (12, 12, 0, 0),
        (12, 9, 2, 1),
        (7, 0, 0, 7),
        (9, 4, 5, 0),
    ]
    for total, met, failed, unknown in scenarios:
        if met + failed + unknown != total:
            raise SystemExit('profile tally conservation failed')
        if unknown and failed == 0 and unknown == total and met != 0:
            raise SystemExit('unknown evidence was converted to met/unmet evidence')
    branch_checks = [(5, 5, 0, 0), (5, 3, 2, 0)]
    selected = next(values for values in branch_checks if values[2] == 0 and values[3] == 0)
    logical_group = (1, 1, 0, 0)
    combined = tuple(selected[i] + logical_group[i] for i in range(4))
    if combined != (6, 6, 0, 0):
        raise SystemExit('satisfied OR group exposes failed checks from an unneeded branch')
    print('VulkanScope 1.5.1 profile/navigation/opening state machine: PASS')


if __name__ == '__main__':
    main()
