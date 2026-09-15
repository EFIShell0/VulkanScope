#!/usr/bin/env python3
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
s=(ROOT/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text()
for fn,next_fn in [('TurnipFileManagerDialog','TurnipFileManagerSelectionSummary'),('SharedStorageBrowserDialog','SharedStorageFolderRow')]:
    scope=s.split('private fun '+fn+'(',1)[1].split('private fun '+next_fn,1)[0]
    assert scope.count('LoadingIndicator(')==2, fn
    prefix=s.split('private fun '+fn+'(',1)[0][-140:]
    assert '@OptIn(ExperimentalMaterial3ExpressiveApi::class)' in prefix, fn
print('release_1307 state machine: PASS')
