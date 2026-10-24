# 4.0.15 source hygiene

Historical `RELEASE_4_0_1.md` and `RELEASE_4_0_2.md` were retired from the active source package; corresponding release history remains summarized in `changelog.md`. The oversized legacy `BUILD_AUDIT.md` was replaced by focused current-version checks in `docs/QA.md` and `docs/CODE_AUDIT.md`.

Unused imports from the original mechanically segmented Kotlin sources were pruned. Unreferenced top-level helpers were removed only after a name-reference scan and inspection. JNI entry points and generated Vulkan symbol/reference indexes were intentionally preserved. `VulkanScopeRoot.kt`, `VulkanScopeStartupAnimation.kt`, `ChromeAutoHide.kt`, `ChromeVisibilityPreferences.kt`, `DesignSurfaces.kt` explicitly describe their responsibilities.
