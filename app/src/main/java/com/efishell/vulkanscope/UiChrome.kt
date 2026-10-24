@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import android.content.res.Configuration
import android.view.Surface
import android.view.KeyEvent as AndroidKeyEvent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
internal fun VulkanLazyPage(
    verticalSpacing: Dp,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val useTwoPaneLayout = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && configuration.screenWidthDp >= 640
    // Capture pager descriptors during Lazy DSL construction, not from a lazily
    // composed child. LazyColumn legitimately disposes off-screen items.
    val sourceItems = AdaptiveLazyListRecorder().also { content(it) }
    val adaptiveContent = if (useTwoPaneLayout) adaptiveTwoColumnContent(sourceItems) else content
    val pointerWheelScalePx = rememberPointerWheelScalePx()
    val focusManager = LocalFocusManager.current
    val tvNavigationScope = rememberCoroutineScope()
    val pageFocusRequester = remember { FocusRequester() }
    LaunchedEffect(listState) { runCatching { pageFocusRequester.requestFocus() } }
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val transientOverlayContentInset = LocalTransientOverlayContentInset.current
    val pinnedPagerContentInset = LocalPinnedPagerContentInset.current
    val coordinatedTransientOverlayInset by animateDpAsState(
        targetValue = transientOverlayContentInset,
        animationSpec = tween(OverlayCoordinationMotionMillis, easing = FastOutSlowInEasing),
        label = "coordinatedTopOverlayInset"
    )
    val headerContentInset = LocalAppHeaderContentInset.current
    val bottomNavigationContentInset = LocalBottomNavigationContentInset.current
    val horizontalNavigationStartInset = navigationPadding.calculateLeftPadding(layoutDirection)
    val horizontalNavigationEndInset = navigationPadding.calculateRightPadding(layoutDirection)
    val density = LocalDensity.current
    val headerBoundaryPx = with(density) { headerContentInset.roundToPx() }
    var maximumHeaderBoundaryPx by remember(listState) { mutableIntStateOf(headerBoundaryPx) }
    SideEffect { maximumHeaderBoundaryPx = maxOf(maximumHeaderBoundaryPx, headerBoundaryPx) }
    val stablePagerPinBoundaryPx = maxOf(headerBoundaryPx, maximumHeaderBoundaryPx)
    val pageTopContentInset = headerContentInset + PageChromeSeparation
    val glassPaddingPx = with(density) { 12.dp.roundToPx() }
    val overlayCoordinationLeadPx = with(density) { OverlayCoordinationLead.roundToPx() }
    val verticalSpacingPx = with(density) { verticalSpacing.roundToPx() }
    val pagerMeasuredHeights = remember(listState) { mutableStateMapOf<String, Int>() }
    val measuredHeightLookup = remember(pagerMeasuredHeights) { { key: String -> pagerMeasuredHeights[key] ?: 0 } }
    val measuredHeightReporter = remember(pagerMeasuredHeights) {
        { key: String, heightPx: Int ->
            if (heightPx > 0 && pagerMeasuredHeights[key] != heightPx) pagerMeasuredHeights[key] = heightPx
            Unit
        }
    }
    val pagerSource = sourceItems.collectionPagers.lastOrNull()
    val pagerCallback = rememberUpdatedState(pagerSource?.onPageChange)
    val stablePagerCallback: (Int) -> Unit = remember { { target -> pagerCallback.value?.invoke(target) } }
    val pagerRegistration = pagerSource?.let { spec ->
        val actualIndex = sourceItems.renderedPagerIndex(spec.key, useTwoPaneLayout)
        actualIndex?.let { index ->
            val reportedHeight = pagerMeasuredHeights[spec.key] ?: with(density) { 72.dp.roundToPx() }
            CollectionPagerRegistration(
                key = spec.key,
                itemIndex = index,
                heightPx = reportedHeight,
                totalItems = spec.totalItems,
                currentPage = spec.currentPage,
                pageSize = spec.pageSize,
                onPageChange = stablePagerCallback,
                onMeasuredHeight = remember(spec.key, measuredHeightReporter) {
                    { height: Int -> measuredHeightReporter(spec.key, height) }
                }
            )
        }
    }
    val sourceLayer = rememberGraphicsLayer()
    val blurredLayer = rememberGraphicsLayer()
    val reportChromeBackdrop = LocalChromeBackdropReporter.current
    var pageRootOffset by remember { mutableStateOf(Offset.Zero) }
    val blurRadiusPx = with(density) { 24.dp.toPx() }
    val glassRenderEffect = remember(blurRadiusPx) {
        AndroidRenderEffect.createBlurEffect(blurRadiusPx, blurRadiusPx, Shader.TileMode.CLAMP).asComposeRenderEffect()
    }
    LaunchedEffect(blurredLayer, glassRenderEffect) {
        blurredLayer.renderEffect = glassRenderEffect
    }
    LaunchedEffect(blurredLayer, pageRootOffset) {
        if (pageRootOffset.y <= 0.5f) reportChromeBackdrop(blurredLayer, pageRootOffset) else reportChromeBackdrop(blurredLayer, null)
    }
    DisposableEffect(blurredLayer) {
        onDispose { reportChromeBackdrop(blurredLayer, null) }
    }
    val headerVisible = LocalAppHeaderVisible.current
    val pinnedPager = pagerRegistration?.takeIf { registration ->
        val layout = listState.layoutInfo
        val pagerRow = layout.visibleItemsInfo.firstOrNull { it.key == registration.key }
        shouldPinCollectionPager(
            anchorItemIndex = registration.itemIndex,
            firstVisibleItemIndex = listState.firstVisibleItemIndex,
            totalItems = registration.totalItems,
            pageSize = registration.pageSize,
            visiblePagerOffsetPx = pagerRow?.offset,
            viewportStartOffsetPx = layout.viewportStartOffset,
            stablePinBoundaryPx = stablePagerPinBoundaryPx
        )
    }
    val pagerIsPinned = pinnedPager != null
    // One animated handoff drives BOTH versions of the same pager. Never remove
    // the overlay at the instant the pin predicate changes: that caused snapping.
    val pagerHandoffFraction by animateFloatAsState(
        targetValue = if (pagerIsPinned) 1f else 0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "collectionPagerDetachAttach"
    )
    val pagerHandoffTravelPx = with(density) { 10.dp.toPx() }
    val pagerBlurFraction by animateFloatAsState(
        targetValue = if (headerVisible) 1f else 0f,
        animationSpec = tween(durationMillis = CHROME_MOTION_MILLIS, easing = FastOutSlowInEasing),
        label = "pagerBlurIndependentOfPinnedPager"
    )
    val animatedPagerTop by animateFloatAsState(
        targetValue = headerBoundaryPx.toFloat(),
        animationSpec = tween(durationMillis = CHROME_MOTION_MILLIS, easing = FastOutSlowInEasing),
        label = "pinnedPagerTopPosition"
    )
    val reportStickyPagerState = LocalStickyPagerStateReporter.current
    val pinnedPagerKey = pagerRegistration?.let { "${it.key}@${System.identityHashCode(listState)}" }
    val animatedLaneExtent by animateIntAsState(
        targetValue = if (pagerIsPinned) pinnedPager!!.heightPx + glassPaddingPx else 0,
        animationSpec = tween(durationMillis = 245, easing = FastOutSlowInEasing),
        label = "pagerLaneJoinExtent"
    )
    SideEffect {
        if (pinnedPagerKey != null) {
            reportStickyPagerState(pinnedPagerKey, pagerIsPinned, animatedLaneExtent)
        }
    }
    DisposableEffect(pinnedPagerKey) {
        onDispose { if (pinnedPagerKey != null) reportStickyPagerState(pinnedPagerKey, false, 0) }
    }
    val scrollHintTopInset by animateDpAsState(
        targetValue = headerContentInset + pinnedPagerContentInset + transientOverlayContentInset + 10.dp,
        animationSpec = tween(OverlayCoordinationMotionMillis, easing = FastOutSlowInEasing),
        label = "scrollHintTopInset"
    )
    CompositionLocalProvider(
        LocalPrimaryLazyListState provides listState,
        LocalCollectionPagerPinned provides pagerIsPinned,
        LocalCollectionPagerHandoffFraction provides pagerHandoffFraction,
        LocalCollectionPagerMeasuredHeightLookup provides measuredHeightLookup,
        LocalCollectionPagerMeasuredHeightReporter provides measuredHeightReporter
    ) {
        Box(
            modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    val offset = coordinates.positionInRoot()
                    if (offset != pageRootOffset) pageRootOffset = offset
                }
        ) {
            LazyColumn(
                state = listState,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 18.dp + horizontalNavigationStartInset,
                    top = pageTopContentInset + coordinatedTransientOverlayInset,
                    end = 18.dp + horizontalNavigationEndInset,
                    bottom = bottomNavigationContentInset
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        sourceLayer.record { this@drawWithContent.drawContent() }
                        blurredLayer.record { drawLayer(sourceLayer) }
                        drawLayer(sourceLayer)
                    }
                    .desktopVerticalPointerScroll(listState, pointerWheelScalePx)
                    .focusRequester(pageFocusRequester)
                    .focusable()
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                        val direction = when (event.key.nativeKeyCode) {
                            AndroidKeyEvent.KEYCODE_DPAD_DOWN -> FocusDirection.Down
                            AndroidKeyEvent.KEYCODE_DPAD_UP -> FocusDirection.Up
                            else -> null
                        }
                        if (direction != null) {
                            if (focusManager.moveFocus(direction)) return@onPreviewKeyEvent true
                            val info = listState.layoutInfo
                            if (info.totalItemsCount <= 0) return@onPreviewKeyEvent false
                            val target = if (direction == FocusDirection.Down) {
                                ((info.visibleItemsInfo.lastOrNull()?.index ?: listState.firstVisibleItemIndex) + 1).coerceAtMost(info.totalItemsCount - 1)
                            } else {
                                ((info.visibleItemsInfo.firstOrNull()?.index ?: listState.firstVisibleItemIndex) - 1).coerceAtLeast(0)
                            }
                            if (target == listState.firstVisibleItemIndex && !listState.canScrollBackward && direction == FocusDirection.Up) return@onPreviewKeyEvent false
                            if (target == info.totalItemsCount - 1 && !listState.canScrollForward && direction == FocusDirection.Down) return@onPreviewKeyEvent false
                            tvNavigationScope.launch {
                                listState.animateScrollToItem(target)
                                delay(24L)
                                focusManager.moveFocus(direction)
                            }
                            return@onPreviewKeyEvent true
                        }
                        when (event.key.nativeKeyCode) {
                            AndroidKeyEvent.KEYCODE_PAGE_DOWN -> {
                                val info = listState.layoutInfo
                                if (info.totalItemsCount <= 0 || !listState.canScrollForward) false else {
                                    val target = (info.visibleItemsInfo.lastOrNull()?.index ?: listState.firstVisibleItemIndex).coerceAtMost(info.totalItemsCount - 1)
                                    tvNavigationScope.launch { listState.animateScrollToItem(target) }
                                    true
                                }
                            }
                            AndroidKeyEvent.KEYCODE_PAGE_UP -> {
                                if (!listState.canScrollBackward) false else {
                                    val target = (listState.firstVisibleItemIndex - (listState.layoutInfo.visibleItemsInfo.size.coerceAtLeast(1) - 1)).coerceAtLeast(0)
                                    tvNavigationScope.launch { listState.animateScrollToItem(target) }
                                    true
                                }
                            }
                            else -> false
                        }
                    }
                    .focusGroup(),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                userScrollEnabled = true,
                content = adaptiveContent
            )
            val handoffPager = pagerRegistration?.takeIf { pagerHandoffFraction > 0.001f }
            val pagerGlassHeight = handoffPager?.let { with(density) { (animatedPagerTop + it.heightPx + glassPaddingPx).toDp() } } ?: 0.dp
            if (handoffPager != null && pagerBlurFraction > 0.001f) {
                Box(
                    Modifier.align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(pagerGlassHeight)
                        .clipToBounds()
                        .graphicsLayer { alpha = pagerBlurFraction * pagerHandoffFraction }
                        .drawWithContent {
                            val glassTop = headerBoundaryPx.toFloat().coerceIn(0f, size.height)
                            if (glassTop < size.height) {
                                clipRect(top = glassTop) {
                                    drawLayer(blurredLayer)
                                    drawRect(
                                        color = VulkanGlassTint,
                                        topLeft = Offset(0f, glassTop),
                                        size = Size(size.width, size.height - glassTop)
                                    )
                                }
                            }
                            drawContent()
                        }
                        .zIndex(7f)
                )
            }
            if (handoffPager != null) {
                val registration = handoffPager
                Surface(
                    color = ComposeColor.Transparent,
                    contentColor = VulkanTextPrimary,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(start = 18.dp + horizontalNavigationStartInset, end = 18.dp + horizontalNavigationEndInset)
                        .offset { IntOffset(0, (animatedPagerTop + (1f - pagerHandoffFraction) * pagerHandoffTravelPx).toInt()) }
                        .graphicsLayer { alpha = pagerHandoffFraction }
                        .zIndex(9f)
                ) {
                    Box(
                        Modifier.fillMaxWidth()
                            .onSizeChanged { size -> if (size.height > 0 && size.height != registration.heightPx) registration.onMeasuredHeight(size.height) }
                    ) {
                        PinnedPagerGlassSurface(
                            blurredLayer = blurredLayer,
                            originX = with(density) { (18.dp + horizontalNavigationStartInset).toPx() },
                            originY = animatedPagerTop,
                            blurVisibility = pagerBlurFraction
                        ) {
                            CollectionPager(
                                totalItems = registration.totalItems,
                                currentPage = registration.currentPage,
                                onPageChange = { targetPage ->
                                    registration.onPageChange(targetPage)
                                },
                                pageSize = registration.pageSize
                            )
                        }
                    }
                }
            }
            ExpressiveScrollHints(
                listState,
                Modifier
                    .fillMaxSize()
                    .zIndex(10f)
                    .padding(
                        start = 12.dp + horizontalNavigationStartInset,
                        top = scrollHintTopInset,
                        end = 12.dp + horizontalNavigationEndInset,
                        bottom = bottomNavigationContentInset + 4.dp
                    )
            )
        }
    }
}

