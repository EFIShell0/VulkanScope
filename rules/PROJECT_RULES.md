# VulkanScope 4.0.15 authoritative engineering policy

## Current release lock

- Version name 4.0.15 / code 4015. Registry/header lock: Vulkan 1.4.366. Android compile SDK 37 minor API 2, target SDK 37, minimum SDK 31. Release ABIs are universal, arm64-v8a and x86_64; no ARMv7 or x86 32-bit package.
- Gradle, Android Gradle Plugin and Kotlin versions must agree with the checked-in wrapper/build scripts. Use supported APIs for release APK names `vulkanscope-4.0.15-<abi>.apk`.
- The separate VulkanScope Database 4.0.6 Worker accepts new complete technical report submissions **only** when producer `version` is `4.0.15` and `versionCode` is `4015`. Historical stored reports remain readable; a source-only change does not deploy the Worker.
- The app submits schema-2 envelopes containing schema-v3 technical reports. The fixed official HTTPS endpoint is immutable in the runtime UI. No background/automatic report uploads or per-capability submission omissions are allowed.

## Runtime evidence and collection safety

- Query the active Vulkan loader and selected physical device; do not infer hardware capabilities from GPU marketing names, Android OS, registry tokens or claimed API version alone.
- Keep loader version, device API version and driver version distinct. Do not fabricate supported values for unknown, unattempted, unavailable or not-applicable queries. `VK_INCOMPLETE` denotes partial positive evidence rather than a complete enumeration.
- Use the locked Vulkan-Headers v1.4.366 and authoritative Khronos registry/specifications; generated code and C++ query catalogs must match the checked-in registry hash. No runtime registry or header downloads.
- Preserve the declared validated native coverage count of 305 physical-device extension providers. The registry exposes 307 potentially queryable providers; the two additional unvalidated providers must remain explicitly marked as pending native implementation and cannot be silently claimed as covered.
- Feature/property struct layout, `sType`, exact enum/flag values and queue/memory/surface enumeration bounds must come from validated definitions, never guesses. Handle driver-controlled enumeration counts defensively, with bounded retries and returned-count validation.
- Do not confuse Surface color-space support with Android display gamut/HDR measurements; only show luminance evidence that Android exposes. A destroyed Surface must not be used for native calls.
- JNI/native Vulkan handles must have deterministic ownership, valid destruction order and bounded publication. Never perform expensive/blocking native queries on the main UI thread. Respect isolated query-process lifecycle, cancellation and resource budgets.
- Complete reports must preserve all applicable evidence and explicit state categories. Never silently discard or truncate fields to satisfy a 2 MiB Database transport ceiling.

## Modular source organization

- Maintain distinct modules for Activity lifecycle, Compose root/chrome, screen-specific presentation, native collector, registry models, scoring, report export/submission and shared UI components.
- Do not reintroduce report serialization or HTTP logic into Extensions or Encyclopedia screen files. The Analysis Score algorithm belongs in `CapabilityEvidenceScore.kt`; positive selected-device Encyclopedia filtering belongs in `EncyclopediaDeviceEvidence.kt`.
- `EvidenceCards.kt`, shared design surfaces and adaptive layouts must be reused for compatible screens rather than duplicated with conflicting appearance rules.
- Keep source identifiers and filenames descriptive. Remove genuinely obsolete material only after reference checks; preserve dynamically referenced JNI entry points, generated Vulkan symbols/catalogs and third-party license contents.
- Avoid unexplained, stale code comments and previous-version release notes in active documentation. Source code comments are not permitted by this project's source policy. Do not rename or edit user-provided image/logo/screenshot assets for documentation cleanup.
- Do not expose unnecessary network permissions or new runtime network paths. Allowed app networking is the official update flow, explicit Database submission/lookup, and user-invoked documentation links. APK download/install must require explicit user action.

## Pager, app chrome and blur — final 4.0.15 contract

