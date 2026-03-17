import 'dart:math';

import 'package:flutter/material.dart';

import '../models/exam_data.dart';
import '../simulation/mri_simulator.dart';
import '../widgets/kspace_manipulator.dart';
import '../widgets/mri_image_display.dart';
import '../widgets/param_slider.dart';

/// Main screen of the Virtual MRI Scanner app.
/// Mirrors the layout and functionality of the Java VMRTFrame.
class MriScreen extends StatefulWidget {
  const MriScreen({super.key});

  @override
  State<MriScreen> createState() => _MriScreenState();
}

class _MriScreenState extends State<MriScreen> {
  ExamData? _examData;
  final List<List<List<int>>> _imageStack = [];
  int _selectedImage = 0;
  bool _show4Images = false;
  final int _scrollOffset = 0;
  bool _isCalculating = false;
  double _progress = 0;

  // Sequence selection
  SequenceType _sequenceType = SequenceType.spinEcho;

  // Sequence parameters
  final SequenceParams _params = SequenceParams(
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
  int _windowCenter = 2048;
  int _windowWidth = 4096;

  // Artifacts
  ArtifactType _artifactType = ArtifactType.none;
  final MotionParams _motionParams = MotionParams();
  final PeriodicMotionParams _periodicMotionParams = PeriodicMotionParams();

  @override
  void initState() {
    super.initState();
    _loadExamData();
  }

  Future<void> _loadExamData() async {
    try {
      final data = await ExamData.loadFromAsset(
          'web_assets/brain_no_pathology.json');
      setState(() => _examData = data);
    } catch (e) {
      // Fall back to reference data
      setState(() => _examData = ExamData.createReferenceData());
    }
  }

  Future<void> _calculate() async {
    if (_examData == null || _isCalculating) return;

    setState(() {
      _isCalculating = true;
      _progress = 0;
    });

    // Update SNR
    _params.snRatio = _params.calculateSNRatio();

    // Run calculation in a microtask to keep UI responsive
    await Future.delayed(Duration.zero);

    final result = MriSimulator.calculate(
      t1Matrix: _examData!.t1Matrix,
      t2Matrix: _examData!.t2Matrix,
      pdMatrix: _examData!.pdMatrix,
      sequence: _sequenceType,
      params: _params,
      artifactType: _artifactType,
      motionParams: _motionParams,
      periodicMotionParams: _periodicMotionParams,
      onProgress: (p) {
        _progress = p;
      },
    );

    setState(() {
      _imageStack.add(result);
      _selectedImage = _imageStack.length - 1;
      _isCalculating = false;
      _progress = 1.0;
    });
  }

  void _loadSampleData() {
    setState(() {
      _examData = ExamData.createReferenceData();
      _imageStack.clear();
      _selectedImage = 0;
    });
  }

  Future<void> _loadBrainData() async {
    try {
      final data = await ExamData.loadFromAsset(
          'web_assets/brain_no_pathology.json');
      setState(() {
        _examData = data;
        _imageStack.clear();
        _selectedImage = 0;
      });
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Virtual MRI Scanner'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        actions: [
          // 1-image / 4-image toggle
          IconButton(
            icon: Icon(_show4Images ? Icons.grid_view : Icons.crop_square),
            tooltip: _show4Images ? 'Show 1 image' : 'Show 4 images',
            onPressed: () => setState(() => _show4Images = !_show4Images),
          ),
          // K-Space Manipulator
          IconButton(
            icon: const Icon(Icons.auto_graph),
            tooltip: 'K-Space Manipulator',
            onPressed: _imageStack.isNotEmpty
                ? () => _openKSpaceManipulator()
                : null,
          ),
          PopupMenuButton<String>(
            onSelected: (v) {
              if (v == 'brain') _loadBrainData();
              if (v == 'ref') _loadSampleData();
            },
            itemBuilder: (_) => [
              const PopupMenuItem(
                  value: 'brain', child: Text('Brain (no pathology)')),
              const PopupMenuItem(
                  value: 'ref', child: Text('Reference Data')),
            ],
            child: const Padding(
              padding: EdgeInsets.symmetric(horizontal: 8),
              child: Row(
                children: [
                  Icon(Icons.folder_open),
                  SizedBox(width: 4),
                  Text('Load'),
                ],
              ),
            ),
          ),
        ],
      ),
      body: LayoutBuilder(
        builder: (context, constraints) {
          if (constraints.maxWidth > 700) {
            return _buildWideLayout();
          }
          return _buildNarrowLayout();
        },
      ),
    );
  }

  Widget _buildWideLayout() {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Image area
        Expanded(
          flex: 3,
          child: _buildImageArea(),
        ),
        // Controls
        SizedBox(
          width: 320,
          child: _buildControlPanel(),
        ),
      ],
    );
  }

  Widget _buildNarrowLayout() {
    return SingleChildScrollView(
      child: Column(
        children: [
          SizedBox(
            height: 300,
            child: _buildImageArea(),
          ),
          _buildControlPanel(),
        ],
      ),
    );
  }

  void _openKSpaceManipulator() {
    if (_imageStack.isEmpty) return;
    showDialog(
      context: context,
      builder: (_) => KSpaceManipulator(
        sourceImage: _imageStack[_selectedImage],
        windowCenter: _windowCenter,
        windowWidth: _windowWidth,
        onTransferToViewer: (image) {
          setState(() {
            _imageStack.add(image);
            _selectedImage = _imageStack.length - 1;
          });
        },
      ),
    );
  }

  Widget _buildImageArea() {
    return Container(
      color: Colors.black,
      child: Column(
        children: [
          Expanded(
            child: _show4Images ? _build4ImageGrid() : _build1ImageView(),
          ),
          if (_isCalculating)
            LinearProgressIndicator(value: _progress)
          else
            _buildImageStatusBar(),
        ],
      ),
    );
  }

  Widget _build1ImageView() {
    return Center(
      child: AspectRatio(
        aspectRatio: 1,
        child: MriImageDisplay(
          imageData: _imageStack.isNotEmpty ? _imageStack[_selectedImage] : null,
          windowCenter: _windowCenter,
          windowWidth: _windowWidth,
        ),
      ),
    );
  }

  Widget _build4ImageGrid() {
    return Center(
      child: AspectRatio(
        aspectRatio: 1,
        child: Column(
          children: [
            Expanded(
              child: Row(
                children: [
                  Expanded(child: _buildGridCell(0)),
                  const VerticalDivider(width: 1, color: Colors.white),
                  Expanded(child: _buildGridCell(1)),
                ],
              ),
            ),
            const Divider(height: 1, color: Colors.white),
            Expanded(
              child: Row(
                children: [
                  Expanded(child: _buildGridCell(2)),
                  const VerticalDivider(width: 1, color: Colors.white),
                  Expanded(child: _buildGridCell(3)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildGridCell(int slot) {
    final idx = _scrollOffset * 4 + slot;
    final hasImage = idx < _imageStack.length;
    final isSelected = hasImage && idx == _selectedImage;
    return GestureDetector(
      onTap: hasImage ? () => setState(() => _selectedImage = idx) : null,
      child: Container(
        decoration: isSelected
            ? BoxDecoration(border: Border.all(color: Colors.red, width: 2))
            : null,
        child: MriImageDisplay(
          imageData: hasImage ? _imageStack[idx] : null,
          windowCenter: _windowCenter,
          windowWidth: _windowWidth,
        ),
      ),
    );
  }

  Widget _buildImageStatusBar() {
    return Container(
      color: Colors.grey[900],
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      child: Row(
        children: [
          if (_examData != null)
            Text(_examData!.title,
                style: const TextStyle(color: Colors.white70, fontSize: 11)),
          const Spacer(),
          if (_imageStack.isNotEmpty) ...[
            IconButton(
              icon: const Icon(Icons.chevron_left, color: Colors.white70, size: 16),
              padding: EdgeInsets.zero,
              constraints: const BoxConstraints(minWidth: 24, minHeight: 24),
              onPressed: _selectedImage > 0
                  ? () => setState(() => _selectedImage--)
                  : null,
            ),
            Text('${_selectedImage + 1}/${_imageStack.length}',
                style: const TextStyle(color: Colors.white70, fontSize: 11)),
            IconButton(
              icon: const Icon(Icons.chevron_right, color: Colors.white70, size: 16),
              padding: EdgeInsets.zero,
              constraints: const BoxConstraints(minWidth: 24, minHeight: 24),
              onPressed: _selectedImage < _imageStack.length - 1
                  ? () => setState(() => _selectedImage++)
                  : null,
            ),
            const SizedBox(width: 8),
          ],
          Text('C: $_windowCenter  W: $_windowWidth',
              style: const TextStyle(color: Colors.white70, fontSize: 11)),
        ],
      ),
    );
  }

  Widget _buildControlPanel() {
    return Container(
      color: Colors.grey[100],
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _buildSequenceSelector(),
            const Divider(),
            _buildSequenceParams(),
            const Divider(),
            _buildCommonParams(),
            const Divider(),
            _buildWindowingControls(),
            const Divider(),
            _buildArtifactControls(),
            const Divider(),
            _buildCalculateButton(),
          ],
        ),
      ),
    );
  }

  Widget _buildSequenceSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Sequence',
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
        const SizedBox(height: 4),
        DropdownButton<SequenceType>(
          isExpanded: true,
          value: _sequenceType,
          items: const [
            DropdownMenuItem(
                value: SequenceType.saturationRecovery,
                child: Text('Saturation Recovery (SR)')),
            DropdownMenuItem(
                value: SequenceType.inversionRecovery,
                child: Text('Inversion Recovery (IR)')),
            DropdownMenuItem(
                value: SequenceType.spinEcho,
                child: Text('Spin Echo (SE)')),
            DropdownMenuItem(
                value: SequenceType.turboSpinEcho,
                child: Text('Turbo Spin Echo (TSE)')),
            DropdownMenuItem(
                value: SequenceType.flash,
                child: Text('FLASH')),
            DropdownMenuItem(
                value: SequenceType.geSpoiled,
                child: Text('GE Spoiled')),
            DropdownMenuItem(
                value: SequenceType.geRefocused,
                child: Text('GE Refocused (FISP)')),
          ],
          onChanged: (v) => setState(() => _sequenceType = v!),
        ),
      ],
    );
  }

  Widget _buildSequenceParams() {
    final widgets = <Widget>[
      const Text('Sequence Parameters',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
    ];

    // TR is common to all
    widgets.add(ParamSlider(
      label: 'TR',
      unit: 'ms',
      min: _sequenceType == SequenceType.flash ||
              _sequenceType == SequenceType.geSpoiled ||
              _sequenceType == SequenceType.geRefocused
          ? 5
          : 100,
      max: 6100,
      value: _params.tr,
      onChanged: (v) => setState(() => _params.tr = v),
    ));

    // TE for spin echo and gradient echo sequences
    if (_sequenceType == SequenceType.spinEcho ||
        _sequenceType == SequenceType.flash ||
        _sequenceType == SequenceType.geSpoiled ||
        _sequenceType == SequenceType.geRefocused) {
      widgets.add(ParamSlider(
        label: 'TE',
        unit: 'ms',
        min: _sequenceType == SequenceType.spinEcho ? 10 : 2,
        max: _sequenceType == SequenceType.spinEcho ? 1010 : 1002,
        value: _params.te,
        onChanged: (v) => setState(() => _params.te = v),
      ));
    }

    // TI for inversion recovery
    if (_sequenceType == SequenceType.inversionRecovery) {
      widgets.add(ParamSlider(
        label: 'TI',
        unit: 'ms',
        min: 10,
        max: 6010,
        value: _params.ti,
        onChanged: (v) => setState(() => _params.ti = v),
      ));
    }

    // TSE parameters
    if (_sequenceType == SequenceType.turboSpinEcho) {
      widgets.add(ParamSlider(
        label: 'TE eff',
        unit: 'ms',
        min: 10,
        max: 1010,
        value: _params.te,
        onChanged: (v) => setState(() => _params.te = v),
      ));
      widgets.add(ParamSlider(
        label: 'ESP',
        unit: 'ms',
        min: 2,
        max: 102,
        value: _params.esp,
        onChanged: (v) => setState(() => _params.esp = v),
      ));
      widgets.add(ParamSlider(
        label: 'ETL',
        unit: '',
        min: 1,
        max: 32,
        value: _params.etl,
        onChanged: (v) => setState(() => _params.etl = v),
      ));
    }

    // Flip angle for gradient echo sequences
    if (_sequenceType == SequenceType.flash ||
        _sequenceType == SequenceType.geSpoiled ||
        _sequenceType == SequenceType.geRefocused) {
      widgets.add(ParamSlider(
        label: 'Flip',
        unit: '°',
        min: 1,
        max: 180,
        value: _params.flipAngle,
        onChanged: (v) => setState(() => _params.flipAngle = v),
      ));
    }

    return Column(
        crossAxisAlignment: CrossAxisAlignment.start, children: widgets);
  }

  Widget _buildCommonParams() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Common Parameters',
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
        ParamSlider(
          label: 'FOV',
          unit: 'mm',
          min: 32,
          max: 256,
          value: _params.fov,
          onChanged: (v) => setState(() => _params.fov = v),
        ),
        ParamSlider(
          label: 'Rect',
          unit: '/8',
          min: 4,
          max: 8,
          value: _params.rect,
          onChanged: (v) => setState(() => _params.rect = v),
        ),
        ParamSlider(
          label: 'Thick',
          unit: 'mm',
          min: 1,
          max: 20,
          value: _params.thickness,
          onChanged: (v) => setState(() => _params.thickness = v),
        ),
        ParamSlider(
          label: 'NEX',
          unit: '',
          min: 1,
          max: 16,
          value: _params.nex,
          onChanged: (v) => setState(() => _params.nex = v),
        ),
        Row(
          children: [
            const SizedBox(
              width: 56,
              child: Text('Coil', style: TextStyle(fontSize: 11)),
            ),
            Expanded(
              child: DropdownButton<int>(
                isExpanded: true,
                value: _params.coil,
                items: const [
                  DropdownMenuItem(value: 0, child: Text('Surface (Organ)')),
                  DropdownMenuItem(value: 1, child: Text('Body')),
                ],
                onChanged: (v) => setState(() => _params.coil = v!),
              ),
            ),
          ],
        ),
        Row(
          children: [
            Expanded(
              child: CheckboxListTile(
                title: const Text('Freq OS', style: TextStyle(fontSize: 11)),
                value: _params.freqOversample,
                dense: true,
                controlAffinity: ListTileControlAffinity.leading,
                onChanged: (v) =>
                    setState(() => _params.freqOversample = v!),
              ),
            ),
            Expanded(
              child: CheckboxListTile(
                title: const Text('Phase OS', style: TextStyle(fontSize: 11)),
                value: _params.phaseOversample,
                dense: true,
                controlAffinity: ListTileControlAffinity.leading,
                onChanged: (v) =>
                    setState(() => _params.phaseOversample = v!),
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildWindowingControls() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Text('Window',
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
            const Spacer(),
            IconButton(
              icon: const Icon(Icons.restart_alt, size: 18),
              tooltip: 'Reset',
              onPressed: () => setState(() {
                _windowCenter = 2048;
                _windowWidth = 4096;
              }),
            ),
            IconButton(
              icon: const Icon(Icons.auto_fix_high, size: 18),
              tooltip: 'Optimal',
              onPressed: _optimizeWindowing,
            ),
          ],
        ),
        ParamSlider(
          label: 'Center',
          unit: '',
          min: 0,
          max: 4095,
          value: _windowCenter,
          onChanged: (v) => setState(() => _windowCenter = v),
        ),
        ParamSlider(
          label: 'Width',
          unit: '',
          min: 1,
          max: 4096,
          value: _windowWidth,
          onChanged: (v) => setState(() => _windowWidth = v),
        ),
      ],
    );
  }

  void _optimizeWindowing() {
    if (_imageStack.isEmpty) return;
    final img = _imageStack[_selectedImage];
    int minVal = 4095;
    int maxVal = 0;
    for (int x = 0; x < 256; x++) {
      for (int y = 0; y < 256; y++) {
        final v = img[x][y];
        if (v < minVal) minVal = v;
        if (v > maxVal) maxVal = v;
      }
    }
    setState(() {
      _windowWidth = max(1, maxVal - minVal);
      _windowCenter = (minVal + maxVal) ~/ 2;
    });
  }

  Widget _buildArtifactControls() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Artifacts',
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
        const SizedBox(height: 4),
        DropdownButton<ArtifactType>(
          isExpanded: true,
          value: _artifactType,
          items: const [
            DropdownMenuItem(
                value: ArtifactType.none, child: Text('No Artifact')),
            DropdownMenuItem(
                value: ArtifactType.motion, child: Text('Motion (Translation)')),
            DropdownMenuItem(
                value: ArtifactType.periodicMotion,
                child: Text('Periodic Motion')),
          ],
          onChanged: (v) => setState(() => _artifactType = v!),
        ),
        if (_artifactType == ArtifactType.motion) ...[
          ParamSlider(
            label: 'X Spd',
            unit: 'mm/min',
            min: 0,
            max: 100,
            value: _motionParams.xSpeed,
            onChanged: (v) => setState(() => _motionParams.xSpeed = v),
          ),
          ParamSlider(
            label: 'Y Spd',
            unit: 'mm/min',
            min: 0,
            max: 100,
            value: _motionParams.ySpeed,
            onChanged: (v) => setState(() => _motionParams.ySpeed = v),
          ),
          ParamSlider(
            label: 'Start',
            unit: 's',
            min: 0,
            max: 99999,
            value: _motionParams.startTime,
            onChanged: (v) => setState(() => _motionParams.startTime = v),
          ),
          ParamSlider(
            label: 'Stop',
            unit: 's',
            min: 0,
            max: 99999,
            value: _motionParams.stopTime,
            onChanged: (v) => setState(() => _motionParams.stopTime = v),
          ),
        ],
        if (_artifactType == ArtifactType.periodicMotion) ...[
          ParamSlider(
            label: 'X Amp',
            unit: 'px',
            min: 0,
            max: 50,
            value: _periodicMotionParams.xAmplitude,
            onChanged: (v) =>
                setState(() => _periodicMotionParams.xAmplitude = v),
          ),
          ParamSlider(
            label: 'Y Amp',
            unit: 'px',
            min: 0,
            max: 50,
            value: _periodicMotionParams.yAmplitude,
            onChanged: (v) =>
                setState(() => _periodicMotionParams.yAmplitude = v),
          ),
          ParamSlider(
            label: 'Freq',
            unit: '/min',
            min: 1,
            max: 60,
            value: _periodicMotionParams.frequency,
            onChanged: (v) =>
                setState(() => _periodicMotionParams.frequency = v),
          ),
          ParamSlider(
            label: 'Start',
            unit: 's',
            min: 0,
            max: 99999,
            value: _periodicMotionParams.startTime,
            onChanged: (v) =>
                setState(() => _periodicMotionParams.startTime = v),
          ),
          ParamSlider(
            label: 'Stop',
            unit: 's',
            min: 0,
            max: 99999,
            value: _periodicMotionParams.stopTime,
            onChanged: (v) =>
                setState(() => _periodicMotionParams.stopTime = v),
          ),
        ],
      ],
    );
  }

  Widget _buildCalculateButton() {
    /// Calculate total scan time matching Java logic
    String totalTime() {
      int totalMs;
      if (_sequenceType == SequenceType.turboSpinEcho) {
        totalMs = 256 * _params.tr * (_params.rect ~/ 8) ~/ _params.etl;
      } else {
        totalMs = 256 * _params.tr * _params.rect ~/ 8;
      }
      totalMs *= _params.nex;
      final minutes = totalMs ~/ 60000;
      final seconds = (totalMs % 60000) ~/ 1000;
      return '${minutes}m ${seconds}s';
    }

    /// Calculate pixel size
    String pixelSize() {
      final ps = _params.fov / _params.matrix;
      return '${ps.toStringAsFixed(1)} mm';
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Row(
          children: [
            Text('Scan time: ${totalTime()}',
                style: const TextStyle(fontSize: 11)),
            const Spacer(),
            Text('Pixel: ${pixelSize()}',
                style: const TextStyle(fontSize: 11)),
          ],
        ),
        const SizedBox(height: 8),
        ElevatedButton.icon(
          onPressed: _isCalculating ? null : _calculate,
          icon: _isCalculating
              ? const SizedBox(
                  width: 16,
                  height: 16,
                  child: CircularProgressIndicator(strokeWidth: 2))
              : const Icon(Icons.play_arrow),
          label: Text(_isCalculating ? 'Calculating...' : 'Calculate'),
          style: ElevatedButton.styleFrom(
            padding: const EdgeInsets.symmetric(vertical: 12),
          ),
        ),
      ],
    );
  }
}
