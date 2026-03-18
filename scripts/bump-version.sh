#!/bin/bash
#
# Version Bump Script for Virtual MRI Scanner
#
# This script provides an interactive way to increment the project version
# in pubspec.yaml and Changelog.md. The new version is copied to clipboard
# for manual use in git commits.
#
# Usage:
#   ./scripts/bump-version.sh              Interactive mode
#   ./scripts/bump-version.sh patch        Bump patch version (4.0.0 -> 4.0.1)
#   ./scripts/bump-version.sh minor        Bump minor version (4.0.0 -> 4.1.0)
#   ./scripts/bump-version.sh major        Bump major version (4.0.0 -> 5.0.0)
#   ./scripts/bump-version.sh 4.1.0        Set specific version
#   ./scripts/bump-version.sh --help       Show help
#
# @author     Thomas Hacklaender <thomas@iftm.de>

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# Get script directory and change to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR"

# Source of truth for version
PUBSPEC_FILE="pubspec.yaml"
CHANGELOG_FILE="Changelog.md"

# ============================================================================
# Helper Functions
# ============================================================================

show_help() {
    echo -e "${BOLD}${BLUE}Virtual MRI Scanner - Version Bump Script${NC}"
    echo ""
    echo "Usage: ./scripts/bump-version.sh [OPTION]"
    echo ""
    echo "Options:"
    echo "  patch          Increment patch version (4.0.0 -> 4.0.1)"
    echo "  minor          Increment minor version (4.0.0 -> 4.1.0)"
    echo "  major          Increment major version (4.0.0 -> 5.0.0)"
    echo "  X.Y.Z          Set specific version (e.g., 4.1.0)"
    echo "  --help, -h     Show this help message"
    echo "  (no option)    Interactive mode"
    echo ""
    echo "This script will:"
    echo "  1. Update version in pubspec.yaml (version + build number)"
    echo "  2. Add a new section in Changelog.md"
    echo "  3. Copy commit message to clipboard"
    echo ""
    echo "After running, review and commit with:"
    echo "  git add -A && git commit -m \"chore(release): bump version to X.Y.Z\""
    echo ""
}

get_current_version() {
    # Extract version from pubspec.yaml (format: version: X.Y.Z+BUILD)
    grep -E "^version:" "$PUBSPEC_FILE" | sed -E 's/^version:[[:space:]]*([0-9]+\.[0-9]+\.[0-9]+).*/\1/'
}

get_current_build() {
    # Extract build number from pubspec.yaml (format: version: X.Y.Z+BUILD)
    local build
    build=$(grep -E "^version:" "$PUBSPEC_FILE" | sed -E 's/^version:[[:space:]]*[0-9]+\.[0-9]+\.[0-9]+\+([0-9]+)/\1/')
    echo "${build:-1}"
}

validate_version() {
    local version="$1"
    if [[ ! "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo -e "${RED}Error: Invalid version format '$version'. Expected format: X.Y.Z${NC}"
        exit 1
    fi
}

compare_versions() {
    local current="$1"
    local new="$2"

    IFS='.' read -ra CURRENT_PARTS <<< "$current"
    IFS='.' read -ra NEW_PARTS <<< "$new"

    # Compare major
    if [[ ${NEW_PARTS[0]} -lt ${CURRENT_PARTS[0]} ]]; then
        return 1
    elif [[ ${NEW_PARTS[0]} -gt ${CURRENT_PARTS[0]} ]]; then
        return 0
    fi

    # Compare minor
    if [[ ${NEW_PARTS[1]} -lt ${CURRENT_PARTS[1]} ]]; then
        return 1
    elif [[ ${NEW_PARTS[1]} -gt ${CURRENT_PARTS[1]} ]]; then
        return 0
    fi

    # Compare patch
    if [[ ${NEW_PARTS[2]} -le ${CURRENT_PARTS[2]} ]]; then
        return 1
    fi

    return 0
}

increment_version() {
    local version="$1"
    local type="$2"

    IFS='.' read -ra PARTS <<< "$version"
    local major="${PARTS[0]}"
    local minor="${PARTS[1]}"
    local patch="${PARTS[2]}"

    case "$type" in
        major)
            major=$((major + 1))
            minor=0
            patch=0
            ;;
        minor)
            minor=$((minor + 1))
            patch=0
            ;;
        patch)
            patch=$((patch + 1))
            ;;
    esac

    echo "${major}.${minor}.${patch}"
}

