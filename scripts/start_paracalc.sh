#!/bin/bash
# start_paracalc.sh - Launches Parameter Image Calculator in development mode
cd "$(dirname "$0")/.."
flutter run -d chrome --target lib/paracalc/main.dart