@Composable
internal fun SoftScrollIntersectionShadows(
    showTop: Boolean,
    showBottom: Boolean,
    modifier: Modifier = Modifier,
    edgeColor: ComposeColor = VulkanBlack
) {
    Box(modifier) {
        AnimatedVisibility(
            visible = showTop,
            enter = fadeIn(tween(170)),
            exit = fadeOut(tween(140)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                edgeColor.copy(alpha = 0.96f),
                                edgeColor.copy(alpha = 0.72f),
                                edgeColor.copy(alpha = 0.30f),
                                ComposeColor.Transparent
                            )
                        )
                    )
            )
        }
        AnimatedVisibility(
            visible = showBottom,
            enter = fadeIn(tween(170)),
            exit = fadeOut(tween(140)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                ComposeColor.Transparent,
                                edgeColor.copy(alpha = 0.28f),
                                edgeColor.copy(alpha = 0.70f),
                                edgeColor.copy(alpha = 0.96f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
internal fun CollectionPager(totalItems: Int, currentPage: Int, onPageChange: (Int) -> Unit, pageSize: Int = COLLECTION_PAGE_SIZE) {
    val pageCount = maxOf(1, (totalItems + pageSize - 1) / pageSize)
    if (pageCount <= 1) return
    val focusManager = LocalFocusManager.current
    var pageField by remember { mutableStateOf(TextFieldValue((currentPage + 1).toString())) }
    var pageFieldFocused by remember { mutableStateOf(false) }
    var suppressFocusCommit by remember { mutableStateOf(false) }
    fun commitPageSelection() {
        val requested = pageField.text.toIntOrNull()
        val resolvedPage = when {
            requested == null -> currentPage + 1
            requested < 1 -> 1
            requested > pageCount -> pageCount
            else -> requested
        }
        pageField = TextFieldValue(resolvedPage.toString())
        if (resolvedPage - 1 != currentPage) onPageChange(resolvedPage - 1)
    }
    fun requestPageChange(targetPage: Int) {
        val bounded = targetPage.coerceIn(0, pageCount - 1)
        suppressFocusCommit = true
        pageField = TextFieldValue((bounded + 1).toString())
        focusManager.clearFocus(force = true)
        pageFieldFocused = false
        suppressFocusCommit = false
        if (bounded != currentPage) onPageChange(bounded)
    }
    LaunchedEffect(currentPage, pageCount, pageFieldFocused) {
        if (!pageFieldFocused) pageField = TextFieldValue((currentPage + 1).coerceIn(1, pageCount).toString())
    }
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = VulkanPagerSurface,
        contentColor = VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant.copy(alpha = 0.82f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { if (currentPage > 0) requestPageChange(currentPage - 1) },
                    enabled = currentPage > 0,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = VulkanAccentContainer,
                        contentColor = VulkanTextPrimary,
                        disabledContainerColor = VulkanSurfaceTonal,
                        disabledContentColor = VulkanTextMuted
                    )
                ) {
                    Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = "Previous page")
                }
                Box(Modifier.width(72.dp), contentAlignment = Alignment.Center) {
                    OutlinedTextField(
                        value = pageField,
                        onValueChange = { value ->
                            val candidate = value.text
                            when {
                                candidate.isEmpty() -> pageField = value
                                candidate.all { it.isDigit() } && !(candidate.length > 1 && candidate.startsWith('0')) && (candidate.toIntOrNull() ?: 0) in 1..pageCount -> pageField = value
                            }
                        },
                        modifier = Modifier.fillMaxWidth().onFocusChanged { focus ->
                            val wasFocused = pageFieldFocused
                            pageFieldFocused = focus.isFocused
                            if (focus.isFocused) {
                                pageField = pageField.copy(selection = TextRange(0, pageField.text.length))
                            } else if (wasFocused && !suppressFocusCommit) {
                                if (pageField.text.isBlank()) pageField = TextFieldValue((currentPage + 1).toString())
                                commitPageSelection()
                            }
                        },
                        singleLine = true,
                        label = { Text("Page") },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            commitPageSelection()
                            focusManager.clearFocus(force = true)
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VulkanAccentSoft,
                            unfocusedBorderColor = VulkanOutline,
                            focusedTextColor = VulkanTextPrimary,
                            unfocusedTextColor = ComposeColor.Transparent,
                            cursorColor = VulkanAccentSoft
                        )
                    )
                    if (!pageFieldFocused) {
                        Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                            AnimatedContent(
                                targetState = (currentPage + 1).coerceIn(1, pageCount),
                                transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(130)) },
                                label = "pageNumberTransition"
                            ) { animatedNumber ->
                                Text(animatedNumber.toString(), color = VulkanTextPrimary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.offset(y = 4.dp))
                            }
                        }
                    }
                }
                Text("/ $pageCount", color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium)
                IconButton(
                    onClick = { if (currentPage + 1 < pageCount) requestPageChange(currentPage + 1) },
                    enabled = currentPage + 1 < pageCount,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = VulkanAccentContainer,
                        contentColor = VulkanTextPrimary,
                        disabledContainerColor = VulkanSurfaceTonal,
                        disabledContentColor = VulkanTextMuted
                    )
                ) {
                    Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Next page")
                }
            }
        }
    }
}



