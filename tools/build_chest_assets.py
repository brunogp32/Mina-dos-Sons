#!/usr/bin/env python3
"""Build selected chest animation frames from Pixel Chest Pack."""

from __future__ import annotations

import json
import re
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "design_sources" / "Pixel Chest Pack"
RES = ROOT / "app" / "src" / "main" / "res" / "drawable-nodpi"
REPORT_DIR = ROOT / "build" / "reports"

CHESTS = {
    "wood": SOURCE / "Wooden Chest 1" / "Wooden Chest 1 Sprites",
    "iron": SOURCE / "Metal Chest" / "Metal Chest Sprites",
    "gold": SOURCE / "Golden Chest 1" / "Golden Chest 1 Sprites",
    "crystal": SOURCE / "Retro Chest" / "Retro Chest Sprites",
}


def frame_number(path: Path) -> int:
    match = re.search(r"(\d+)\.png$", path.name)
    return int(match.group(1)) if match else 0


def clean_old() -> None:
    for old in RES.glob("chest_*.webp"):
        old.unlink()


def save_frame(source: Path, dest: Path) -> int:
    from PIL import Image

    image = Image.open(source).convert("RGBA")
    bbox = image.getbbox()
    if bbox:
        image = image.crop(bbox)
    canvas = Image.new("RGBA", (192, 192), (0, 0, 0, 0))
    image.thumbnail((160, 160), Image.Resampling.NEAREST)
    canvas.alpha_composite(image, ((192 - image.width) // 2, (192 - image.height) // 2))
    canvas.save(dest, "WEBP", lossless=True, method=6)
    return dest.stat().st_size


def main() -> int:
    REPORT_DIR.mkdir(parents=True, exist_ok=True)
    RES.mkdir(parents=True, exist_ok=True)
    clean_old()
    records: list[dict[str, object]] = []
    for chest, folder in CHESTS.items():
        frames = sorted(folder.glob("*.png"), key=frame_number)
        if not frames:
            continue
        selected = [frames[0], frames[min(1, len(frames) - 1)], frames[len(frames) // 2], frames[-1]]
        unique = []
        for item in selected:
            if item not in unique:
                unique.append(item)
        for index, source in enumerate(unique):
            name = f"chest_{chest}_{index:02d}"
            dest = RES / f"{name}.webp"
            records.append(
                {
                    "chest": chest,
                    "frame": index,
                    "resource": name,
                    "source": str(source.relative_to(ROOT)),
                    "bytes": save_frame(source, dest),
                }
            )
    (REPORT_DIR / "chest-asset-manifest.json").write_text(json.dumps(records, indent=2, ensure_ascii=False), encoding="utf-8")
    (REPORT_DIR / "asset-size-report.md").write_text(
        "# Asset Size Report\n\n"
        f"- Chest frames: {len(records)}\n"
        f"- Chest bytes: {sum(int(item['bytes']) for item in records)}\n",
        encoding="utf-8",
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
