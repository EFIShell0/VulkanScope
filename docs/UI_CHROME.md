# App chrome and sticky collection pager

## Auto-hide preferences

Under **Settings → Drivers & Preferences**, immediately below **Opening animation**, app bar visibility has a master **Automatic bar hiding** switch and separate **Top app header** / **Bottom navigation** switches. All three default to enabled and persist locally. The idle timeout is **3 seconds**; touch, scroll, keyboard or navigation interaction reveals the selected app-owned bars. The three settings and the bar-visibility heading use distinct, meaningful icons.

Android system status/navigation indicators, network/update overlays and modal dialogs are not hidden by this app-owned setting. Motion must preserve usable content insets and page-arrow touch targets in both orientations.

## Inline/pinned pager handoff

- A collection pager is a real, interactive inline list item before its pin threshold; it never consists solely of an empty permanent `Spacer`.
- The same logical pager is rendered by an inline presentation while unpinned and by a floating presentation while pinned. Both presentations coordinate a **280 ms** eased handoff with complementary opacity and a small offset, including reverse scrolling.
- The pinned eligibility policy is based on stable LazyList scroll position and the projected row key, not on the current visibility/height of the app header or whether a lazy item happens to be composed.
- After the original item leaves the viewport, the pinned control remains available and its page arrows/input are usable.
- The original **full-width frosted blur strip and rounded pager glass** are preserved while the header is visible. Their visibility animates independently over **320 ms** when the header hides or returns; blur is never the pager's visibility owner.
- No black blank source gap, jump-cut at attach/detach, lingering blurred corner, or floating overlay blocking the inline pager should occur.

These are **source contracts**, not a claim of completed visual Android-device testing. See [QA](QA.md) for the device verification matrix.