internal fun LazyListScope.stickyCollectionPager(
    totalItems: Int,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    pageSize: Int = COLLECTION_PAGE_SIZE,
    pagerKey: String = "collection-pager"
) {
    if (totalItems <= pageSize) return
    if (this is AdaptiveLazyListRecorder) {
        registerPager(RecordedCollectionPager(pagerKey, totalItems, currentPage, pageSize, onPageChange))
    }
    item(key = pagerKey) {
        val reportHeight = LocalCollectionPagerMeasuredHeightReporter.current
        // The inline pager remains real content during the entire transition.
        // A binary Spacer swap would make detach/attach abrupt and leave a black hole.
        val handoff = LocalCollectionPagerHandoffFraction.current
        Box(
            Modifier.fillMaxWidth()
                .onSizeChanged { size -> if (size.height > 0) reportHeight(pagerKey, size.height) }
                .padding(3.dp)
                .graphicsLayer { alpha = (1f - handoff).coerceIn(0f, 1f) }
        ) {
            CollectionPager(
                totalItems = totalItems,
                currentPage = currentPage,
                onPageChange = onPageChange,
                pageSize = pageSize
            )
        }
    }
}

@Composable
internal fun ScrollBoundaryIndicators(listState: LazyListState, modifier: Modifier = Modifier) {
    val showUp by remember(listState) { derivedStateOf { listState.canScrollBackward } }
    val showDown by remember(listState) { derivedStateOf { listState.canScrollForward } }
    val visibilityKey by remember(listState) {
        derivedStateOf { Triple(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset, listState.layoutInfo.totalItemsCount) }
    }
    val visible = rememberScrollIndicatorVisibility(visibilityKey, showUp || showDown, listState.isScrollInProgress)
    ScrollBoundaryIndicatorColumn(showUp, showDown, visible, modifier)
}

