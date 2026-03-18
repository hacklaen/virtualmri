#!/bin/bash
# start_vmri.sh - Launches Virtual MRI Scanner in development mode
cd "$(dirname "$0")/.."
flutter run -d chrome --target lib/vmri/main.dart
