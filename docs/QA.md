# VulkanScope 4.0.15 — release verification

## Source and build checks

```powershell
python -B tools\quality_gate.py
.\gradlew.bat --no-daemon :app:assembleRelease
python -B tools\verify_release_apks.py
```

The quality gate checks locked registry/version metadata, source contracts, common UI/pager invariants, action states and English documentation. Pager policy and adaptive index tests may run under a local Kotlin/JVM harness. **Only a full Android build and device test establish Android/Compose runtime behavior.** Do not report a successful APK build solely from static checks.

## On-device acceptance matrix

1. **Inline pager:** In Extensions and Encyclopedia with more than one result page, the pager must be visible in its list position. There must not be a large blank source row.
2. **Detach:** Scroll until the pager reaches the sticky threshold. The inline/floating controls must exchange smoothly over **280 ms**, never abruptly or with dead arrow/input controls.
3. **Pinned lifetime:** Continue scrolling until the original lazy item is disposed; the fixed pager must remain. It must not vanish after three seconds of no interaction.
4. **Independent blur:** App bars hide after **three seconds**. The pager stays usable, while the full-width and clipped pager glass fade out over **320 ms** and restore on touch. No leftover blurred corner may remain.
5. **Attach:** Reverse the scroll past the original pager row. The floating/inline handoff must animate back; the inline pager must regain the real list position without a black gap.
6. **Responsive geometry:** Repeat in portrait, narrow landscape, wide landscape and after rotation. Settings and Surface should share two centered cards plus a centered odd third item. General result cards should form ordered left/right pairs when wide enough; structural sections/pager remain full width.
7. **Actions/status:** Compare enabled Submit, Check for updates and Run Vulkan self-tests with the dark Open Database action style. Only right-side command controls activate action cards; unavailable controls remain noninteractive. The Database ready/offline/submitting/failure cards remain informational.
8. **Evidence:** Test Encyclopedia **On this device** with supported, missing and not-yet-queried extensions; only positive collected evidence qualifies. Verify complete-report TXT/HTML/JSON export grouping, separate Analysis snapshots and Minimums import/export.
9. **Accessibility:** Check focus order, scroll hints, large fonts, dialogs, network notifications, Android TV/DPAD and landscape system insets.
10. **Backend:** When Database 4.0.6 is actually deployed, verify new submissions from 4.0.15 / 4015 are accepted and other app versions rejected. Previously stored reports must still be readable.

## Distribution prerequisites

The distributed source ZIP includes no proof of a fully compiled APK or completed on-device animation checks from this packaging environment. GitHub Actions and physical-device testing are release requirements; do not infer those outcomes from the source archive alone.