@Composable
internal fun ScrollBoundaryIndicators(gridState: LazyGridState, modifier: Modifier = Modifier) {
    val showUp by remember(gridState) { derivedStateOf { gridState.canScrollBackward } }
    val showDown by remember(gridState) { derivedStateOf { gridState.canScrollForward } }
    val visibilityKey by remember(gridState) {
        derivedStateOf { Triple(gridState.firstVisibleItemIndex, gridState.firstVisibleItemScrollOffset, gridState.layoutInfo.totalItemsCount) }
    }
    val visible = rememberScrollIndicatorVisibility(visibilityKey, showUp || showDown, gridState.isScrollInProgress)
    ScrollBoundaryIndicatorColumn(showUp, showDown, visible, modifier)
}

@Composable
internal fun ScrollBoundaryIndicators(scrollState: ScrollState, modifier: Modifier = Modifier) {
    val showUp by remember(scrollState) { derivedStateOf { scrollState.value > 0 } }
    val showDown by remember(scrollState) { derivedStateOf { scrollState.value < scrollState.maxValue } }
    val visible = rememberScrollIndicatorVisibility(scrollState.value to scrollState.maxValue, showUp || showDown, scrollState.isScrollInProgress)
    ScrollBoundaryIndicatorColumn(showUp, showDown, visible, modifier)
}

