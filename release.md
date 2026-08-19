# VulkanScope 0.33.1

## Complete-collection driver gate

System Vulkan driver selection, Turnip / third-party driver selection, and Turnip ZIP import now remain disabled while the complete Vulkan collection pass is active. They unlock together with TXT, HTML, and Database complete-report actions once collection finishes.

This is an interactivity gate only: the restored 0.32.4 Turnip/SAF implementation itself remains unchanged.

## Turnip / SAF full restoration

This release restores the complete Turnip/SAF path to the known-working VulkanScope 0.32.4 implementation. The restoration covers the Storage Access Framework picker, ZIP import flow, driver-mode switching, installed Turnip library discovery, and the Settings driver/import controls.

The native Turnip loader integration was already identical to 0.32.4 and remains unchanged.

Later independent improvements are preserved: Vulkan 1.4.360 query coverage, HDR logo/capability presentation, update confirmation, exact structured report data, TXT/HTML complete-report gating, and VulkanScope Database compatibility.
