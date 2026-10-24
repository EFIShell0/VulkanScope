from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
source = (ROOT / 'app/src/main/java/com/efishell/vulkanscope/UiComponents.kt').read_text(encoding='utf-8')

def rgb(hex_value):
    return [int(hex_value[i:i + 2], 16) / 255.0 for i in (0, 2, 4)]

def luminance(hex_value):
    values = rgb(hex_value)
    linear = [v / 12.92 if v <= 0.04045 else ((v + 0.055) / 1.055) ** 2.4 for v in values]
    return sum(c * w for c, w in zip(linear, (0.2126, 0.7152, 0.0722)))

def contrast(text, background):
    a, b = sorted((luminance(text), luminance(background)), reverse=True)
    return (a + 0.05) / (b + 0.05)

for foreground, background in (
    ('F7F2F3', '20171B'),
    ('B6ACAE', '20171B'),
    ('9C9697', '101010'),
    ('898485', '101010'),
    ('989092', '191919'),
):
    assert contrast(foreground, background) >= 4.5, (foreground, background, contrast(foreground, background))
assert 'val container = if (!enabled) ComposeColor(0xFF101010)' in source
assert 'else VulkanSurfaceRaised' in source
assert 'val titleColor = if (enabled) VulkanTextPrimary else ComposeColor(0xFF9C9697)' in source
assert 'val detailColor = if (enabled) VulkanTextSecondary else ComposeColor(0xFF898485)' in source
assert 'disabledContainerColor = ComposeColor(0xFF191919)' in source
assert 'modifier = modifier.then(if (wholeRowClickable' not in source
print('PASS 4.0.14 enabled action tint, disabled-only black, contrast ratios and chevron-only dispatch (static)')
