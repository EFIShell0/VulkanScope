#!/usr/bin/env python3
from dataclasses import dataclass
from enum import Enum, auto

class UpdateState(Enum):
    HIDDEN = auto()
    CHECKING = auto()
    UP_TO_DATE = auto()
    AVAILABLE = auto()
    DOWNLOADING = auto()
    FAILED = auto()

@dataclass(frozen=True)
class BannerModel:
    update_icon: bool
    review_contained: bool
    review_chevron: bool


def banner(state: UpdateState) -> BannerModel:
    return BannerModel(state is UpdateState.AVAILABLE, state is UpdateState.AVAILABLE, state is UpdateState.AVAILABLE)


def confirm_action(network_validated: bool, action: str) -> str:
    if action == 'cancel': return 'dismiss'
    if action == 'confirm' and network_validated: return 'start_explicit_download'
    if action == 'confirm': return 'blocked_offline'
    return 'none'

for state in UpdateState:
    model = banner(state)
    expected = state is UpdateState.AVAILABLE
    assert model.update_icon == expected
    assert model.review_contained == expected
    assert model.review_chevron == expected
assert confirm_action(True, 'cancel') == 'dismiss'
assert confirm_action(False, 'cancel') == 'dismiss'
assert confirm_action(False, 'confirm') == 'blocked_offline'
assert confirm_action(True, 'confirm') == 'start_explicit_download'
icons = {'fetch':'database_fetch', 'submit':'database_submit', 'browse':'database_browse', 'compare':'compare', 'permalink':'link'}
assert len(set(icons.values())) == len(icons)
print('PASS VulkanScope 1.0.13 update/Database semantic state machine')
