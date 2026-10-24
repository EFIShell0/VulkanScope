# VulkanScope 4.0.14 — video-led pager and category navigation fixes

## Video review

The full 14.97-second desktop/Android recording from October 23, 2026 was reviewed frame by frame. On the Encyclopedia page, the floating collection pager intermittently disappears or jumps during scroll (roughly 4–10.5 seconds). At the end of the recording, Settings shows two centered destination columns plus the centered third card, but Surface displays three full-width single-column destinations (roughly 11–14.5 seconds).

## Pager root-cause correction

The former pager registration lived in the `stickyCollectionPager` placeholder's composable body. Since lazy lists dispose rows when off-screen, this left registration lifetime tied to viewport visibility while the overlay relied on stale registration/retention state. Landscape pairing also changes the actual lazy row index.

Version 4.0.14 now records the pager's identity, page data and callback while building the lazy item DSL, before viewport-based composition. The projected row index is calculated by the same policy as the two-column list transformation, and a pure Kotlin regression check validates the mapping. The overlay remains independent of the app header; only the frosted/glass opacity follows its 320 ms transition.

## Settings and Surface consistency

Both category destinations now share `SectionDestinationGrid.kt`. On landscape layouts with adequate width, rows contain two balanced cards and the third odd card is centered; on portrait they stack vertically. The same animated destination card style and width cap apply to both.

## Unchanged

Vulkan registry/header baseline 1.4.366; API 37/minor 2 build settings; 64-bit and universal APK variants; 3-second automatic app-bar hide; Analysis tests; encyclopedia filter; Database backend untouched.

## Verification limits

The source gate and standalone Kotlin JVM pager-index test have been run in the packaging environment. A complete Android Gradle build and real-device scroll/blur verification remain mandatory and are not claimed as complete.

## In-list pager restoration

The blank-gap defect is addressed by removing the unconditional empty `Spacer` in the source page row. Below the pin line, `stickyCollectionPager` now renders the actual `CollectionPager` and records its measured height. Above the pin line, the overlay takes over and the original row reserves exactly the measured height. The decision is centralized in `shouldPinCollectionPager` and is independent of app-header visibility. The pager blur layers remain separate, smoothly reversible animations.

This source-side fix needs Android device verification before claiming that the real-world visual regression is fully resolved.
