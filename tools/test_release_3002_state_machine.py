#!/usr/bin/env python3

def compile_scope(extracted_top_level, uses_expressive_loading, has_expressive_opt_in):
    if extracted_top_level and uses_expressive_loading and not has_expressive_opt_in:
        return 'compile-error'
    return 'compile-ok'

if compile_scope(True, True, False) != 'compile-error':
    raise SystemExit('missing top-level expressive opt-in was not modeled as a compile error')
if compile_scope(True, True, True) != 'compile-ok':
    raise SystemExit('explicit top-level expressive opt-in did not restore the modeled compile state')
if compile_scope(False, True, False) != 'compile-ok':
    raise SystemExit('state model incorrectly requires a local opt-in when the declaration is not extracted')
if compile_scope(True, False, False) != 'compile-ok':
    raise SystemExit('state model incorrectly requires expressive opt-in without expressive API use')

print('PASS VulkanScope 3.0.2 expressive loading compile state machine')
