# ADR-0001: Consolidate Multi-App Monorepo Under Single Package

## Status

Accepted

## Context

The project originally consisted of two separate Flutter projects:
- Virtual MRI Scanner in the root directory (`lib/`)
- Parameter Image Calculator in a subdirectory (`parameter_image_calculator/`)

Each had its own `pubspec.yaml`, `web/` directory, and independent build process. This led to:
- Duplicated configuration and build infrastructure
- No easy path to share code between apps
- Two separate `flutter pub get` / `flutter build` steps in CI

## Decision

Consolidate both apps into a single Flutter package with the following structure:
- `lib/vmri/` - Virtual MRI Scanner
- `lib/paracalc/` - Parameter Image Calculator
- `lib/shared/` - Shared code and configuration

Both apps are built from the same package using `--target` flags:
```bash
flutter build web --target lib/vmri/main.dart
flutter build web --target lib/paracalc/main.dart
```

## Consequences

### Positive
- Single `pubspec.yaml` with unified dependencies
- Easy code sharing via `lib/shared/`
- Simplified CI/CD pipeline
- Consistent tooling and linting across both apps

### Negative
- Both apps must use compatible dependency versions
- `package:web` is now a dependency for the entire package (only used by ParaCalc)
- Single `web/index.html` shared between builds (title set in each app's `MaterialApp`)
