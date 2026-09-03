# VulkanScope 0.80.3 build / regression audit

## Baseline
- Version: 0.80.3 / versionCode 803
- Immutable predecessor: VulkanScope 0.80.2
- Predecessor SHA-256: `be11ceea517c1a36f3720ebed866624a754b8b2f8d2584bfe6db59ee477b04e5`
- Predecessor census: 280 files
- Vulkan registry/header baseline remains 1.4.361 / 361

## Demonstrated defects and requested architecture
0.80.2 embeds the complete Encyclopedia and Analysis workspace directly in Overview. The user requested compact Overview entry cards whose content opens in separate pages, matching the existing More-style destination behavior.

The 0.80.2 generated symbol rows encode separators as literal `\\t` text, but runtime decoding searches for tab characters. A matching query can therefore call substring with delimiter index -1 and throw StringIndexOutOfBoundsException.

## Repair
- Separate `Page.Encyclopedia` and `Page.Analysis` destinations, both parented visually/back-navigation-wise to Overview.
- Overview does not construct Analysis state or execute Encyclopedia search.
- Encyclopedia result rows are top-level lazy items under the common boundary-aware page wrapper.
- Generator emits one Kotlin `\t` escape per field boundary; malformed rows are skipped/fail-closed before substring decoding.
- Runtime Vulkan evidence/query/report/Database semantics are unchanged.

## Verification
Failing-before-fix against immutable 0.80.2: FAIL as required. Successor source verifier, search state machine and negative mutation suite: PASS. Existing detailed Encyclopedia verifier is successor-compatible and retains the Vulkan 1.4.361 glossary/symbol-census checks. Final aggregate/extracted-ZIP results are recorded by the packaging run.

## Build evidence class
Android assemble/lint/unit were attempted through `bash gradlew :app:assembleRelease :app:lintRelease :app:testReleaseUnitTest --offline --no-daemon`, but Gradle 9.7.1 is not cached and `services.gradle.org` cannot be resolved (`UnknownHostException`). The Android tasks are therefore NOT EXECUTED, not PASS. Real-device search/UI execution is also NOT EXECUTED.
