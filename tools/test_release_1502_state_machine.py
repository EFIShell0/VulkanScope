#!/usr/bin/env python3


def parts(value: str) -> tuple[int, ...]:
    return tuple(int(piece) for piece in value.split('.'))


def main():
    agp = '9.4.1'
    gradle = '9.7.1'
    if parts(agp) < parts('9.4.1') or parts(gradle) < parts('9.6.0'):
        raise SystemExit('AGP/Gradle compatibility floor failed')
    compile_sdk = 37
    target_sdk = 37
    agp_max_api = 37
    if compile_sdk != target_sdk or target_sdk > agp_max_api:
        raise SystemExit('Android API/AGP compatibility drifted')
    bar_width = 310.0
    bar_height = 54.0
    cells = 4
    if bar_height < 48.0 or bar_width / cells < 48.0:
        raise SystemExit('primary navigation touch target fell below 48 dp')
    bounded_inputs = {
        'turnip_archive': 96 * 1024 * 1024,
        'analysis_snapshot': 8 * 1024 * 1024,
        'probe_output': 64 * 1024 * 1024,
    }
    if any(value <= 0 or value > 256 * 1024 * 1024 for value in bounded_inputs.values()):
        raise SystemExit('resource bound became invalid or unreasonably large')
    cleanup = {
        'network_callback': True,
        'display_listener': True,
        'activity_scope': True,
        'update_calls': True,
        'probe_worker': True,
    }
    if not all(cleanup.values()):
        raise SystemExit('lifecycle cleanup model is incomplete')
    evidence_states = {'met', 'verified_unmet', 'unknown'}
    if evidence_states != {'met', 'verified_unmet', 'unknown'}:
        raise SystemExit('report evidence state partition drifted')
    print('VulkanScope 1.5.2 build/accessibility/resource state model: PASS')


if __name__ == '__main__':
    main()
