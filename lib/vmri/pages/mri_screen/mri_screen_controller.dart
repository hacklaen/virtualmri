import 'dart:math';

import 'package:get/get.dart';

import '../../models/exam_data.dart';
import '../../simulation/mri_simulator.dart';

/// Business logic controller for the MRI screen.
class MriScreenController extends GetxController {
  ExamData? examData;
  final List<List<List<int>>> imageStack = [];
  int selectedImage = 0;
  bool show4Images = false;
  final int scrollOffset = 0;
  bool isCalculating = false;
  double progress = 0;

  // Sequence selection
  SequenceType sequenceType = SequenceType.spinEcho;

  // Sequence parameters
  final SequenceParams params = SequenceParams(
    tr: 500,
    te: 20,
    ti: 500,
    flipAngle: 90,
    etl: 3,
    esp: 15,
    fov: 256,
    matrix: 256,
    rect: 8,
    thickness: 6,
    nex: 1,
    coil: 0,
    freqOversample: false,
    phaseOversample: false,
  );

  // Windowing
  int windowCenter = 2048;
  int windowWidth = 4096;

  // Artifacts
  ArtifactType artifactType = ArtifactType.none;
  final MotionParams motionParams = MotionParams();
  final PeriodicMotionParams periodicMotionParams = PeriodicMotionParams();

  @override
  void onInit() {
    super.onInit();
    _loadExamData();
  }

  Future<void> _loadExamData() async {
    try {
      final data = await ExamData.loadFromAsset(
          'web_assets/brain_no_pathology.json');
      examData = data;
    } catch (e) {
      // Fall back to reference data
      examData = ExamData.createReferenceData();
    }
    update();
  }

  /// Run the MRI simulation and add the result to the image stack.
  Future<void> calculate() async {
    if (examData == null || isCalculating) return;

    isCalculating = true;
    progress = 0;
    update();

    // Update SNR
    params.snRatio = params.calculateSNRatio();

    // Run calculation in a microtask to keep UI responsive
    await Future.delayed(Duration.zero);

    final result = MriSimulator.calculate(
      t1Matrix: examData!.t1Matrix,
      t2Matrix: examData!.t2Matrix,
      pdMatrix: examData!.pdMatrix,
      sequence: sequenceType,
      params: params,
      artifactType: artifactType,
      motionParams: motionParams,
      periodicMotionParams: periodicMotionParams,
      onProgress: (p) {
        progress = p;
      },
    );

    imageStack.add(result);
    selectedImage = imageStack.length - 1;
    isCalculating = false;
    progress = 1.0;
    update();
  }

  /// Load reference exam data.
  void loadSampleData() {
    examData = ExamData.createReferenceData();
    imageStack.clear();
    selectedImage = 0;
    update();
  }

  /// Load brain exam data from asset.
  Future<void> loadBrainData() async {
    try {
      final data = await ExamData.loadFromAsset(
          'web_assets/brain_no_pathology.json');
      examData = data;
      imageStack.clear();
      selectedImage = 0;
      update();
    } catch (_) {}
  }

  /// Add an image (e.g. from K-Space manipulator) to the stack.
  void addImage(List<List<int>> image) {
    imageStack.add(image);
    selectedImage = imageStack.length - 1;
    update();
  }

  /// Optimize windowing to the current image's value range.
  void optimizeWindowing() {
    if (imageStack.isEmpty) return;
    final img = imageStack[selectedImage];
    int minVal = 4095;
    int maxVal = 0;
    for (int x = 0; x < 256; x++) {
      for (int y = 0; y < 256; y++) {
        final v = img[x][y];
        if (v < minVal) minVal = v;
        if (v > maxVal) maxVal = v;
      }
    }
    windowWidth = max(1, maxVal - minVal);
    windowCenter = (minVal + maxVal) ~/ 2;
    update();
  }

  /// Reset windowing to full range.
  void resetWindowing() {
    windowCenter = 2048;
    windowWidth = 4096;
    update();
  }
}
