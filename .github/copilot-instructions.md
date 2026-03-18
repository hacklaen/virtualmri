# Virtual MRI Scanner – Copilot Instructions

This document defines the project structure and coding conventions for the Virtual MRI Scanner monorepo. It contains two Flutter Web apps sharing a single `pubspec.yaml`.

---

**Important:** Language Requirements

| Content Type | Language |
|--------------|----------|
| Code comments | English |
| Doc comments | English |
| Documentation files | English |

---

## Project Overview

The monorepo contains two educational MRI tools, both deployed as pure Flutter Web apps on GitHub Pages:

| App | Description | Entry Point | URL |
|-----|-------------|-------------|-----|
| **Virtual MRI Scanner (vmri)** | Simulates MRI imaging with authentic physics formulas (7 pulse sequences, artifacts, k-space manipulation) | `lib/vmri/main.dart` | https://hacklaen.github.io/virtualmri/ |
| **Parameter Image Calculator (paracalc)** | Calculates T1, T2 and PD parameter images from DICOM MRI data | `lib/paracalc/main.dart` | https://hacklaen.github.io/virtualmri/pic/ |

- **Language:** Dart / Flutter (Web only)
- **SDK:** Dart >= 3.11.1, Flutter stable
- **Dependencies:** Flutter SDK, `get` (GetX), `web: ^1.1.1` (for browser file picker in paracalc), `flutter_lints`
- **State management:** GetX exclusively – **NEVER use `StatefulWidget`**
- **Repo:** `hacklaen/virtualmri`, Branch `flutter`

---

## Project Structure

```
project-root/
├── .github/
│   ├── copilot-instructions.md    # This file
│   └── workflows/deploy.yml       # GitHub Pages deployment
├── doc/
│   ├── README.md                  # Documentation hub
│   ├── QUICKSTART.md              # Quick setup guide
│   ├── adr/                       # Architecture Decision Records
│   └── guide/                     # Developer guides
├── lib/
│   ├── vmri/                      # Virtual MRI Scanner app
│   │   ├── main.dart              # Entry point: GetMaterialApp
│   │   ├── models/
│   │   │   └── exam_data.dart     # ExamData: T1/T2/PD parameter matrices (256x256)
│   │   ├── navigation/
│   │   │   ├── app_pages.dart     # GetPage route definitions
│   │   │   └── app_routes.dart    # Route name constants
│   │   ├── pages/
│   │   │   └── mri_screen/        # Main MRI screen
│   │   │       ├── mri_screen_binding.dart
│   │   │       ├── mri_screen_controller.dart
│   │   │       └── mri_screen_page.dart
│   │   ├── simulation/
│   │   │   ├── fft.dart           # ComplexImage, forwardFFT2D, inverseFFT2D (Cooley-Tukey)
│   │   │   └── mri_simulator.dart # MriSimulator.calculate() – 7 pulse sequences + artifacts
│   │   └── widgets/
│   │       ├── kspace_manipulator.dart  # K-space dialog: 4-panel view, manipulation, IFFT
│   │       ├── mri_image_display.dart   # Image rendering with windowing (12-bit -> RGBA)
│   │       └── param_slider.dart        # Slider + text field for int parameters
│   ├── paracalc/                  # Parameter Image Calculator app
│   │   ├── main.dart              # Entry point: GetMaterialApp
│   │   ├── navigation/
│   │   │   ├── app_pages.dart     # GetPage route definitions
│   │   │   └── app_routes.dart    # Route name constants
│   │   ├── pages/
│   │   │   └── calculator/        # Main calculator screen
│   │   │       ├── calculator_binding.dart
│   │   │       ├── calculator_controller.dart
│   │   │       └── calculator_page.dart
│   │   ├── calculation/
│   │   │   ├── image_filters.dart # Selective Median/Smooth filter
│   │   │   ├── least_square.dart  # Linear least-square regression
│   │   │   ├── t1_calculator.dart # T1 calculation from 2 FLASH images
│   │   │   └── t2_calculator.dart # T2/PD calculation from multi-echo SE
│   │   ├── dicom/
│   │   │   ├── data_element.dart      # DICOM Data Element parser
│   │   │   ├── dicom_exception.dart   # DICOM-specific exceptions
│   │   │   └── dicom_reader.dart      # DICOM Part 10 reader
│   │   ├── screens/
│   │   │   └── dicom_loader.dart      # Web file picker (package:web)
│   │   └── widgets/
│   │       ├── param_slider.dart              # Slider + text field for double parameters
│   │       └── parameter_image_display.dart   # Grayscale image with windowing
│   └── shared/                    # Shared components (currently placeholder)
│       └── config/                # Future shared configuration
├── scripts/
│   ├── bump-version.sh             # Interactive version bump (pubspec + changelog)
│   ├── start_vmri.sh              # Launch vmri in Chrome
│   ├── start_paracalc.sh          # Launch paracalc in Chrome
│   ├── deploy.sh                  # Build both apps for deployment
│   └── run_tests.sh               # Run all tests
├── test/
│   ├── widget_test.dart           # Smoke test
│   └── unit/
│       ├── vmri/                  # vmri unit tests (fft, exam_data, mri_simulator)
│       └── paracalc/              # paracalc unit tests (filters, t1, t2, dicom, least_square)
├── web/                           # Web shell (index.html etc.)
├── web_assets/
│   └── brain_no_pathology.json    # Brain dataset: base64-encoded uint16 BE matrices
├── pubspec.yaml                   # Single package: virtual_mri
├── Changelog.md                   # Version history
└── Readme.md                      # Project overview
```

