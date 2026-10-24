# Analysis Score — local evidence index

This is an **app-defined descriptive index**, not a GPU benchmark, a Vulkan certification result, a Khronos CTS report, or a reliable cross-device performance ranking. Vulkan extensions expose distinct interfaces; simply counting them cannot establish hardware speed or conformance. See the [Vulkan extensions guide](https://docs.vulkan.org/guide/latest/enabling_extensions.html) and [Vulkan CTS guide](https://docs.vulkan.org/guide/latest/vulkan_cts.html).

## Components and weights

| Component | Weight | Data source and calculation |
| --- | ---: | --- |
| Vulkan API version | 30% | 1.0 = 0.2, 1.1 = 0.4, 1.2 = 0.6, 1.3 = 0.8, 1.4+ = 1.0 of this component |
| Enumerated device extensions | 25% | Unique, positively enumerated device extension names. Fraction = min(1, sqrt(count / 160)) |
| Reported feature bits | 20% | Number of feature bits reported true divided by all explicitly reported bits |
| Collection integrity | 20% | Passed applicable safety/completion checks divided by all applicable checks |
| Surface presentation | 5% | 1 when presentation is supported by an available surface query, otherwise 0; unavailable queries are excluded |

These percentages are a **documented application heuristic**, not weights provided by the Vulkan specification. Saturation at 160 extensions is intentional to reduce runaway reward from sheer token count; it is not a compliance requirement.

## Calculation

Only components with recorded evidence are assessed:

```text
score = round(100 × Σ(component_weight × observed_component_fraction) / Σ(assessed_component_weight))
coverage = Σ(assessed_component_weight)
```

`coverage` must be at least 45 out of 100 for a numerical score. Otherwise the app displays `Unavailable` / `Insufficient device evidence`. No missing or not-applicable input is presumed false, and the evidence coverage is shown separately.

The Score tab displays individual components, the measured evidence and the calculation method; safety checks are listed separately from device capability breadth. The Tests tab continues to run optional operational Vulkan probes; those probes are not repurposed as formal conformance tests.
