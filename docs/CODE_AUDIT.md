# Source organization review — VulkanScope 4.0.15

- The Android UI root, opening animation and persisted app bar visibility state are separate modules.
- Runtime query diagnostics and evidence are not inferred from the registry catalog.
- `ReportExportPipeline.kt` owns complete-report export/Database transport; it is not embedded in `UiExtensions.kt`.
- `CapabilityEvidenceScore.kt` owns the five-component local evidence score; `AnalysisScreens.kt` only presents it.
- `EncyclopediaDeviceEvidence.kt` matches positive device evidence without relying on a registry-description substring.
- `EvidenceCards.kt` holds shared compact result-card design used across Extensions, Formats, Surface, Features and Encyclopedia.
- Adaptive item projection, pager policy, glass rendering and Settings/Surface destination grids remain separated.
- JNI interfaces and generated locked Vulkan catalogs are retained; apparent unused symbols cannot safely be removed by name-only searches.

Run `python -B tools/quality_gate.py`, then the actual Android release build and on-device UI checks described in [QA](QA.md). A source audit alone does not validate the complete application.
