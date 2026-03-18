# Documentation Hub

This is the central documentation hub for the Virtual MRI project, which includes two Flutter web applications:

- **Virtual MRI Scanner** - MRI pulse sequence simulator
- **Parameter Image Calculator** - T1/T2/PD parameter image calculation from DICOM data

## Quick Links

- [Quick Start Guide](QUICKSTART.md)
- [Architecture Guide](guide/architecture.md)
- [Deployment Guide](guide/deployment.md)
- [Architecture Decision Records](adr/)
- [User Manual (MRT 3.2.3)](manual/mrt_user_manual_3.2.3_060412.md)

## Project Structure

```
lib/
├── vmri/           # Virtual MRI Scanner app
├── paracalc/       # Parameter Image Calculator app
└── shared/         # Shared code and configuration
```

## Apps

### Virtual MRI Scanner (`lib/vmri/`)

Simulates MRI imaging with authentic physics formulas, deployed as a Flutter web app.

- **Live:** https://hacklaen.github.io/virtualmri/
- **Entry point:** `lib/vmri/main.dart`
- **7 Pulse sequences:** SR, IR, SE, TSE, Flash, GE Spoiled, GE Refocused
- **Artifacts:** Motion, Periodic Motion (via k-space manipulation)
- **FFT:** Cooley-Tukey Radix-2 for TSE, motion artifacts, and k-space manipulator

### Parameter Image Calculator (`lib/paracalc/`)

Calculates T1, T2, and PD parameter images from DICOM MRI data.

- **Live:** https://hacklaen.github.io/virtualmri/pic/
- **Entry point:** `lib/paracalc/main.dart`
- **T1 calculation:** From 2 FLASH images with different flip angles
- **T2/PD calculation:** Least-square fit on multi-echo spin echo data
- **DICOM support:** Implicit VR LE, Explicit VR LE/BE, 16-bit MONOCHROME1/2

## Documentation Structure

| Directory | Content |
|-----------|---------|
| [guide/](guide/) | Developer guides |
| [adr/](adr/) | Architecture Decision Records |
| [manual/](manual/) | User manual (PDF, DOC, Markdown) |
