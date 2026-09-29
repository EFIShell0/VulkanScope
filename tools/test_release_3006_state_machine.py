#!/usr/bin/env python3
from dataclasses import dataclass

@dataclass(frozen=True)
class DatabaseUiState:
    network_available: bool
    mode: int
    list_loading: bool = False
    report_loading: bool = False
    report_id_valid: bool = False
    has_cursor: bool = True

    def controls(self):
        return {
            'load_or_refresh': self.network_available and not self.list_loading,
            'load_more': self.network_available and not self.list_loading and self.has_cursor,
            'report_id_input': self.network_available,
            'fetch_report': self.network_available and self.report_id_valid and not self.report_loading,
        }

for network in (False, True):
    list_state = DatabaseUiState(network_available=network, mode=0, report_id_valid=True)
    id_state = DatabaseUiState(network_available=network, mode=1, report_id_valid=True)
    assert list_state.controls() == id_state.controls()
    controls = list_state.controls()
    assert controls['load_or_refresh'] is network
    assert controls['report_id_input'] is network
    assert controls['fetch_report'] is network

assert not DatabaseUiState(True, 0, list_loading=True).controls()['load_or_refresh']
assert not DatabaseUiState(True, 0, list_loading=True).controls()['load_more']
assert not DatabaseUiState(True, 1, report_loading=True, report_id_valid=True).controls()['fetch_report']
assert not DatabaseUiState(True, 1, report_id_valid=False).controls()['fetch_report']
assert not DatabaseUiState(True, 0, has_cursor=False).controls()['load_more']
print('PASS VulkanScope 3.0.6 validated-network Database UI state machine')
