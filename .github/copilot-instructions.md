# Virtual MRI Scanner – Copilot Instructions

## Projektübersicht

Flutter-Web-Portierung des Java-basierten „Virtual MRI Scanner" (ehemals `VMRTFrame`). Die App simuliert MRT-Bildgebung mit authentischen Physikformeln und ist als reine Web-App auf GitHub Pages deployed.

- **Sprache:** Dart / Flutter (Web)
- **SDK:** Dart ≥ 3.11.1, Flutter stable
- **Keine externen Pakete** – nur Flutter SDK + flutter_lints
- **Repo:** `hacklaen/virtualmri`, Branch `flutter`
- **Live:** https://hacklaen.github.io/virtualmri/

## Projektstruktur

```
lib/
├── main.dart                        # Entry Point: VirtualMriApp → MriScreen
├── models/
│   └── exam_data.dart               # ExamData: T1/T2/PD-Parametermatrizen (256×256)
├── simulation/
│   ├── fft.dart                     # ComplexImage, forwardFFT2D, inverseFFT2D (Cooley-Tukey)
│   └── mri_simulator.dart           # MriSimulator.calculate() – 7 Pulssequenzen + Artefakte
└── widgets/
    ├── kspace_manipulator.dart      # K-Raum Dialog: 4-Panel-Ansicht, Manipulation, IFFT
    ├── mri_image_display.dart       # Bild-Rendering mit Windowing (12-bit → RGBA)
    ├── mri_screen.dart              # Haupt-UI: Layout, Parameter, Bildstapel, Steuerung
    └── param_slider.dart            # Slider + Textfeld für Parameter-Eingabe
web_assets/
    └── brain_no_pathology.json      # Hirn-Datensatz: base64-kodierte uint16 Big-Endian Matrizen
.github/workflows/
    └── deploy.yml                   # GitHub Pages Deployment (Push auf flutter-Branch)
```

## Architektur & Datenfluss

1. `ExamData` lädt T1/T2/PD-Matrizen (256×256 `List<List<int>>`) aus JSON (base64 Big-Endian uint16) oder erzeugt Referenzdaten
2. `MriSimulator.calculate()` berechnet synchron ein MRT-Bild aus den Matrizen + Sequenzparametern
3. Ergebnis ist ein 12-bit Bild (`List<List<int>>`, Werte 0–4095)
4. `MriImageDisplay` rendert mit Windowing (Center/Width) über `ui.Image` + `FutureBuilder`
5. `MriScreen` verwaltet einen Bildstapel (`_imageStack`) mit 1-Bild/4-Bild-Umschaltung

## Wichtige Konventionen

### Code-Stil
- **Private Member:** Unterstrich-Präfix (`_examData`, `_params`)
- **Const-Konstruktoren** wo möglich
- **Material3** Design mit `blueGrey` Seed-Color
- **Responsive Layout:** `LayoutBuilder` für Wide (Row) / Narrow (Column)
- **Keine externen State-Management-Pakete** – reines `setState()`

### Datenformate
- **Bilder:** `List<List<int>>` – 256×256, Werte 0–4095 (12-bit)
- **Komplexe Bilder:** `ComplexImage` Klasse mit separaten `real`/`imag` 2D-Listen
- **Parametermatrizen:** `List<List<int>>` – 256×256, Werte aus uint16-Bereich
- **JSON-Keys für Exam-Daten:** `t1`, `t2`, `pd` (NICHT `t1_base64` etc.)

### MRI-Simulation
- **7 Pulssequenzen:** SR, IR, SE, TSE, Flash, GE Spoiled, GE Refocused
- **Artefakte:** Motion, Periodic Motion (über K-Raum-Manipulation)
- **FFT:** Cooley-Tukey Radix-2 für TSE, Bewegungsartefakte und K-Raum-Manipulator
- **Bildgröße:** Fest 256×256 (`_size = 256`)

### Bekannte Java-Bugs (absichtlich beibehalten!)
Diese Bugs sind 1:1 aus dem Java-Original übernommen und dürfen NICHT korrigiert werden:
1. **`cosangel = sin(dangel)`** in Flash/GESpoiled/GERefocused (sollte `cos` sein)
2. **`exp(-TR/TI)`** statt `exp(-TR/T1)` in InversionRecovery

### PD-Skalierung
- JSON-Daten haben bereits ×25 Skalierung
- `createReferenceData()` wendet ×25 selbst an

## Build & Deployment

```bash
# Lokal starten
flutter run -d chrome

# Analysieren
flutter analyze

# Web-Build
flutter build web --release

# Deployment: automatisch via GitHub Actions bei Push auf flutter-Branch
git push origin flutter
```

Der GitHub Actions Workflow baut mit `--base-href "/virtualmri/"` und deployed nach GitHub Pages.

## Hinweise für Änderungen

- Alle Simulationsformeln in `mri_simulator.dart` stammen aus der Java-Vorlage – Änderungen nur bei echten Bugs
- `mri_screen.dart` ist die größte Datei (~820 Zeilen) – enthält gesamte UI-Logik
- Neue Widgets in `lib/widgets/` anlegen, neue Modelle in `lib/models/`
- Tests in `test/widget_test.dart` – aktuell nur Smoke-Test
- Kein `.env` oder Secrets nötig – reines Frontend
