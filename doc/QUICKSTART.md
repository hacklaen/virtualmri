# Quick Start Guide

## Prerequisites

- Flutter SDK (stable channel, Dart >= 3.11.1)
- Chrome browser (for web development)
- Git

## Setup

```bash
git clone https://github.com/hacklaen/virtualmri.git
cd virtualmri
git checkout flutter
flutter pub get
```

## Running the Apps

### Virtual MRI Scanner

```bash
./scripts/start_vmri.sh
# or manually:
flutter run -d chrome --target lib/vmri/main.dart
```

### Parameter Image Calculator

```bash
./scripts/start_paracalc.sh
# or manually:
flutter run -d chrome --target lib/paracalc/main.dart
```

## Running Tests

```bash
./scripts/run_tests.sh test          # All tests
./scripts/run_tests.sh unit/vmri     # VMRI unit tests only
./scripts/run_tests.sh unit/paracalc # ParaCalc unit tests only
```

## Building for Production

```bash
./scripts/deploy.sh
```

This builds both apps and combines them into `build/web/`:
- Virtual MRI Scanner at root (`/`)
- Parameter Image Calculator at `/pic/`

## Project Structure

```
lib/
├── vmri/                    # Virtual MRI Scanner
│   ├── main.dart            # Entry point
│   ├── models/              # Data models (ExamData)
│   ├── simulation/          # MRI simulation (FFT, sequences)
│   └── widgets/             # UI components
├── paracalc/                # Parameter Image Calculator
│   ├── main.dart            # Entry point
│   ├── calculation/         # T1/T2 calculators, filters
│   ├── dicom/               # DICOM reader library
│   ├── screens/             # App screens
│   └── widgets/             # UI components
└── shared/                  # Shared code
    └── config/              # Shared configuration
```

## Deployment

Deployment is automatic via GitHub Actions on push to the `flutter` branch.
The workflow builds both apps and deploys them to GitHub Pages.
