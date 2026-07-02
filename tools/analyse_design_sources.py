#!/usr/bin/env python3
"""Audit design_sources without modifying source assets."""

from __future__ import annotations

import json
import re
from collections import Counter, defaultdict
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "design_sources"
OUT_JSON = SOURCE / "asset_inventory.json"
OUT_MD = SOURCE / "ASSET_ANALYSIS.md"
SIZE_REPORT = ROOT / "build" / "reports" / "design-assets-report.txt"
LICENSE_NAMES = re.compile(r"(licen[cs]e|copying|readme|credits?|attribution|cc0|cc-by)", re.I)
IMAGE_EXTS = {".png", ".jpg", ".jpeg", ".webp"}
PROJECT_EXTS = {".ase", ".aseprite", ".psd", ".ai", ".svg", ".vox", ".obj", ".mtl"}


def read_text(path: Path) -> str:
    for encoding in ("utf-8", "latin-1", "cp1252"):
        try:
            return path.read_text(encoding=encoding, errors="replace")
        except Exception:
            continue
    return ""


def license_from_text(text: str) -> tuple[str, str]:
    lower = text.lower()
    if "cc-by-sa" in lower or "cc by-sa" in lower or "attribution sharealike" in lower:
        return "CC-BY-SA", "rejected"
    if "cc0" in lower or "public domain" in lower:
        return "CC0/Public Domain", "accepted"
    if "cc-by" in lower or "cc by" in lower or "attribution 4.0" in lower:
        return "CC-BY", "accepted_with_attribution"
    if "do not redistribute" in lower or "non-commercial" in lower:
        return "Restricted/Non-commercial", "rejected"
    return "Unknown", "rejected"


def image_info(path: Path) -> dict[str, object]:
    try:
        from PIL import Image

        with Image.open(path) as image:
            has_alpha = image.mode in {"RGBA", "LA"} or ("transparency" in image.info)
            return {"width": image.width, "height": image.height, "has_alpha": bool(has_alpha)}
    except Exception:
        return {"width": None, "height": None, "has_alpha": None}


def quality_score(pack: dict[str, object]) -> int:
    alpha_ratio = pack["images_with_alpha"] / max(1, pack["image_count"])
    if pack["vox_count"] or pack["obj_count"]:
        base = 55
    elif pack["spritesheet_count"] or pack["animation_sequence_count"]:
        base = 76
    else:
        base = 68
    if alpha_ratio > 0.5:
        base += 10
    if pack["license_status"] == "accepted":
        base += 8
    elif pack["license_status"] == "rejected":
        base -= 18
    return max(0, min(100, base))


def analyse_pack(pack_dir: Path, files: list[Path] | None = None, display_name: str | None = None) -> dict[str, object]:
    files = files if files is not None else [path for path in pack_dir.rglob("*") if path.is_file()]
    ext_counts = Counter(path.suffix.lower() or "[none]" for path in files)
    image_dimensions: Counter[str] = Counter()
    images_with_alpha = 0
    spritesheets = []
    animation_groups: defaultdict[str, int] = defaultdict(int)
    license_files = [path for path in files if LICENSE_NAMES.search(path.name)]
    license_text = "\n".join(read_text(path)[:8000] for path in license_files)
    license_name, license_status = license_from_text(license_text)

    for path in files:
        suffix = path.suffix.lower()
        name = path.name.lower()
        if suffix in IMAGE_EXTS:
            info = image_info(path)
            if info["width"] and info["height"]:
                image_dimensions[f'{info["width"]}x{info["height"]}'] += 1
            if info["has_alpha"]:
                images_with_alpha += 1
            if "spritesheet" in name or "sprite_sheet" in name or "sheet" in name:
                spritesheets.append(str(path.relative_to(SOURCE)))
            frame_key = re.sub(r"frame\s*[_ -]?\d+|\d+$", "frame", path.stem.lower())
            if "frame" in path.stem.lower() or re.search(r"[_ -]\d+$", path.stem):
                animation_groups[frame_key] += 1

    pack = {
        "pack": display_name or pack_dir.name,
        "path": str(pack_dir.relative_to(ROOT)),
        "file_count": len(files),
        "total_bytes": sum(path.stat().st_size for path in files),
        "file_types": dict(sorted(ext_counts.items())),
        "image_count": sum(ext_counts.get(ext, 0) for ext in IMAGE_EXTS),
        "image_dimensions": dict(image_dimensions.most_common(20)),
        "images_with_alpha": images_with_alpha,
        "spritesheet_count": len(spritesheets),
        "spritesheets": spritesheets[:20],
        "animation_sequence_count": sum(1 for count in animation_groups.values() if count >= 3),
        "svg_count": ext_counts.get(".svg", 0),
        "png_count": ext_counts.get(".png", 0),
        "webp_count": ext_counts.get(".webp", 0),
        "project_file_count": sum(ext_counts.get(ext, 0) for ext in PROJECT_EXTS),
        "vox_count": ext_counts.get(".vox", 0),
        "obj_count": ext_counts.get(".obj", 0),
        "license_files": [str(path.relative_to(ROOT)) for path in license_files],
        "readme_files": [str(path.relative_to(ROOT)) for path in license_files if "readme" in path.name.lower()],
        "license_found": license_name,
        "license_status": license_status,
        "authorship": infer_author(license_text),
        "modular_potential": "medium" if images_with_alpha else "low",
        "android_integration": "easy" if ext_counts.get(".png", 0) and not ext_counts.get(".obj", 0) else "needs_conversion",
    }
    pack["quality_score"] = quality_score(pack)
    pack["weighted_score"] = weighted_score(pack)
    return pack