@Composable
internal fun ExpressiveScrollHints(listState: LazyListState, modifier: Modifier = Modifier) = ScrollBoundaryIndicators(listState, modifier)

@Composable
internal fun ExpressiveScrollHints(gridState: LazyGridState, modifier: Modifier = Modifier) = ScrollBoundaryIndicators(gridState, modifier)

@Composable
internal fun ExpressiveScrollHints(scrollState: ScrollState, modifier: Modifier = Modifier) = ScrollBoundaryIndicators(scrollState, modifier)

@Composable
internal fun rememberScrollIndicatorVisibility(triggerKey: Any, hasScrollableDirection: Boolean, isScrollInProgress: Boolean): Boolean {
    var visible by remember { mutableStateOf(hasScrollableDirection) }
    LaunchedEffect(triggerKey, hasScrollableDirection, isScrollInProgress) {
        if (!hasScrollableDirection) {
            visible = false
        } else {
            visible = true
            delay(if (isScrollInProgress) 1050 else 900)
            visible = false
        }
    }
    return visible
}

@Composable
internal fun ScrollBoundaryIndicatorColumn(showUp: Boolean, showDown: Boolean, visible: Boolean, modifier: Modifier = Modifier) {
    Box(modifier.zIndex(10f)) {
        AnimatedVisibility(
            visible = visible && showUp,
            modifier = Modifier.align(Alignment.TopCenter),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ScrollBoundaryIndicatorBubble(up = true)
        }
        AnimatedVisibility(
            visible = visible && showDown,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ScrollBoundaryIndicatorBubble(up = false)
        }
    }
}

