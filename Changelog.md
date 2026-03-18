# Release Notes

## Version: 1.0.1
**Date:** 2026-03-18
- **TODO:**
  - Added/Changed/Fixed/Removed: Description of change

## Version: 1.0.0
**Date:** 2026-03-18
- **General:**
  - Added: Initial Flutter Web release of Virtual MRI Scanner and Parameter Image Calculator
  - Added: Monorepo structure with shared pubspec.yaml for both apps
- **Architecture:**
  - Added: GetX state management with GetMaterialApp, GetxController, GetView, GetBuilder
  - Added: Page convention with binding/controller/page files per screen
  - Added: Named route navigation for both apps
- **Virtual MRI Scanner (vmri):**
  - Added: 7 pulse sequences (SR, IR, SE, TSE, Flash, GE Spoiled, GE Refocused)
  - Added: Motion and Periodic Motion artifacts
  - Added: K-Space Manipulator dialog with FFT visualization
  - Added: Image windowing (Center/Width) and 1/4-image stack view
  - Added: Brain reference dataset (JSON, base64 uint16)
- **Parameter Image Calculator (paracalc):**
  - Added: T1 calculation from 2 FLASH images
  - Added: T2/PD calculation from multi-echo SE via least-square fit
  - Added: Custom DICOM library (Implicit/Explicit VR, Little/Big Endian)
  - Added: Selective Median and Smooth filters
  - Added: Web-based DICOM file picker
