#!/usr/bin/env python3
"""Generate asset catalog reports for the card-based version."""

from __future__ import annotations

import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
REPORT_DIR = ROOT / "build" / "reports"
RAW = ROOT / "app" / "src" / "main" / "res" / "raw"


def load(name: str):
    path = REPORT_DIR / name
    return json.loads(path.read_text(encoding="utf-8")) if path.exists() else []


def main() -> int:
    REPORT_DIR.mkdir(parents=True, exist_ok=True)
    chests = load("chest-asset-manifest.json")
    audio = [
        {"resource": path.stem, "bytes": path.stat().st_size, "file": str(path.relative_to(ROOT))}
        for path in sorted(RAW.glob("som_*.m4a"))
    ]
    catalog = {"chests": chests, "audio": audio, "cards": {"generated_in_compose": 500}}
    (REPORT_DIR / "asset-inventory.json").write_text(json.dumps(catalog, indent=2, ensure_ascii=False), encoding="utf-8")
    (REPORT_DIR / "asset-selection.md").write_text(
        "# Seleção de Assets\n\n"
        "- Direção: cartas desenhadas em Compose e baús em pixel art.\n"
        "- Pack externo usado: Pixel Chest Pack.\n"
        "- Cartas: geradas por código, sem 500 imagens individuais.\n"
        f"- Frames de baú selecionados: {len(chests)}\n"
        f"- Áudios oficiais: {len(audio)}\n",
        encoding="utf-8",
    )
    (REPORT_DIR / "asset-license-map.md").write_text(
        "# Mapa de Licenças dos Assets\n\n"
        "- Pixel Chest Pack: autorização indicada pelo utilizador; licença local não encontrada nesta cópia.\n"
        "- Áudios S/Z/X/J: fornecidos localmente em `sounds/` para este projeto.\n",
        encoding="utf-8",
    )
    size = sum(int(item.get("bytes", 0)) for item in chests + audio)
    (REPORT_DIR / "asset-size-report.md").write_text(
        "# Relatório de Tamanho dos Assets\n\n"
        f"- Frames de baú: {len(chests)}\n"
        f"- Áudios oficiais: {len(audio)}\n"
        f"- Total selecionado: {size} bytes\n",
        encoding="utf-8",
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
