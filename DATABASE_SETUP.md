# VulkanScope Database integration — 4.0.15

The app sends explicit complete-report submissions to its fixed, HTTPS-only endpoint:

`https://vulkanscope-database-api.vulkanscope.workers.dev`

The origin cannot be edited in the app. No report is uploaded in the background. Network-backed submission is available only after a complete local collection and a validated Android default-network connection.

## New-submission version contract

**The companion VulkanScope Database 4.0.6 Worker accepts new reports only from VulkanScope 4.0.15 (`versionCode 4015`).** It validates the reported producer version and schema; reports from earlier and later app builds are rejected until the server-side allow-list is deliberately updated. Historical submitted reports remain readable. The live Worker must be deployed separately from any GitHub Pages update: source changes alone do not change server acceptance.

- Request: `POST /v1/reports`
- Envelope: `schemaVersion = 2`, app `version = "4.0.15"`, `versionCode = 4015`
- Nested report: `technicalReport.schemaVersion = 3`
- Transport ceiling: **2 MiB**; oversized data fails without omission/truncation
- Success: validates a 64-character lowercase hexadecimal report ID
- Failures: bounded, copyable diagnostics; an HTTP rejection is never treated as success

## Public report lookup

Analysis may perform an explicit, bounded `GET /v1/reports/<lowercase-64-hex-id>?compact=1` from the same official origin. Comparison requires a returned `technicalReport`, and all comparison work remains local. Report permalinks and QR images are created locally; no external QR service is contacted.

## Data and privacy

The technical report preserves actual Vulkan evidence with explicit states (Supported, Unsupported, Available, Unavailable, Incomplete, Not applicable, Unknown). The sender does not invent capabilities, drop fields selectively to fit transport, or include personal identifiers/private paths in the submission. Database backend source is maintained in the separate [VulkanScope Database repository](https://github.com/EFIShell0/VulkanScope_database/).
