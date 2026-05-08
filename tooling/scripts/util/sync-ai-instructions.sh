#!/bin/bash
#
# sync-ai-instructions.sh
# ========================
# Legacy compatibility script.
#
# AI/Claude instructions are now centralized at the workspace level:
#   ../AGENTS.md
#   ../CLAUDE.md
#   ../gjp-open-doc/09-backend-api-harness.md
#
# This script intentionally does not copy AI.md into tool-specific files,
# because doing so would recreate duplicated and drifting instructions.

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
cd "${PROJECT_DIR}"

echo "AI instructions are centralized in the workspace harness."
echo "Read:"
echo "  - ../AGENTS.md"
echo "  - ../CLAUDE.md"
echo "  - ../gjp-open-doc/09-backend-api-harness.md"
echo ""
echo "No files were modified."