---

## Code Conventions

### Code Style
- **Private members:** Underscore prefix (`_examData`, `_params`)
- **Const constructors** where possible
- **Material3** design with `blueGrey` seed color
- **Responsive layout:** `LayoutBuilder` for wide (Row) / narrow (Column)
- **State management:** GetX exclusively – **NEVER use `StatefulWidget`**

### Data Formats

| Type | Format |
|------|--------|
| MRI images | `List<List<int>>` – 256x256, values 0-4095 (12-bit) |
| Complex images | `ComplexImage` class with separate `real`/`imag` 2D lists |
| Parameter matrices | `List<List<int>>` – 256x256, values from uint16 range |
| JSON keys for exam data | `t1`, `t2`, `pd` (NOT `t1_base64` etc.) |
| DICOM pixel data | 16-bit MONOCHROME1/MONOCHROME2 |

### Doc Comments

All public classes, methods, and properties must have doc comments (`///`).

### Inline Comments

Use inline comments for complex logic, non-obvious decisions, algorithm steps, and workarounds with reasoning.

---

## App Structure (under `/lib/<app_name>/`)

Each app follows this consistent structure:

```
lib/<app_name>/
├── main.dart                      # App entry point (GetMaterialApp)
├── navigation/
│   ├── app_pages.dart             # Route definitions (GetPages)
│   └── app_routes.dart            # Route name constants
├── pages/
│   ├── <page_name>/               # One folder per page
│   │   ├── <page_name>_binding.dart
│   │   ├── <page_name>_controller.dart
│   │   └── <page_name>_page.dart
│   └── ...
├── models/                        # App-specific models (optional)
└── widgets/                       # Reusable widgets
```

### Page Structure Convention

Every page consists of these files with the page name as prefix:

| File | Purpose |
|------|---------|
| `<page_name>_binding.dart` | Dependency injection using `Bindings` |
| `<page_name>_controller.dart` | Business logic using `GetxController` |
| `<page_name>_page.dart` | UI using `GetView<Controller>` or `StatelessWidget` |

---

## State Management (GetX)

### Core Principles

- **NEVER use `StatefulWidget`** anywhere in the codebase
- Use **GetX exclusively** for all state management
- All mutable UI state must use `GetxController` with `update()`

### Widget Patterns

| Pattern | When to Use |
|---------|-------------|
| `GetView<ControllerType>` | Pages and widgets with a single controller |
| `StatelessWidget` + `Get.find<T>()` | Widgets needing multiple controllers |
| `GetBuilder<T>` | UI updates via `update()` calls |
| `Obx(() => ...)` | Reactive UI updates with `.obs` variables |

### Controller Registration

```dart
// In Binding class
class MriScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<MriScreenController>(() => MriScreenController());
  }
}
```

---

## Virtual MRI Scanner (vmri)

### Architecture & Data Flow

1. `ExamData` loads T1/T2/PD matrices (256x256 `List<List<int>>`) from JSON (base64 Big-Endian uint16) or generates reference data
2. `MriSimulator.calculate()` computes an MRI image synchronously from the matrices + sequence parameters
3. Result is a 12-bit image (`List<List<int>>`, values 0-4095)
4. `MriImageDisplay` renders with windowing (Center/Width) via `ui.Image` + `FutureBuilder`
5. `MriScreenController` manages an image stack (`imageStack`) with 1-image/4-image toggle

### MRI Simulation Details
- **7 pulse sequences:** SR, IR, SE, TSE, Flash, GE Spoiled, GE Refocused
- **Artifacts:** Motion, Periodic Motion (via k-space manipulation)
- **FFT:** Cooley-Tukey Radix-2 for TSE, motion artifacts and k-space manipulator
- **Image size:** Fixed 256x256 (`_size = 256`)

### Known Legacy Bugs (DO NOT FIX!)

These bugs are intentionally preserved from the original implementation and must NOT be corrected:

1. **`cosangel = sin(dangel)`** in Flash/GESpoiled/GERefocused (should be `cos`)
2. **`exp(-TR/TI)`** instead of `exp(-TR/T1)` in InversionRecovery

### PD Scaling
- JSON data already has x25 scaling applied
- `createReferenceData()` applies x25 itself

---

## Parameter Image Calculator (paracalc)

### DICOM Support
- Custom DICOM library without external dependencies
- Supports Implicit VR Little Endian, Explicit VR Little/Big Endian
- 16-bit MONOCHROME1/MONOCHROME2 pixel data
- Automatically extracts MRI parameters (TR, TE, Flip Angle) from DICOM headers

