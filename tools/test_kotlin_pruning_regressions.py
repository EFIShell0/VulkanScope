from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
src = root / "app/src/main/java/com/efishell/vulkanscope"
core = (src / "CoreModels.kt").read_text(encoding="utf-8")
ui = (src / "UiComponents.kt").read_text(encoding="utf-8")
settings = (src / "UiSettings.kt").read_text(encoding="utf-8")
chrome = (src / "ChromeAutoHide.kt").read_text(encoding="utf-8")
root_ui = (src / "VulkanScopeRoot.kt").read_text(encoding="utf-8")
assert "internal fun readTurnipSourceInfo(sourceFile: File): TurnipSourceInfo?" in core
assert "val source = readTurnipSourceInfo(sourceFile)" in core
assert "ExpressiveAssistChip" not in ui
assert "ExpandableFileManagerSearch" not in settings
for text in (ui, settings):
    assert not re.search(r"(?m)^}\s*\n\s+(?:label|value): String,", text), "orphan Kotlin function signature"
for f in src.glob("*.kt"):
    imports = [line.strip() for line in f.read_text(encoding="utf-8").splitlines() if line.strip().startswith("import ")]
    assert len(imports) == len(set(imports)), f"duplicate imports in {f.name}"
assert "CHROME_INACTIVITY_MILLIS = 3000L" in chrome
assert "kotlinx.coroutines.delay(CHROME_INACTIVITY_MILLIS)" in root_ui
assert "After 3 seconds" in (src / "ChromeVisibilityPreferences.kt").read_text(encoding="utf-8")
assert "versionCode = 4015" in (root / "app/build.gradle.kts").read_text(encoding="utf-8")
assert "Version name 4.0.15 / code 4015" in (root / "rules/PROJECT_RULES.md").read_text(encoding="utf-8")
print("PASS Kotlin removed-declaration regressions, import uniqueness, 3-second chrome contract and release code (STATIC ONLY)")

assert all(f"fun {method}(value: Boolean)" in chrome for method in ("updateEnabled", "updateHideHeader", "updateHideNavigation"))
