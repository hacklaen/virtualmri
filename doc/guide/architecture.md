# Architecture Guide

## Overview

This project is a Flutter web monorepo containing two applications that share a common package and build infrastructure.

## Data Flow

### Virtual MRI Scanner

```
ExamData (JSON/Reference)
  │  T1/T2/PD matrices (256x256 uint16)
  ▼
MriSimulator.calculate()
  │  Pulse sequence + parameters
  ▼
12-bit image (List<List<int>>, 0-4095)
  │
  ▼
MriImageDisplay (windowing: center/width → RGBA)
  │
  ▼
MriScreen (image stack, 1/4-image layout)
```

1. `ExamData` loads T1/T2/PD matrices (256x256 `List<List<int>>`) from JSON (base64 big-endian uint16) or generates reference data
2. `MriSimulator.calculate()` synchronously computes an MRI image from matrices + sequence parameters
3. Result is a 12-bit image (`List<List<int>>`, values 0-4095)
4. `MriImageDisplay` renders with windowing (center/width) via `ui.Image` + `FutureBuilder`
5. `MriScreen` manages an image stack (`_imageStack`) with 1-image/4-image toggle

### Parameter Image Calculator

```
DICOM files (browser file picker)
  │  package:web
  ▼
DicomReader.parse()
  │  Extracts pixel data + MRI parameters (TR, TE, flip angle)
  ▼
T1Calculator / T2Calculator
  │  Physics formulas + least-square regression
  ▼
Parameter images (List<List<double>>)
  │
  ▼
ParameterImageDisplay (windowing → grayscale)
```

1. `DicomLoader` uses browser file picker (`package:web`) to load DICOM files
2. `DicomReader` parses DICOM Part 10 format, extracts pixel data and MRI parameters
3. `T1Calculator` / `T2Calculator` compute parameter images using physics formulas
4. `ParameterImageDisplay` renders double-valued images with windowing

## Key Design Decisions

### State Management

Both apps use pure `setState()` - no external state management packages. This keeps the codebase simple and dependency-free (except `package:web` for DICOM file picking).

### MRI Simulation Fidelity

The simulation formulas in `mri_simulator.dart` are ported 1:1 from the Java original. Known bugs from the Java version are intentionally preserved:
- `cosangel = sin(dangel)` in Flash/GESpoiled/GERefocused
- `exp(-TR/TI)` instead of `exp(-TR/T1)` in InversionRecovery

### DICOM Library

A custom DICOM library (in `lib/paracalc/dicom/`) avoids external dependencies. It supports:
- Implicit VR Little Endian
- Explicit VR Little/Big Endian
- 16-bit MONOCHROME1/MONOCHROME2 pixel data

### Image Data Formats

| App | Image Type | Format |
|-----|-----------|--------|
| Virtual MRI | MRI simulation output | `List<List<int>>` (12-bit, 0-4095) |
| Virtual MRI | Complex k-space | `ComplexImage` (real/imag 2D lists) |
| Parameter Calculator | Parameter maps | `List<List<double>>` |
| Both | Display rendering | RGBA via `dart:ui` |
