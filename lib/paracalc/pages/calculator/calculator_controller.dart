import 'package:get/get.dart';

import '../../calculation/image_filters.dart';
import '../../calculation/t1_calculator.dart';
import '../../calculation/t2_calculator.dart';
import '../../models/loaded_image.dart';
import '../../screens/dicom_loader.dart';

/// Business logic controller for the calculator screen.
class CalculatorController extends GetxController {
  // Loaded DICOM images
  final List<LoadedImage> images = [];

  // Calculation mode
  String mode = 'T1'; // 'T1' or 'T2'

  // T1 Parameters
  double tr = 30.0;
  double alpha1 = 10.0;
  double alpha2 = 80.0;

  // T2 Parameters
  double teFirst = 20.0;
  double teStep = 20.0;

  // Common
  int backgroundSignal = 5;

  // Filter settings
  bool applyMedianFilter = false;
  double medianFactor = 3.0;
  bool applySmoothFilter = false;
  double smoothCutOff = 2.0;

  // Result images
  List<List<double>>? t1Result;
  List<List<double>>? t2Result;
  List<List<double>>? pdResult;

  // Windowing
  double t1WinCenter = 750.0;
  double t1WinWidth = 1500.0;
  double t2WinCenter = 250.0;
  double t2WinWidth = 500.0;
  double pdWinCenter = 500.0;
  double pdWinWidth = 1000.0;

  // Status
  String statusMessage = 'Load DICOM images to start.';
  bool calculating = false;

  /// Whether calculation can be performed.
  bool canCalculate() {
    if (images.isEmpty) return false;
    if (mode == 'T1' && images.length != 2) return false;
    if (mode == 'T2' && images.length < 3) return false;
    return true;
  }

  /// Load DICOM images via file picker.
  Future<void> loadImages() async {
    try {
      final loaded = await DicomLoader.pickAndLoadFiles();
      if (loaded.isEmpty) return;

      images.addAll(loaded);
      statusMessage = '${images.length} image(s) loaded.';
      update();

      // Try to extract sequence parameters from DICOM metadata
      _tryExtractParameters();
    } catch (e) {
      statusMessage = 'Error loading files: $e';
      update();
    }
  }

  void _tryExtractParameters() {
    if (images.isEmpty) return;
    final info = images.first.reader.getImageInfo();

    if (info.repetitionTime != null) {
      tr = info.repetitionTime!;
    }
    if (info.echoTime != null) {
      if (mode == 'T2') {
        teFirst = info.echoTime!;
      }
    }
    if (info.flipAngle != null && images.isNotEmpty) {
      alpha1 = info.flipAngle!;
    }
    if (images.length >= 2) {
      final info2 = images[1].reader.getImageInfo();
      if (info2.flipAngle != null) {
        alpha2 = info2.flipAngle!;
      }
    }
    update();
  }

  /// Remove a specific image.
  void removeImage(LoadedImage img) {
    images.remove(img);
    statusMessage = '${images.length} image(s) loaded.';
    update();
  }

  /// Clear all loaded images and results.
  void clearImages() {
    images.clear();
    t1Result = null;
    t2Result = null;
    pdResult = null;
    statusMessage = 'Load DICOM images to start.';
    update();
  }

  /// Auto-detect background signal from loaded images.
  void autoBackground() {
    final matrices = images.map((img) => img.pixelData).toList();
    final bg = T1Calculator.calculateBackground(matrices);
    backgroundSignal = bg;
    statusMessage = 'Background signal auto-detected: $bg';
    update();
  }

  /// Run the calculation.
  Future<void> calculate() async {
    calculating = true;
    statusMessage = 'Calculating...';
    t1Result = null;
    t2Result = null;
    pdResult = null;
    update();

    // Use a microtask to let the UI update before heavy computation
    await Future.delayed(const Duration(milliseconds: 50));

    try {
      if (mode == 'T1') {
        _calculateT1();
      } else {
        _calculateT2();
      }
    } catch (e) {
      statusMessage = 'Calculation error: $e';
    } finally {
      calculating = false;
      update();
    }
  }

  void _calculateT1() {
    if (images.length != 2) {
      statusMessage = 'Exactly 2 images required for T1.';
      return;
    }

    var result = T1Calculator.calculate(
      image1: images[0].pixelData,
      image2: images[1].pixelData,
      tr: tr,
      alpha1: alpha1,
      alpha2: alpha2,
      backgroundSignal: backgroundSignal,
    );

    if (applyMedianFilter) {
      result = ImageFilters.selectiveMedianFilter(result,
          limitFactor: medianFactor);
    }
    if (applySmoothFilter) {
      result =
          ImageFilters.selectiveSmoothFilter(result, cutOff: smoothCutOff);
    }

    t1Result = result;
    statusMessage = 'T1 calculation complete.';
  }

  void _calculateT2() {
    if (images.length < 3) {
      statusMessage = 'At least 3 images required for T2/PD.';
      return;
    }

    final matrices = images.map((img) => img.pixelData).toList();
    final t2pd = T2Calculator.calculate(
      images: matrices,
      teFirst: teFirst,
      teStep: teStep,
      backgroundSignal: backgroundSignal,
    );

    var t2Res = t2pd.t2Map;
    var pdRes = t2pd.pdMap;

    if (applyMedianFilter) {
      t2Res = ImageFilters.selectiveMedianFilter(t2Res,
          limitFactor: medianFactor);
      pdRes = ImageFilters.selectiveMedianFilter(pdRes,
          limitFactor: medianFactor);
    }
    if (applySmoothFilter) {
      t2Res =
          ImageFilters.selectiveSmoothFilter(t2Res, cutOff: smoothCutOff);
      pdRes =
          ImageFilters.selectiveSmoothFilter(pdRes, cutOff: smoothCutOff);
    }

    t2Result = t2Res;
    pdResult = pdRes;
    statusMessage = 'T2/PD calculation complete.';
  }
}
