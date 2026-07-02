#!/usr/bin/env python3
"""Simple Portuguese (Portugal) audit for user-facing text."""

from __future__ import annotations

import re
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
REPORT = ROOT / "build" / "reports" / "pt-pt-audit.md"
SOURCE_DIRS = [ROOT / "app" / "src" / "main" / "java", ROOT / "README.md"]

FORBIDDEN = {
    "tela": "ecrã",
    "celular": "telemóvel",
    "arquivo": "ficheiro",
    "usuário": "utilizador",
    "salvar": "guardar",
    "deletar": "remover",
    "configurações": "definições",
    "prêmio": "prémio",
}

ACCENT_HINTS = {
    "Premios": "Prémios",
    "bau": "baú",
    "Bau": "Baú",
    "niveis": "níveis",
    "Nivel": "Nível",
    "Opcoes": "Opções",
}

STRING_LITERAL_RE = re.compile(r'"(?:\\.|[^"\\])*"', re.S)


def files():
    for root in SOURCE_DIRS:
        if root.is_file():
            yield root
        elif root.exists():
            yield from root.rglob("*.kt")


def visible_text(path: Path, text: str) -> str:
    if path.suffix == ".md":
        return text
    literals = []
    for match in STRING_LITERAL_RE.finditer(text):
        raw = match.group(0)[1:-1]
        if raw.startswith("@") or ("/" in raw and " " not in raw):
            continue
        literals.append(raw)
    return "\n".join(literals)


def main() -> int:
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    lines = ["# PT-PT Audit", ""]
    issues = 0
    for path in files():
        text = visible_text(path, path.read_text(encoding="utf-8", errors="replace"))
        for word, replacement in FORBIDDEN.items():
            if re.search(rf"\b{re.escape(word)}\b", text, re.I):
                issues += 1
                lines.append(f"- `{path.relative_to(ROOT)}`: usar `{replacement}` em vez de `{word}`.")
        for word, replacement in ACCENT_HINTS.items():
            if re.search(rf"\b{re.escape(word)}\b", text):
                issues += 1
                lines.append(f"- `{path.relative_to(ROOT)}`: rever `{word}` -> `{replacement}`.")
    if issues == 0:
        lines.append("- Sem ocorrências dos termos problemáticos configurados.")
    lines.append("")
    lines.append(f"Total issues: {issues}")
    REPORT.write_text("\n".join(lines) + "\n", encoding="utf-8")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
