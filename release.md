# VulkanScope 0.33.10

VulkanScope 0.33.10 corrects the Properties & Limits summary semantics introduced in 0.33.9.

## Changes

- Stops combining property-query rows and limit rows into one `query results` value in the All filter.
- All now reports property query results, unique property names, and limits as three independent counts.
- Limits now reports only the visible limit count.
- `Unique property names` is calculated only from detailed property rows and never includes limit names.
- Search-filtered counts continue to update from the currently visible rows.
- Vulkan collection, native query coverage, TXT/HTML reports, Database payloads, Display/HDR, update, Turnip/SAF and security behavior are unchanged.

## Version

- Version: `0.33.10`
- versionCode: `340`
- Vulkan query/header staging baseline: `1.4.360`
