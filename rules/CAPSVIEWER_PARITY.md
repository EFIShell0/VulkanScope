# CapsViewer 4.12 parity methodology

This is a source-level audit only. It never infers runtime support from the database or from device model names.

When a local CapsViewer 4.12 checkout is available, run `tools/compare_capsviewer_4_12.py` or `tools/audit_capsviewer_parity.py --mode source` to classify source-referenced physical-device structs and fields as missing or partial.

A field is considered covered only when the canonical Vulkan header defines it, the runtime pNext query can produce it, and the generated/native report path consumes it. Unknown fields must remain raw/unknown rather than being guessed.
