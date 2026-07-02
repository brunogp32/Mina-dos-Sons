#!/usr/bin/env python3
"""Compatibility wrapper for the asset pipeline."""

from __future__ import annotations

import subprocess
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]


def main() -> int:
    analyse = subprocess.run([sys.executable, str(ROOT / "tools" / "analyse_design_sources.py")], cwd=ROOT)
    if analyse.returncode != 0:
        return analyse.returncode
    prepare = subprocess.run([sys.executable, str(ROOT / "tools" / "build_chest_assets.py")], cwd=ROOT)
    return prepare.returncode


if __name__ == "__main__":
    raise SystemExit(main())
