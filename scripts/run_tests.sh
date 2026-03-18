#!/bin/bash
# run_tests.sh - Run tests
# Usage:
#   ./scripts/run_tests.sh test             # All tests
#   ./scripts/run_tests.sh unit/vmri        # Specific subdirectory
set -e
cd "$(dirname "$0")/.."

if [ "$1" = "" ] || [ "$1" = "test" ]; then
  echo "Running all tests..."
  flutter test
elif [ -d "test/$1" ]; then
  echo "Running tests in test/$1..."
  flutter test "test/$1"
else
  echo "Running test file: $1"
  flutter test "$1"
fi
