#!/usr/bin/env python3
import argparse
from pathlib import Path

parser=argparse.ArgumentParser()
parser.add_argument('--root',type=Path,default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version',action='store_true')
args=parser.parse_args(); root=args.root.resolve(); errors=[]
main=(root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
rules=(root/'rules/PROJECT_RULES.md').read_text(encoding='utf-8')

def need(cond,msg):
    if not cond: errors.append(msg)

def block(start_token,end_token):
    start=main.find(start_token)
    if start<0: return ''
    end=main.find(end_token,start+len(start_token))
    return main[start:] if end<0 else main[start:end]

if not args.skip_version:
    need('versionCode = 815' in gradle and 'versionName = "0.80.15"' in gradle,'release identity is not 0.80.15/815')

page=block('private fun VulkanLazyPage(', '@Composable\nprivate fun ScrollBoundaryIndicators')
need('end = 18.dp' in page,'top-level page still reserves the old indicator lane')
need('end = 46.dp' not in page,'old 46dp indicator lane remains')
need('ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp))' in page,'top-level overlay indicator placement missing')

indicator=block('private fun ScrollBoundaryIndicators(listState: LazyListState', '@Composable\nprivate fun PageContent')
for token in ['listState.canScrollBackward','listState.canScrollForward','listState.isScrollInProgress','scrollState.isScrollInProgress','rememberScrollIndicatorVisibility','delay(900)','visible = false','visible = visible && showUp','visible = visible && showDown','Modifier.align(Alignment.TopCenter)','Modifier.align(Alignment.BottomCenter)','enter = fadeIn()','exit = fadeOut()']:
    need(token in indicator,f'scroll activity/boundary presentation missing: {token}')
need('Modifier.padding(8.dp).size(24.dp)' in indicator,'scroll arrows must use the required larger 24dp geometry')
need('contentDescription = null' in indicator,'scroll arrows must remain decorative')

detail=block('private fun ExpressiveDetailDialog(', 'private val FORMAT_USAGE_FILTERS')
for token in ['Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false))','widthIn(max = 560.dp)','shape = MaterialTheme.shapes.extraLarge','horizontalMargin = if (configuration.screenWidthDp < 360) 10.dp else 18.dp','verticalMargin = if (configuration.screenHeightDp < 520) 8.dp else 16.dp','CompositionLocalProvider(LocalDetailKeyValuePresentation provides true)','ExpressiveScrollHints(scrollState, Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp))']:
    need(token in detail,f'responsive detail dialog requirement missing: {token}')
need('bodyMaxHeight = minOf(540.dp, maxOf(140.dp, (configuration.screenHeightDp - 200).dp))' in detail or ('val dialogMaxHeight =' in detail and 'bodyMaxHeight = minOf(540.dp, maxOf(96.dp, dialogMaxHeight - 170.dp))' in detail and '.weight(1f, fill = false)' in detail), 'responsive detail dialog bounded-body requirement missing')
need('ExpressiveTextButton("Close")' in detail or 'ExpressiveContainedTextButton("Close")' in detail or 'ExpressiveContainedIconTextButton("Close", R.drawable.ic_close)' in detail,'responsive detail dialog Close action missing')
need('padding(end = 38.dp)' not in detail,'detail dialog still reserves the old side lane')

kv=block('private fun ExpressiveEvidenceRow(key: String, value: String)', '@Composable\nprivate fun CapabilityKeyValue')
for token in ['if (detailPresentation)','maxWidth < 420.dp','key.length > 22','value.length > 30','MaterialTheme.shapes.medium','androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)','color = VulkanSurfaceTonal']:
    need(token in kv,f'grouped responsive detail row requirement missing: {token}')
need('ComposeColor.Transparent else VulkanSurfaceTonal' not in kv,'flat transparent detail-row regression remains')
need('TextAlign.End' not in kv,'detail values must not be right-aligned into ragged wrapping')

explore=block('private fun ExploreDestinationTile(', '@OptIn(ExperimentalMaterial3Api::class)')
for token in ['BoxWithConstraints(Modifier.fillMaxWidth())','maxWidth < 300.dp','maxWidth < 620.dp','pages.chunked(columns)','ExploreDestinationTile']:
    need(token in explore,f'Explore adaptive grid requirement missing: {token}')
need('horizontalScroll(rememberScrollState())' not in explore,'Explore still relies on a clipped horizontal strip')

overview=block('private fun OverviewPage(', 'private const val ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT')
for token in ['BoxWithConstraints(Modifier.fillMaxWidth())','maxWidth < 540.dp -> 2','maxWidth < 780.dp -> 3','quickAccessItems.chunked(quickAccessColumns)']:
    need(token in overview,f'Quick access adaptive grid requirement missing: {token}')

release=block('private fun ReleaseNotesContent(', '@Composable\nprivate fun ReleaseNoteLine')
need('modifier = Modifier.fillMaxWidth().focusGroup(),' in release,'release notes still reserve an indicator lane')
need('padding(end = 28.dp)' not in release,'old release-note side lane remains')
need('ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp))' in release,'release-note overlay indicator missing')

for token in ['MaterialExpressiveTheme(','motionScheme = MotionScheme.expressive()','import androidx.compose.ui.semantics.role','role = Role.Button']:
    need(token in main,f'retained expressive/accessibility invariant missing: {token}')
need('## Release 0.80.15 responsive overlay-scroll and detail-dialog requirements' in rules,'PROJECT_RULES 0.80.15 contract missing')
need((root/'rules/0.80.15_RESPONSIVE_OVERLAY_DIALOG_UI_AUDIT.md').is_file(),'0.80.15 UI audit missing')
need((root/'tests/golden/0.80.14_regression_contract.json').is_file(),'0.80.14 regression contract missing')

if errors:
    for e in errors: print('FAIL:',e)
    raise SystemExit(1)
print('PASS VulkanScope 0.80.15 responsive overlay-scroll/detail-dialog contract')