- The pager source row renders a **real interactive CollectionPager** before pinning. It must never be a permanently blank `Spacer`; a measured spacer is allowed only while the fixed presentation genuinely owns the pinned pager.
- `VulkanLazyPage` determines pin eligibility from stable LazyList scroll position and the actual projected pager row key/index; eligibility must not depend on viewport-dependent item composition, transient item counts or the visibility/animated height of the app header.
- The inline and floating pager presentations share a **280 ms FastOutSlowInEasing animated handoff** with complementary opacity and small translation. Do not abruptly cut either presentation away at the attach/detach threshold; retain the outgoing presentation for its reverse transition. Do not leave a large black gap between filter/header and results.
- Once the inline row reaches the sticky boundary, the pinned pager must remain usable, including after the original lazy row leaves composition and when the top/bottom app bars hide. Scrolling back below the boundary releases the pin and animates the inline copy back to its original row.
- Preserve the original full-width frosted blur band and clipped pager glass. Their opacity changes smoothly over **320 ms** when the app header auto-hides/reappears; pager control visibility and interactivity do **not** depend on that blur fraction. No residual blurred corner or permanent band may be left after the blur is fully hidden.
- The idle auto-hide timeout is **3 seconds**. Only the app-owned header and floating bottom navigation hide; Android status/navigation bars, notifications, network/update banners and modal dialogs must remain unaffected. Persistent master, header and navigation switches default to enabled and appear under Drivers & Preferences beneath Opening animation with distinct icons.
- Regression checks must cover before/at/beyond sticky threshold, lazy-item disposal, both animation directions, app-bar auto-hide during pinning, orientation changes and stable row mapping under two-column projection. A Python text test or isolated Kotlin/JVM policy test is not evidence of on-device Compose animation correctness.

## Landscape, dialogs and accessible controls

- In sufficiently wide landscape layouts, consecutive normal singleton and repeated list results form ordered left/right cards without eager expansion of large datasets. Structural headings, filters and pager rows remain individually keyed and full width.
- Center the final unpaired card at one-lane width; do not stretch it across both lanes or leave an awkward blank half-row.
- Settings and Surface **must share** `SectionDestinationGrid` so their centered two-column destination cards, widths, gaps, motion and centered third-card behavior match. Keep the parent grid a full-width structural item rather than pairing it with unrelated cards.
- In portrait or narrow windows, retain an accessible, readable one-column design. Avoid clipped text, unreachable controls, excessive blank spaces or UI-thread pagination allocations. Dialog layouts must respond to narrow widths and large text.
- The ordinary Settings destination card body is informational: only its trailing chevron activates navigation. Database submission and other descriptive action cards similarly dispatch commands only via their right-side **minimum 48 dp** control, not the entire tile. Explicit filter/list selections and fields retain their normal interaction.
- Enabled action tiles use the restrained dark filled style of Open VulkanScope Database, not a full bright pink panel. True disabled/unavailable/locked actions are near-black and noninteractive. Status must remain distinguishable by text and accessible contrast as well as color.
- The Trademarks and artwork About heading has **no leading decorative TM or Khronos icon**. Preserve written trademark notices, official copyright ownership and independent-project disclosure. The four bar-visibility icon meanings must be distinct.

## Analysis, Encyclopedia and reporting

- Score is a documented **non-normative local evidence index**, not Vulkan CTS certification, hardware speed or a driver-quality ranking. Its weights are Vulkan API version 30%, unique positive device extensions 25%, reported feature bits 20%, collector integrity 20% and Surface presentation 5%. Unknown/inapplicable inputs do not count as failures and insufficient evidence yields no numeric score.
- The Encyclopedia **On this device** filter uses positive current-device extension, feature and format evidence. Filter before result-cap and pagination; never infer support from a registry description substring.
- Put complete-report TXT, HTML and JSON exports together in Reports & Database. Keep Analysis snapshot JSON and custom-minimum-profile JSON distinct. Minimums' saved profiles and custom-profile import/export actions belong at the top of that section.
- Report formats preserve raw numeric flags, canonical Vulkan tokens, explicit status distinctions, driver/extension metadata and applicable safety diagnostics. Local storage export/import and Turnip bundle handling must validate paths, file size, provenance and overwrite decisions.
- Optional self-tests are explicit isolated Vulkan operations, never claimed as full conformance testing or automatically used to overwrite reported device capability evidence.

## GitHub documentation and release quality

- Keep public README and docs **English only**; preserve machine identifiers, Vulkan enum names and legally required third-party license text verbatim.
- The canonical root `README.md` follows the user's detailed public project structure, including its existing remote screenshot URLs and notes. GitHub release history is consolidated in `release.md` and describes **final 4.0.15 features** from the 4.0.0 series without compiling a list of temporary build failures or superseded UI experiments.
- Current build, registry, scoring, report, database and UI docs must not contain outdated release/version claims. Every relative Markdown link must point to a present file. Historical files without a current purpose may be removed after checking dependencies; do not remove required tooling or user artwork.
- Keep the pinned release source tests, ABI verification, registry lock and pager policy checks green. Perform a **real Android release build**, test each supported ABI, verify portrait/landscape pager interactions and check the deployed Database submission contract before asserting a fully validated release. Static checks or local JVM policy tests alone are insufficient.