update_pubspec() {
    local old_version="$1"
    local new_version="$2"
    local old_build="$3"
    local new_build=$((old_build + 1))

    local old_line="version: ${old_version}+${old_build}"
    local new_line="version: ${new_version}+${new_build}"

    if grep -q "$old_line" "$PUBSPEC_FILE"; then
        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "s/$old_line/$new_line/" "$PUBSPEC_FILE"
        else
            sed -i "s/$old_line/$new_line/" "$PUBSPEC_FILE"
        fi
        echo -e "${GREEN}  ✓ Updated: $PUBSPEC_FILE ($old_line → $new_line)${NC}"
    else
        echo -e "${RED}  ✗ Could not find '$old_line' in $PUBSPEC_FILE${NC}"
        exit 1
    fi
}

update_changelog() {
    local new_version="$1"
    local date
    date=$(date +"%Y-%m-%d")

    # Check if version already exists in changelog
    if grep -q "## Version: $new_version" "$CHANGELOG_FILE"; then
        echo -e "${YELLOW}  ⚠ Version $new_version already in changelog${NC}"
        return
    fi

    # Insert new version section after the "# Release Notes" heading
    local temp_file
    temp_file=$(mktemp)
    local header_found=false

    while IFS= read -r line; do
        echo "$line" >> "$temp_file"
        # Insert after the first heading line
        if [[ $header_found == false && "$line" == "# Release Notes" ]]; then
            header_found=true
            echo "" >> "$temp_file"
            echo "## Version: $new_version" >> "$temp_file"
            echo "**Date:** $date" >> "$temp_file"
            echo "- **TODO:**" >> "$temp_file"
            echo "  - Added/Changed/Fixed/Removed: Description of change" >> "$temp_file"
        fi
    done < "$CHANGELOG_FILE"

    mv "$temp_file" "$CHANGELOG_FILE"
    echo -e "${GREEN}  ✓ Updated: $CHANGELOG_FILE (added section for $new_version)${NC}"
}

copy_to_clipboard() {
    local version="$1"
    local commit_msg="chore(release): bump version to $version"

    if command -v pbcopy > /dev/null 2>&1; then
        echo -n "$commit_msg" | pbcopy
        return 0
    elif command -v xclip > /dev/null 2>&1; then
        echo -n "$commit_msg" | xclip -selection clipboard
        return 0
    elif command -v xsel > /dev/null 2>&1; then
        echo -n "$commit_msg" | xsel --clipboard --input
        return 0
    elif command -v clip.exe > /dev/null 2>&1; then
        echo -n "$commit_msg" | clip.exe
        return 0
    else
        return 1
    fi
}

interactive_mode() {
    local current_version="$1"

    echo "" >&2
    echo -e "${BOLD}Select version bump type:${NC}" >&2
    echo "" >&2
    echo -e "  ${CYAN}1)${NC} patch  $(increment_version "$current_version" "patch")  (Bug fixes)" >&2
    echo -e "  ${CYAN}2)${NC} minor  $(increment_version "$current_version" "minor")  (New features)" >&2
    echo -e "  ${CYAN}3)${NC} major  $(increment_version "$current_version" "major")  (Breaking changes)" >&2
    echo -e "  ${CYAN}4)${NC} custom (Enter specific version)" >&2
    echo -e "  ${CYAN}5)${NC} cancel" >&2
    echo "" >&2

    read -p "$(echo -e "${CYAN}Enter choice [1-5]: ${NC}")" choice

    case "$choice" in
        1)
            echo "patch"
            ;;
        2)
            echo "minor"
            ;;
        3)
            echo "major"
            ;;
        4)
            read -p "$(echo -e "${CYAN}Enter version (X.Y.Z): ${NC}")" custom_version
            echo "$custom_version"
            ;;
        5|*)
            echo "cancel"
            ;;
    esac
}

# ============================================================================
# Main Script
# ============================================================================

