#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_detail_tv_collection_0804.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')


def verify(tree: Path) -> bool:
    return subprocess.run(
        [sys.executable, str(verifier), '--root', str(tree)],
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
        check=False,
    ).returncode == 0


def mutate(name: str, old: str, new: str, expect_pass: bool = False) -> None:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-0804-mutation-') as tmp:
        tree = Path(tmp) / 'root'
        shutil.copytree(root, tree, ignore=shutil.ignore_patterns('__pycache__', '*.pyc'))
        path = tree / main_rel
        text = path.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'{name}: mutation anchor missing')
        path.write_text(text.replace(old, new, 1), encoding='utf-8')
        passed = verify(tree)
        if passed != expect_pass:
            outcome = 'PASS' if passed else 'FAIL'
            wanted = 'PASS' if expect_pass else 'FAIL'
            raise SystemExit(f'{name}: verifier returned {outcome}; expected {wanted}')


mutate(
    'format detail affordance removal',
    'Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { DetailAffordance { selected = format } }',
    'Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { Spacer(Modifier.height(1.dp)) }',
)
mutate(
    'modal vertical scrolling removal',
    'Modifier.fillMaxWidth().verticalScroll(scrollState).focusGroup().padding(end = 38.dp)',
    'Modifier.fillMaxWidth().focusGroup().padding(end = 38.dp)',
)
mutate(
    'modal boundary indicators removal',
    'ScrollBoundaryIndicators(scrollState, Modifier.align(Alignment.CenterEnd).padding(end = 2.dp))',
    'Spacer(Modifier.size(1.dp))',
)
mutate(
    'shared capability-card TV browse focus removal',
    'Surface(color = containerColor, shape = shape, modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape)))',
    'Surface(color = containerColor, shape = shape, modifier = Modifier.fillMaxWidth())',
)
mutate(
    'collection failure classification removal',
    'collectionStatus = if (collectionFinishedSuccessfully(latestReport)) CollectionStatus.COMPLETED else CollectionStatus.FAILED',
    'collectionStatus = CollectionStatus.COMPLETED',
)
mutate(
    'collecting text restoration',
    '"VulkanScope is collecting Vulkan information in the background.",',
    '"Collecting information…",',
)
mutate(
    'unrelated format description false-positive control',
    'Implementation-reported format capabilities. Bitmasks are expanded to canonical Vulkan feature names; unknown bits remain visible in hexadecimal.',
    'Implementation-reported format capabilities. Canonical names and raw bits remain visible.',
    expect_pass=True,
)

if not verify(root):
    raise SystemExit('untouched successor no longer passes verifier')
print('PASS 0.80.4 detail/TV/collection negative mutations and false-positive control')
