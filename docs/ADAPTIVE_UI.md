# Adaptive layouts

VulkanScope uses orientation- and width-aware layout components rather than copying large lists into eager paired collections.

## Portrait and compact windows

Portrait and constrained widths retain single-column lazy list results. Controls and dialogs stack as needed for enlarged text and screen readers.

## Wide landscape

- Ordinary result cards — including standalone `item {}` cards and repeated `items(...)` content — appear as ordered **left/right pairs** where two readable lanes fit.
- Structural cards, filter headers and collection pagers retain their full-width identities and stable lazy keys.
- The final unpaired result card is centered at one column width rather than stretched to the full row.
- Settings and Surface share the reusable `SectionDestinationGrid`: two equally sized destination cards followed, where applicable, by a centered third card. The group is not paired with unrelated items.
- Bounded detail dialogs use columns when the viewport permits and stack when readability requires it.

## Pager and animation

Adaptive row projection must use the same key/index mapping used by the pager pinning policy. Lazy item disposal is not allowed to destroy the pinned descriptor. The inline/pinned handoff is 280 ms, while frosted-blur transitions react to app-header visibility over 320 ms.

## Verification checklist

Rotate while scrolled or pinned; verify both directions of pager handoff, filter focus, dialogs, accessible controls, Settings/Surface third-card alignment, portrait list order and landscape result pairing. Repeat with fewer than one page of results (no pager expected). See [QA](QA.md).