main() {
    echo ""
    echo -e "${BOLD}${BLUE}════════════════════════════════════════════════════════════${NC}"
    echo -e "${BOLD}${BLUE}  Virtual MRI Scanner - Version Bump${NC}"
    echo -e "${BOLD}${BLUE}════════════════════════════════════════════════════════════${NC}"

    # Check for help flag
    if [[ "$1" == "--help" || "$1" == "-h" ]]; then
        show_help
        exit 0
    fi

    # Verify pubspec.yaml exists
    if [[ ! -f "$PUBSPEC_FILE" ]]; then
        echo -e "${RED}Error: $PUBSPEC_FILE not found. Run from project root.${NC}"
        exit 1
    fi

    # Get current version and build number
    CURRENT_VERSION=$(get_current_version)
    CURRENT_BUILD=$(get_current_build)
    echo ""
    echo -e "${BOLD}Current version:${NC} ${CYAN}${CURRENT_VERSION}+${CURRENT_BUILD}${NC}"

    # Determine new version
    if [[ -z "$1" ]]; then
        BUMP_TYPE=$(interactive_mode "$CURRENT_VERSION")

        if [[ "$BUMP_TYPE" == "cancel" ]]; then
            echo "Aborted."
            exit 0
        fi
    else
        BUMP_TYPE="$1"
    fi

    # Calculate new version
    case "$BUMP_TYPE" in
        patch|minor|major)
            NEW_VERSION=$(increment_version "$CURRENT_VERSION" "$BUMP_TYPE")
            ;;
        *)
            NEW_VERSION="$BUMP_TYPE"
            validate_version "$NEW_VERSION"

            if ! compare_versions "$CURRENT_VERSION" "$NEW_VERSION"; then
                echo -e "${RED}Error: New version ($NEW_VERSION) must be greater than current ($CURRENT_VERSION)${NC}"
                exit 1
            fi
            ;;
    esac

    NEW_BUILD=$((CURRENT_BUILD + 1))
    echo -e "${BOLD}New version:${NC}     ${GREEN}${NEW_VERSION}+${NEW_BUILD}${NC}"
    echo ""

    # Confirm
    read -p "$(echo -e "${CYAN}Proceed with version bump? (y/n): ${NC}")" confirm
    if [[ ! "$confirm" =~ ^[Yy]$ ]]; then
        echo "Aborted."
        exit 0
    fi

    echo ""
    echo -e "${BLUE}Updating files...${NC}"

    # Update pubspec.yaml
    update_pubspec "$CURRENT_VERSION" "$NEW_VERSION" "$CURRENT_BUILD"

    # Update changelog
    update_changelog "$NEW_VERSION"

    # Copy commit message to clipboard
    echo ""
    if copy_to_clipboard "$NEW_VERSION"; then
        echo -e "${GREEN}  ✓ Commit message copied to clipboard${NC}"
    else
        echo -e "${YELLOW}  ⚠ Could not copy to clipboard (no clipboard tool found)${NC}"
    fi

    echo ""
    echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}  ✓ Version bumped to ${NEW_VERSION}+${NEW_BUILD}${NC}"
    echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
    echo ""

    # Summary
    echo -e "${BOLD}Summary:${NC}"
    echo -e "  • Version: ${CURRENT_VERSION}+${CURRENT_BUILD} → ${NEW_VERSION}+${NEW_BUILD}"
    echo -e "  • Updated: pubspec.yaml, Changelog.md"
    echo -e "  • Commit message: copied to clipboard"
    echo ""
    echo -e "${BOLD}Next steps:${NC}"
    echo -e "  1. Edit changelog:  ${CYAN}Fill in changes in Changelog.md${NC}"
    echo -e "  2. Review changes:  ${CYAN}git diff${NC}"
    echo -e "  3. Stage & commit:  ${CYAN}git add -A && git commit -m \"<paste>\"${NC}"
    echo -e "  4. Create tag:      ${CYAN}git tag -a v$NEW_VERSION -m \"Release $NEW_VERSION\"${NC}"
    echo -e "  5. Push:            ${CYAN}git push origin flutter && git push --tags${NC}"
    echo ""
}

main "$@"
