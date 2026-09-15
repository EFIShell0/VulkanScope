#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = 'tools/verify_release_1305.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
qr_rel = Path('app/src/main/java/com/efishell/vulkanscope/VulkanQrCode.kt')

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise RuntimeError(f'mutation anchor missing: {old}')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')

def run_mutation(name, mutate, expect_fail=True):
    with tempfile.TemporaryDirectory() as tmp:
        dst = Path(tmp) / 'tree'
        shutil.copytree(root, dst)
        mutate(dst)
        result = subprocess.run([sys.executable, str(dst / verifier), '--root', str(dst)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        failed = result.returncode != 0
        if failed != expect_fail:
            print(result.stdout)
            raise SystemExit(f'{name}: expected fail={expect_fail}, got fail={failed}')

run_mutation('restore duplicate compile declaration', lambda d: replace_once(
    d / main_rel,
    '    val registryCoverage = report.registryCoverage\n',
    '    val registryCoverage = report.registryCoverage\n    val registryCoverage = report.registryCoverage\n'
))
run_mutation('remove archive cancellation check', lambda d: replace_once(
    d / main_rel,
    '                    coroutineContext.ensureActive()\n                    if (++entryCount > 2048)',
    '                    if (++entryCount > 2048)'
))
run_mutation('remove scan generation guard', lambda d: replace_once(
    d / main_rel,
    '                val listing = withContext(Dispatchers.IO) { scanTurnipFileManagerDirectory(root, target) }\n                if (generation != turnipFileManagerScanGeneration) return@launch\n',
    '                val listing = withContext(Dispatchers.IO) { scanTurnipFileManagerDirectory(root, target) }\n'
))
run_mutation('allow navigation while loading', lambda d: replace_once(
    d / main_rel,
    'if (!current.visible || current.loading || current.importing || !android.os.Environment.isExternalStorageManager()) return',
    'if (!current.visible || current.importing || !android.os.Environment.isExternalStorageManager()) return'
))
run_mutation('restore raw folder pointer handler', lambda d: replace_once(
    d / main_rel,
    '.clickable(enabled = enabled, role = Role.Button, onClick = onOpen)',
    '.pointerInput(path, enabled) { if (enabled) detectTapGestures(onTap = { onOpen() }) }'
))
run_mutation('restore raw filter pointer handler', lambda d: replace_once(
    d / main_rel,
    '.alpha(if (enabled) 1f else 0.52f).clickable(enabled = enabled, role = Role.Button) {\n                expanded = !expanded\n            }',
    '.alpha(if (enabled) 1f else 0.52f).pointerInput(enabled) { detectTapGestures { if (enabled) expanded = !expanded } }.semantics { role = Role.Button }'
))
run_mutation('swallow Turnip import cancellation', lambda d: replace_once(
    d / main_rel,
    '''                val importFailure = try {
                    withContext(Dispatchers.IO) { installDriverBundleIo(sourceInfo) { FileInputStream(candidate.path) } }
                    null
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Throwable) {
                    error
                }
''',
    '''                val importFailure = runCatching {
                    withContext(Dispatchers.IO) { installDriverBundleIo(sourceInfo) { FileInputStream(candidate.path) } }
                }.exceptionOrNull()
'''
))
run_mutation('restore synchronous QR encoding', lambda d: replace_once(
    d / qr_rel,
    'val matrix by produceState<BitMatrix?>(initialValue = null, key1 = text) {',
    'val matrix = remember(text) {'
))
run_mutation('remove responsive file-manager header', lambda d: replace_once(
    d / main_rel,
    '    val atRoot = state.directoryPath == state.rootPath\n    val expandedTextLayout = preferExpandedTextLayout()\n',
    '    val atRoot = state.directoryPath == state.rootPath\n'
))
run_mutation('unrelated wording false-positive control', lambda d: replace_once(
    d / main_rel,
    'No Vulkan® report',
    'No Vulkan® report available'
), expect_fail=False)

print('PASS VulkanScope 1.3.5 negative mutations and false-positive control')
