# VulkanScope 0.41.6

VulkanScope is an Android Vulkan capability, driver, Surface/WSI, display/HDR and analysis inspector built around runtime evidence and canonical Khronos naming.

## Current baseline

- Published/query baseline: Vulkan 1.4.360.
- Android compile/target SDK: 37; minimum SDK: 24.
- Target native ABIs: arm64-v8a, armeabi-v7a and x86_64.
- Canonical Database payload: schema 2 / technicalReport 3.
- Recommended companion Database: VulkanScope Database 0.39.4 or newer.


## 0.41.6 build fix

0.41.6 fixes the release-blocking Kotlin compile regression in the Analysis dependency-graph depth state. `mutableIntStateOf` remains the primitive Int state implementation and now has its required Compose runtime import. Release verification also checks primitive Compose state-factory imports so the same class of build failure is caught before release. Vulkan collection, Analysis semantics, profiles, reporting, Database schema and the Vulkan 1.4.360 baseline are unchanged from 0.41.5.

## 0.41.5 Analysis audit

0.41.5 focuses on Analysis correctness, profile freshness, comparison completeness, selected-device self-tests and Database permalink compatibility while preserving canonical collection/reporting semantics.

### Analysis changes

- Snapshot evidence now includes query status/reasons, memory heaps/types, queue/video state, layers, Surface completeness/safety, Profiles, Display/HDR detail and registry provenance in addition to features, extensions, formats, limits and properties.
- Canonical 0.41.4 snapshot keys are preserved for baseline compatibility; only true duplicate evidence rows receive deterministic duplicate suffixes instead of silently overwriting earlier rows.
- Compare supports evidence-kind and Added/Removed/Changed/Regression filters. Missing device extensions or Surface formats are regression candidates only when the current enumeration is complete enough to justify that conclusion.
- Watched evidence is bounded and previewed without materializing every matching entry into a temporary list.
- Dependency graph depth is selectable from one to four while preserving the 64-node traversal and 24-node visual bounds.
- Quality scoring is explicitly a heuristic diagnostic-evidence summary and is calculated only when its Analysis section is open.
- Optional Vulkan self-tests target the exact selected physical device by vendor/device id and do not silently test another GPU.
- Database report links use the canonical `#reports/<report-id>/Overview` route introduced by the current Database.

### Profile/minimum changes

- Profile API requirements compare full major.minor.patch versions rather than only major/minor.
- Current Android profile families include Android 17/16/15 requirements and Android Vulkan Profile 2025, while current Roadmap 2026/2024/2022 entries are visible.
- The evaluator distinguishes verified failures from missing evidence. Incomplete device-extension enumeration cannot turn an absent extension into a false FAIL.
- Promoted-to-core extensions may be satisfied by the device core API when checked-in registry promotion metadata proves the promotion.
- Minimum and maximum-direction numeric checks are handled separately.
- Vulkan Profiles can require more evidence classes than the lightweight in-app evaluator maps. Partial evaluation therefore remains `UNKNOWN` instead of claiming full profile `PASS`.

## Evidence semantics

Runtime capability evidence is never inferred from GPU marketing names. Unsupported, unavailable, not applicable and unknown remain distinct. Partial Vulkan enumerations expose returned entries as positive evidence only and do not infer absent entries unsupported.