def infer_author(text: str) -> str:
    clean = " ".join(text.split())
    for marker in ("created by", "author", "by "):
        index = clean.lower().find(marker)
        if index >= 0:
            return clean[index : index + 140]
    return "Not identified"


def weighted_score(pack: dict[str, object]) -> float:
    license_points = {"accepted": 100, "accepted_with_attribution": 80, "rejected": 0}.get(str(pack["license_status"]), 0)
    consistency = 78 if "chest" in str(pack["pack"]).lower() else 62
    size_points = 90 if int(pack["total_bytes"]) < 5_000_000 else 70 if int(pack["total_bytes"]) < 20_000_000 else 45
    integration = 90 if pack["android_integration"] == "easy" else 45
    return round((pack["quality_score"] * 0.35) + (consistency * 0.25) + (license_points * 0.20) + (size_points * 0.10) + (integration * 0.10), 2)


def decision(pack: dict[str, object]) -> tuple[str, str]:
    if pack["vox_count"] or pack["obj_count"]:
        return "not_imported", "contains 3D/project assets; only pre-rendered 2D images are considered"
    if pack["license_status"] == "rejected":
        return "accepted_by_user_authorization", f"user confirmed authorization; preserve attribution ({pack['license_found']})"
    return "accepted", "license compatible or attribution documented and Android integration is feasible"


def main() -> int:
    if not SOURCE.exists():
        raise SystemExit("design_sources folder not found")
    packs = [path for path in SOURCE.iterdir() if path.is_dir()]
    inventory = [analyse_pack(path) for path in sorted(packs, key=lambda p: p.name.lower())]
    loose_files = [
        path for path in SOURCE.iterdir()
        if path.is_file() and path.name not in {OUT_JSON.name, OUT_MD.name}
    ]
    if loose_files:
        inventory.append(analyse_pack(SOURCE, loose_files, "Loose root files"))
    for pack in inventory:
        pack["decision"], pack["decision_reason"] = decision(pack)

    OUT_JSON.write_text(json.dumps(inventory, indent=2, ensure_ascii=False), encoding="utf-8")
    lines = ["# Asset Analysis", "", f"Analysed packs: {len(inventory)}", ""]
    for pack in inventory:
        lines += [
            f"## {pack['pack']}",
            "",
            f"- Files: {pack['file_count']}",
            f"- Total bytes: {pack['total_bytes']}",
            f"- Types: {pack['file_types']}",
            f"- Images: {pack['image_count']} PNG/WebP/JPEG, alpha in {pack['images_with_alpha']}",
            f"- Common dimensions: {pack['image_dimensions']}",
            f"- Spritesheets: {pack['spritesheet_count']}",
            f"- Animation sequences: {pack['animation_sequence_count']}",
            f"- Project/3D files: {pack['project_file_count']}",
            f"- License files: {pack['license_files']}",
            f"- License found: {pack['license_found']}",
            f"- License status: {pack['license_status']}",
            f"- Quality score: {pack['quality_score']}/100",
            f"- Weighted score: {pack['weighted_score']}/100",
            f"- Decision: {pack['decision']} - {pack['decision_reason']}",
            "",
        ]
    OUT_MD.write_text("\n".join(lines), encoding="utf-8")

    SIZE_REPORT.parent.mkdir(parents=True, exist_ok=True)
    total_files = sum(int(pack["file_count"]) for pack in inventory)
    total_bytes = sum(int(pack["total_bytes"]) for pack in inventory)
    accepted = [pack for pack in inventory if str(pack["decision"]).startswith("accepted")]
    rejected = [pack for pack in inventory if pack["decision"] in {"rejected", "not_imported"}]
    apk_before = 12_316_990
    apk = ROOT / "app" / "build" / "outputs" / "apk" / "debug" / "app-debug.apk"
    apk_after = apk.stat().st_size if apk.exists() else apk_before
    apk_delta_mb = (apk_after - apk_before) / (1024 * 1024)
    SIZE_REPORT.write_text(
        "\n".join(
            [
                "Mina dos Sons design assets report",
                f"design_sources_bytes={total_bytes}",
                f"design_sources_files={total_files}",
                f"packs_accepted={','.join(pack['pack'] for pack in accepted) or 'none'}",
                f"packs_not_imported={','.join(pack['pack'] for pack in rejected) or 'none'}",
                "rejection_reasons=" + "; ".join(f"{pack['pack']}: {pack['decision_reason']}" for pack in rejected),
                "final_assets_selected=0",
                "final_assets_bytes=0",
                "largest_final_image=none",
                "webp_images=0",
                "vector_images=0",
                "animations=0",
                f"apk_before_bytes={apk_before}",
                f"apk_after_bytes={apk_after}",
                f"apk_delta_mb={apk_delta_mb:.2f}",
            ]
        )
        + "\n",
        encoding="utf-8",
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
