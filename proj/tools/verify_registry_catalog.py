import json
import re
import sys
from pathlib import Path

def main():
    if len(sys.argv) != 4:
        raise SystemExit("usage: verify_registry_catalog.py <manifest.json> <header.h> <catalog.h>")
    manifest=json.loads(Path(sys.argv[1]).read_text(encoding="utf-8"))
    header=Path(sys.argv[2]).read_text(encoding="utf-8", errors="ignore")
    catalog=Path(sys.argv[3]).read_text(encoding="utf-8", errors="ignore")
    structs=set(manifest.get("implementedPhysicalDeviceStructs", []))
    missing=[s for s in structs if not re.search(r"\btypedef struct "+re.escape(s)+r"\b", header)]
    if missing:
        raise SystemExit("Missing native struct definitions: " + ", ".join(sorted(missing)))
    m=re.search(r"kImplementedPhysicalDeviceStructCount = (\d+)", catalog)
    if not m or int(m.group(1)) != len(structs):
        raise SystemExit("Catalog struct count mismatch")
    print(f"registry catalog verified: {len(structs)} native structs")

if __name__ == "__main__":
    main()
