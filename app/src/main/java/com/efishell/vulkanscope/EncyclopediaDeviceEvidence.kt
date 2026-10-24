package com.efishell.vulkanscope

internal fun collectedDeviceEncyclopediaEntries(
    report: VulkanReport?,
    device: DeviceReport?
): List<EncyclopediaReferenceEntry> {
    if (report == null || device == null) return emptyList()
    val entries = linkedMapOf<String, EncyclopediaReferenceEntry>()

    for (extension in report.instanceExtensions + device.extensions) {
        if (!extension.supported) continue
        val metadata = VULKAN_EXTENSION_REFERENCE[extension.name]
        val entry = metadata?.let(::extensionReferenceDefinition)
            ?: EncyclopediaReferenceEntry(
                title = extension.name,
                category = "Extension reference",
                definition = "Enumerated by the active Vulkan runtime.",
                detail = "${extension.scope} extension; reported revision ${extension.specVersion}."
            )
        entries["extension/${extension.name}"] = entry
    }
    for (feature in device.features) {
        if (!feature.supported) continue
        entries["feature/${feature.name}"] = EncyclopediaReferenceEntry(
            title = feature.name,
            category = "Device feature",
            definition = "Feature bit reported true by the selected device.",
            detail = "Collected feature evidence. Availability and enabling are distinct operations."
        )
    }
    for (format in device.formats) {
        if (!format.supported) continue
        entries["format/${format.name}"] = EncyclopediaReferenceEntry(
            title = format.name,
            category = "Device format",
            definition = "This format has reported usable format-feature evidence on the device.",
            detail = "Linear/optimal/buffer support is usage-specific; open Formats for detailed feature masks."
        )
    }
    return entries.values.toList()
}