### Calculation Formulas
- **T1:** `T1 = TR / ln((sin(a1)cos(a2) - q*sin(a2)cos(a1)) / (sin(a1) - q*sin(a2)))`, q = S1/S2
- **T2/PD:** Least-square fit on `ln(S) = ln(PD) - TE/T2`
- **Filters:** Selective Median (outliers above), Selective Smooth (outliers below)

---

## Build & Deployment

```bash
# Start vmri locally
./scripts/start_vmri.sh
# or: flutter run -d chrome --target lib/vmri/main.dart

# Start paracalc locally
./scripts/start_paracalc.sh
# or: flutter run -d chrome --target lib/paracalc/main.dart

# Analyze
flutter analyze

# Run tests
./scripts/run_tests.sh test

# Deployment: automatic via GitHub Actions on push to flutter branch
git push origin flutter
```

The GitHub Actions workflow builds both apps with `--target` flags and deploys them to GitHub Pages:
- Virtual MRI Scanner -> `/virtualmri/`
- Parameter Image Calculator -> `/virtualmri/pic/`

---

## Testing

### Test Organization

```
test/
├── widget_test.dart           # App smoke test
└── unit/
    ├── vmri/
    │   ├── fft_test.dart              # FFT roundtrip, Parseval's theorem
    │   ├── exam_data_test.dart        # Reference data, scaling, matrix dimensions
    │   └── mri_simulator_test.dart    # All sequences, clamping, edge cases
    └── paracalc/
        ├── least_square_test.dart     # Linear fit, noisy data, weights
        ├── image_filters_test.dart    # Median, smooth, boundary behavior
        ├── t1_calculator_test.dart    # Background, clamping, threshold
        ├── t2_calculator_test.dart    # Exponential decay, clamping
        └── dicom_exception_test.dart  # Exception types
```

### Testing Requirements

When modifying code:
- Run `./scripts/run_tests.sh test` to verify nothing is broken
- Review and update related tests:
  - **Obsolete** tests (testing removed functionality) - **delete them**
  - **Outdated** tests (testing changed behavior) - **fix assertions**
  - **Missing** tests for new/changed behavior - **add them**

### What to Test for New Code

| New Code | Required Tests | Location |
|----------|---------------|----------|
| Data model | Unit test (parsing, serialization, edge cases) | `test/unit/<app>/` |
| Calculation/algorithm | Unit test (correctness, edge cases) | `test/unit/<app>/` |
| UI widget | Widget test (rendering, interaction) | `test/widget/` |

### Test Conventions

- Test names start with `should`: `'should parse valid JSON'`
- No external test dependencies beyond `flutter_test`

### Running Tests

```bash
./scripts/run_tests.sh test             # All tests
flutter test test/unit/vmri/            # Specific subdirectory
```

---

## Documentation Structure (`/doc/`)

| Path | Content |
|------|---------|
| `/doc/README.md` | Central documentation hub with links |
| `/doc/QUICKSTART.md` | New developer setup guide |
| `/doc/adr/` | Architecture Decision Records |
| `/doc/guide/` | Developer guides (modular chapters) |

### Architecture Decision Records (ADRs)

Create an ADR for significant technical decisions:

```markdown
# ADR-XXXX: Title

## Status
Accepted | Proposed | Deprecated

## Context
What is the issue that we're seeing that motivates this decision?

## Decision
What is the change that we're proposing and/or doing?

## Consequences
What becomes easier or more difficult to do because of this change?
```

---

## Scripts (`/scripts/`)

| Script | Purpose |
|--------|---------|
| `bump-version.sh` | Interactive version bump (updates pubspec.yaml + Changelog.md) |
| `start_vmri.sh` | Launch Virtual MRI Scanner in Chrome |
| `start_paracalc.sh` | Launch Parameter Image Calculator in Chrome |
| `deploy.sh` | Build both apps for deployment |
| `run_tests.sh` | Run all unit + widget tests |

---

## Version Management

- Version defined in `pubspec.yaml` (format: `MAJOR.MINOR.PATCH+BUILD`)
- Use `./scripts/bump-version.sh` to bump version (updates pubspec.yaml + Changelog.md)
- Supports: `patch`, `minor`, `major`, specific version, or interactive mode

### Changelog Format

```markdown
## Version: X.Y.Z
**Date:** YYYY-MM-DD
- **Category:**
  - Added/Changed/Fixed/Removed: Description of change
```

---

## Backward Compatibility Policy

This project does **NOT maintain backward compatibility**:

- **Delete** obsolete code, tests, and documentation immediately
- Do **not** mark items as `@deprecated` or "outdated"
- Do **not** keep legacy code paths "for compatibility"
- Clean removal is preferred over technical debt

---

## End of Session Checklist

Before ending a coding session:

1. **Changelog**: Document user-facing changes in `Changelog.md`
2. **Tests**: Run affected tests to verify changes
3. **Documentation**: Update `/doc/` files if architecture changed
