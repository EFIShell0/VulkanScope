# Technical report exports — VulkanScope 4.0.15

## Complete report

Under **Settings → Reports & Database → Export complete report**, TXT, self-contained HTML and schema-v3 structured JSON are offered together. These exports are distinct from Analysis snapshot comparison and custom minimum-profile JSON files. Collection must be complete before the corresponding complete-report action is available.

- Export is always initiated by the user through the in-app shared-storage browser.
- The app generates a temporary snapshot in its private storage (up to 64 MiB), validates the output path and copies the chosen artifact safely.
- The full report preserves raw numeric values, canonical Vulkan® tokens and Unknown/Unavailable/Not applicable distinctions. No capability omission filter is offered.
- Analysis still allows structured inspection of `technicalReport`; the duplicate complete-report JSON export action was removed from Analysis.

## Database submission

**Submit complete report** remains in Reports & Database. The action is dispatched **only through the trailing button with its accessible 48 dp hit target**. The descriptive card body is not clickable. Submission is gated on local report completeness and validated network connectivity. The status panel communicates Ready, Unavailable, In progress, Success and Failure without conflating them.

Only app 4.0.15 / code 4015 is accepted by the companion Database 4.0.6 Worker for **new** reports; historical report reads remain available. The complete request cannot exceed 2 MiB and must not be truncated to fit. See [Database integration](../DATABASE_SETUP.md).

## Analysis and Minimums files

`Import analysis snapshot` / `Export analysis snapshot` use the separate bounded Analysis comparison format. `Import custom profile` / `Export custom profile` belong at the top of Minimums alongside the saved minimum-profile list. These formats do not replace a complete technical report.
