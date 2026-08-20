## VulkanScope 0.33.4

Info/settings parity, official Vulkan red theming, HTML report visual parity, and Android TV D-pad browse improvements.

# VulkanScope 0.33.4

Full application engineering audit release.

## Fixes
- Deterministic cleanup of the service-side Surface parcel used by isolated Vulkan probes.
- Lower CPU/RAM churn while waiting for large base-report checkpoints: unchanged checkpoint files are no longer repeatedly read and parsed.
- Correct Android display semantics: on API 26+ wide-gamut `false` is Unsupported, while API 24-25 where the API is unavailable remains Unavailable; empty reported HDR type sets remain Unavailable to match Database 0.35.3 semantics.
- UI, TXT, HTML and schema-v3 Database payload now preserve the same display/HDR state model.
- Added `hdrCapabilityStatus` and `preferredWideGamutColorSpace` additively while keeping fields consumed by VulkanScope Database 0.35.3.
- Android HDR luminance uses the explicit invalid sentinel rather than dropping a valid finite 0.0 value.
- HTML Surface diagnostic booleans stay literal scalar values instead of being mislabeled as support states.
- Present-mode enumeration uses the dedicated safety bound.
- Documentation now distinguishes the currently published Khronos Vulkan 1.4.358 specification from VulkanScope's independently pinned/validated 1.4.360 producer/query staging baseline.

Turnip/SAF internals are unchanged.
