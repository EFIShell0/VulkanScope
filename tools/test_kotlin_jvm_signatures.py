from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
SOURCES = ROOT / "app/src/main/java/com/efishell/vulkanscope"
MODEL = (SOURCES / "ChromeAutoHide.kt").read_text(encoding="utf-8")
UI = (SOURCES / "ChromeVisibilityPreferences.kt").read_text(encoding="utf-8")

def generated_setter_clashes(body):
    properties = re.findall(r"(?m)^\s*(?:var|internal\s+var|private\s+var)\s+(\w+)\s+by\s+", body)
    declarations = set(re.findall(r"\bfun\s+(\w+)\s*\(", body))
    return sorted(prop for prop in properties if "set" + prop[0].upper() + prop[1:] in declarations)

model_body = MODEL.split("internal class ChromeAutoHideSettings(", 1)[1].split("internal val LocalChromeAutoHideSettings", 1)[0]
assert generated_setter_clashes(model_body) == [], "JVM property setter clashes with named update method"
for prop, old, new in (
    ("enabled", "setEnabled", "updateEnabled"),
    ("hideHeader", "setHideHeader", "updateHideHeader"),
    ("hideNavigation", "setHideNavigation", "updateHideNavigation"),
):
    assert re.search(r"(?m)^\s*var\s+" + prop + r"\s+by\s+mutableStateOf\(", model_body), prop
    assert re.search(r"(?s)var\s+" + prop + r"\s+by\s+mutableStateOf\([^\n]*\)\s*private\s+set", model_body), prop + " must retain private setter"
    assert "fun " + new + "(value: Boolean)" in model_body
    assert "preferences::" + new in UI
    assert not re.search(r"\b" + old + r"\s*\(", model_body)
    assert not re.search(r"preferences::" + old + r"\b", UI)
    tampered = model_body.replace("fun " + new + "(", "fun " + old + "(")
    assert prop in generated_setter_clashes(tampered), "guard did not detect reintroduced " + old
assert 'CHROME_INACTIVITY_MILLIS = 3000L' in MODEL
print("PASS auto-hide setter JVM collision guard, all UI bindings, and negative mutation regressions (static)")