@Composable
internal fun ScrollBoundaryIndicatorBubble(up: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = VulkanAccentContainer.copy(alpha = 0.84f),
        tonalElevation = 4.dp,
        shadowElevation = 3.dp
    ) {
        Icon(
            painter = painterResource(if (up) R.drawable.ic_scroll_up else R.drawable.ic_scroll_down),
            contentDescription = null,
            tint = VulkanTextPrimary,
            modifier = Modifier.padding(8.dp).size(24.dp)
        )
    }
}

@Composable
internal fun PageContent(
    page: Page,
    report: VulkanReport,
    display: DisplayReport,
    driverMode: DriverMode,
    turnipSupport: TurnipSupport,
    turnipManagerRevision: Int,
    turnipManagerBusy: Boolean,
    storagePermissionDeniedFeedback: Boolean,
    selectedDeviceIndex: Int,
    onDriverModeChanged: (DriverMode) -> Unit,
    onInstallDriverBundle: () -> Unit,
    onActivateTurnipDriver: (Int) -> Unit,
    onRemoveTurnipDriver: (Int) -> Unit,
    onCheckForUpdates: () -> Unit,
    updateCheckInFlight: Boolean,
    directUpdatesEnabled: Boolean,
    onDirectUpdatesChanged: (Boolean) -> Unit,
    openingAnimationEnabled: Boolean,
    onOpeningAnimationChanged: (Boolean) -> Unit,
    collectionStatus: CollectionStatus,
    onNavigate: (Page) -> Unit,
    settingsSection: SettingsSection?,
    onSettingsSectionChanged: (SettingsSection?) -> Unit,
    surfaceSection: SurfaceSection?,
    onSurfaceSectionChanged: (SurfaceSection?) -> Unit,
    onRequestQuery: (String) -> Unit,
    queryTimingMs: Map<String, Long>,
    encyclopediaSeed: String
) {
    val device = report.devices.getOrNull(selectedDeviceIndex) ?: report.devices.firstOrNull()
    when (page) {
        Page.Overview -> OverviewPage(report, device, display, driverMode, onNavigate)
        Page.Vulkan -> VulkanPage(report, device, turnipSupport)
        Page.Display -> DisplayPage(display, device)
        Page.Surface -> SurfaceDestinationPage(display, device, surfaceSection, onSurfaceSectionChanged)
        Page.Features -> FeaturesPage(device)
        Page.Memory -> MemoryPage(device)
        Page.Queues -> QueuesPage(device)
        Page.Video -> VulkanVideoPage(device)
        Page.Formats -> FormatsPage(device)
        Page.Properties -> PropertiesPage(device, onRequestQuery)
        Page.Extensions -> ExtensionsPage(report, device)
        Page.Profiles -> ProfilesPage(report, device)
        Page.Encyclopedia -> EncyclopediaPage(encyclopediaSeed, report, device)
        Page.Analysis -> AnalysisPage(report, device, display, driverMode, turnipSupport, collectionStatus, queryTimingMs, onDriverModeChanged)
        Page.Settings -> SettingsPage(
            report = report,
            display = display,
            mode = driverMode,
            turnipSupport = turnipSupport,
            turnipManagerRevision = turnipManagerRevision,
            turnipManagerBusy = turnipManagerBusy,
            storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
            onModeChanged = onDriverModeChanged,
            onInstallDriverBundle = onInstallDriverBundle,
            onActivateTurnipDriver = onActivateTurnipDriver,
            onRemoveTurnipDriver = onRemoveTurnipDriver,
            collectionStatus = collectionStatus,
            directUpdatesEnabled = directUpdatesEnabled,
            onDirectUpdatesChanged = onDirectUpdatesChanged,
            openingAnimationEnabled = openingAnimationEnabled,
            onOpeningAnimationChanged = onOpeningAnimationChanged,
            onCheckForUpdates = onCheckForUpdates,
            updateCheckInFlight = updateCheckInFlight,
            selectedSection = settingsSection,
            onSectionSelected = onSettingsSectionChanged
        )
        Page.Info -> SettingsPage(
            report = report,
            display = display,
            mode = driverMode,
            turnipSupport = turnipSupport,
            turnipManagerRevision = turnipManagerRevision,
            turnipManagerBusy = turnipManagerBusy,
            storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
            onModeChanged = onDriverModeChanged,
            onInstallDriverBundle = onInstallDriverBundle,
            onActivateTurnipDriver = onActivateTurnipDriver,
            onRemoveTurnipDriver = onRemoveTurnipDriver,
            collectionStatus = collectionStatus,
            directUpdatesEnabled = directUpdatesEnabled,
            onDirectUpdatesChanged = onDirectUpdatesChanged,
            openingAnimationEnabled = openingAnimationEnabled,
            onOpeningAnimationChanged = onOpeningAnimationChanged,
            onCheckForUpdates = onCheckForUpdates,
            updateCheckInFlight = updateCheckInFlight,
            selectedSection = SettingsSection.INFO,
            onSectionSelected = { }
        )
    }
}
