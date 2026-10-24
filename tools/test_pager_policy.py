from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
source = ROOT/'app/src/main/java/com/efishell/vulkanscope/PinnedPagerPolicy.kt'
text = source.read_text(encoding='utf-8')
assert 'collectionPagerPassedAnchor(' in text
assert 'projectedPagerRowIndex(' in text
assert 'headerVisible' not in text and 'headerBoundaryPx' not in text and 'layoutItemCount' not in text

compiler = shutil.which('kotlinc')
java = shutil.which('java')
if compiler is None or java is None:
    raise RuntimeError('Kotlin/JVM compiler is required for the pager policy behavior test')

with tempfile.TemporaryDirectory() as temporary:
    tmp = Path(temporary)
    test = tmp/'PinnedPagerPolicyTest.kt'
    test.write_text('''package com.efishell.vulkanscope

fun main() {
    check(!collectionPagerPassedAnchor(4, 3, 100, 20))
    check(collectionPagerPassedAnchor(4, 4, 100, 20))
    check(collectionPagerPassedAnchor(4, 9, 100, 20))
    check(!collectionPagerPassedAnchor(-1, 6, 100, 20))
    check(!collectionPagerPassedAnchor(4, 6, 20, 20))

    // Below the pin boundary: the in-list pager must remain interactive.
    fun pinned(first: Int, offset: Int?, boundary: Int = 280): Boolean =
        shouldPinCollectionPager(
            anchorItemIndex = 4,
            firstVisibleItemIndex = first,
            totalItems = 479,
            pageSize = 25,
            visiblePagerOffsetPx = offset,
            viewportStartOffsetPx = 0,
            stablePinBoundaryPx = boundary
        )
    check(!pinned(2, 700))
    check(!pinned(3, 281))
    check(pinned(3, 280))
    check(pinned(4, 120))
    check(pinned(6, null)) // The original pager left lazy composition.
    check(!pinned(2, null)) // Reverse scroll: show the real inline row.
    // Header visibility is never an input to shouldPinCollectionPager.
    check(pinned(6, null, 280) == pinned(6, null, 280))
    check(!shouldPinCollectionPager(4, 8, 25, 25, null, 0, 280))
    check(!shouldPinCollectionPager(-1, 8, 479, 25, null, 0, 280))
    for (offset in -100..1000 step 25) {
        val actual = pinned(3, offset)
        check(actual == (offset <= 280))
        val inlineMounted = !actual
        val overlayMounted = actual
        check(inlineMounted != overlayMounted)
    }

    fun tile(count: Int, standalone: Boolean = false, fullWidth: Boolean = false, pager: Boolean = false) =
        PagerProjectionSegment(count, standalone, fullWidth, pager)

    val page = listOf(
        tile(1,true), tile(1,true), tile(1,true), tile(1,true),
        tile(1,true,true,true), tile(27), tile(1,true,true), tile(1,true)
    )
    check(projectedPagerRowIndex(page, twoPane = true) == 2)
    check(projectedPagerRowIndex(page, twoPane = false) == 4)
    check(projectedPagerRowIndex(page.take(4), twoPane = true) == null)

    // Uneven singleton layouts and arbitrary lazy-list interval lengths.
    for (singles in 0..15) {
        for (bulk in 0..55) {
            val parts = List(singles) { tile(1,true) } +
                (if (bulk > 0) listOf(tile(bulk)) else emptyList()) + listOf(tile(1,true,true,true))
            val expectedLandscape = (singles + 1)/2 + (bulk + 1)/2
            val expectedPortrait = singles + bulk
            check(projectedPagerRowIndex(parts, true) == expectedLandscape)
            check(projectedPagerRowIndex(parts, false) == expectedPortrait)
        }
    }
    // Structural groups flush a pending singleton before the pager.
    val structural = listOf(tile(1,true),tile(1,true,true),tile(1,true,true,true))
    check(projectedPagerRowIndex(structural,true) == 2)
    check(projectedPagerRowIndex(structural,false) == 2)
    println("PASS pager row mapping and anchor policy (real Kotlin/JVM)")
}
''', encoding='utf-8')
    output = tmp/'pager-tests.jar'
    subprocess.run([compiler,str(source),str(test),'-include-runtime','-d',str(output)],check=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE,timeout=90)
    subprocess.run([java,'-jar',str(output)],check=True,timeout=30)
print('PASS pager policy Kotlin/JVM behavior tests')
