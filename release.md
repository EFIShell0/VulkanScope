# VulkanScope 0.22.7

## UI change
- Replaced the top-left `VulkanScope` text title with a horizontal logo lockup derived from the existing application icon artwork.
- The original Vulkan wordmark and SCOPE lettering are preserved without redrawing or changing their geometry.
- The original horizontal divider is rotated vertically and used between `Vulkan` and `Scope` as requested.
- The logo is constrained to the existing top app-bar space so the page title and actions remain usable on phones and Android TV.

## Compatibility and preservation
- No Vulkan query/probe coverage was removed or changed.
- No existing feature, report field, SAF behavior or Android TV navigation behavior was removed.
- ABI targets remain `armeabi-v7a`, `arm64-v8a`, and `x86_64`; x86 remains excluded.

## Report exports
- HTML exports now embed the exact horizontal VulkanScope logo artwork used by the application top bar, keeping exported reports self-contained.
- TXT and HTML exports now include application version, version code, package name, the installed application ABI, supported device ABIs, developer name/nickname, and GitHub account information.
- User-facing TXT, HTML and in-app detailed property labels no longer expose the internal CapsViewer 4.12 parity prefix; internal parity metadata remains intact for coverage auditing.
- Added executable `VK_KHR_ray_query` feature-query coverage through the validated isolated feature-query path.
- Styled the HTML GitHub link to match the report visual system instead of the browser-default link presentation.

## Version
- versionName: `0.22.7`
- versionCode: `150`

## VulkanScope 0.22.7

- Fixed the remaining `VK_KHR_ray_query` isolated query-group alias handling.
- Removed user-visible `CapsViewer 4.12 parity ·` labels from generated TXT/HTML report data without removing internal parity coverage.
- Preserved existing query/property coverage and report schema.
