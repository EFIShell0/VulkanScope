# Architecture and source responsibilities

VulkanScope retains a single Android Activity for lifecycle and permissions, but splits the Compose UI, Vulkan collector, analysis logic, report export and registry data by responsibility.

| Source | Responsibility |
| --- | --- |
| `MainActivity.kt` | Activity lifecycle, system integration, permissions and collection coordination |
| `VulkanScopeRoot.kt` | Compose application state and navigation, app-owned chrome |
| `VulkanScopeStartupAnimation.kt` | Opening animation |
| `ChromeAutoHide.kt`, `ChromeVisibilityPreferences.kt` | 3-second idle timeout, persisted bar preferences and Settings UI |
| `UiChrome.kt`, `PinnedPagerPolicy.kt`, `PinnedPagerGlassSurface.kt` | Lazy-page pager policy, sticky rendering and independent frost transition |
| `AdaptiveLazyLayout.kt`, `AdaptiveDialogLayout.kt`, `SectionDestinationGrid.kt` | Responsive two-column rows/dialogs and centered Settings/Surface destinations |
| `UiComponents.kt`, `DesignSurfaces.kt`, `EvidenceCards.kt` | Shared cards, buttons, icon/status presentation and design tokens |
| `UiOverview.kt`, `UiDeviceScreens.kt`, `UiFormatsAndProperties.kt`, `UiExtensions.kt` | Main device, format, extension and Surface screens |
| `UiEncyclopedia.kt`, `EncyclopediaDeviceEvidence.kt` | Offline reference search, current-device evidence filter |
| `AnalysisEntry.kt`, `AnalysisDomain.kt`, `AnalysisScreens.kt`, `AdvancedAnalysis.kt` | Analysis state, comparisons, minimums, search and self-test presentation |
| `CapabilityEvidenceScore.kt` | Independent five-component score model (not a benchmark) |
| `ReportExportPipeline.kt`, `UiReports.kt` | Complete report serialization, local exports, Database client and status UI |
| `UiSettings.kt`, `UiProfiles.kt` | Settings and Vulkan profile presentation |
| `VulkanProbeService.kt`, `app/src/main/cpp/vulkanscope.cpp` | Isolated runtime/native Vulkan interrogation and safety boundaries |
| `registry/`, `VulkanSymbolIndex.kt`, `VulkanExtensionReference.kt` | Pinned registry and generated symbol/capability catalogs |

## Design boundaries

- Runtime Vulkan evidence is obtained from actual queries; registry metadata alone is never a support result.
- UI files must not absorb HTTP submission, export serialization or score calculation helpers.
- The pager descriptor is captured during lazy-list DSL creation so item disposal cannot remove its pinned identity. Its index reflects the same transformed row layout used by the real list.
- In landscape, Settings and Surface use `SectionDestinationGrid`, while regular list cards are arranged as ordered left/right pairs.
- Generated source files can be large; do not delete entries based solely on their apparent lack of handwritten Kotlin call sites.
- Database Worker/Pages source is maintained separately. This 4.0.15 ZIP contains Android app sources, not a live database deployment.
