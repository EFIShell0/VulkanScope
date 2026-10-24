# VulkanScope 4.0.15 design system

## Surfaces and cards

VulkanScope uses restrained, dark Material 3 Expressive surfaces with subtle borders, consistent radius/padding, accessible contrast and clearly identified states. Existing image and logo assets are intentionally preserved.

- **Destination cards:** prominent title and explanation; body is informational, and navigation is activated through the trailing chevron (minimum 48 dp target).
- **Evidence cards:** shared `VulkanEvidenceCard` presentation for Extensions, Features, Formats, Surface and Encyclopedia. Explicit support/reference labels and compact technical metadata remain readable in portrait and paired landscape lanes.
- **Action buttons:** enabled actions have the same dark filled treatment as *Open VulkanScope Database*, with red accents reserved for icons/affordances. True unavailable, locked and disabled states use near-black and do not dispatch.
- **Submission status:** icon-free, state-specific informational card, separate from the upload action control.
- **Markdown:** legible white/near-white text in updates, licenses, documentation and legal notices.
- **Branding:** the About **Trademarks and artwork** heading has no decorative TM/Khronos icon. Actual trademark and logo ownership notices remain in text.

## Navigation and pager

Only app-owned upper/lower bars participate in 3-second auto-hide. Pager source/pinned presentations exchange smoothly in 280 ms; frosted full-width and in-card blur layers fade independently over 320 ms. The pager itself remains interactive when the bars are hidden.

## Responsive behavior

Settings and Surface use the same centered destination grid; regular information results are paired in wide landscape without compressing cards or changing source order. A single final card is centered. Android system and transient status overlays remain independently visible.
