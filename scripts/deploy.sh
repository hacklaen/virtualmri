#!/bin/bash
# deploy.sh - Build and deploy all apps
set -e
cd "$(dirname "$0")/.."

echo "Building Virtual MRI Scanner..."
flutter pub get
flutter build web --release --base-href "/virtualmri/" --target lib/vmri/main.dart
mv build/web build/vmri_web

echo "Building Parameter Image Calculator..."
flutter build web --release --base-href "/virtualmri/pic/" --target lib/paracalc/main.dart

echo "Combining builds..."
mkdir -p build/vmri_web/pic
cp -r build/web/* build/vmri_web/pic/
rm -rf build/web
mv build/vmri_web build/web

echo "Build complete. Output in build/web/"
echo "  Virtual MRI Scanner: build/web/"
echo "  Parameter Image Calculator: build/web/pic/"
