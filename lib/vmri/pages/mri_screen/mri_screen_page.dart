import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../simulation/mri_simulator.dart';
import '../../widgets/kspace_manipulator.dart';
import '../../widgets/mri_image_display.dart';
import '../../widgets/param_slider.dart';
import 'mri_screen_controller.dart';

/// Main screen of the Virtual MRI Scanner app.
class MriScreenPage extends GetView<MriScreenController> {
  const MriScreenPage({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<MriScreenController>(
      builder: (c) => Scaffold(
        appBar: AppBar(
          title: const Text('Virtual MRI Scanner'),
          backgroundColor: Theme.of(context).colorScheme.inversePrimary,
          actions: [
            // 1-image / 4-image toggle
            IconButton(
              icon: Icon(
                  c.show4Images ? Icons.grid_view : Icons.crop_square),
              tooltip: c.show4Images ? 'Show 1 image' : 'Show 4 images',
              onPressed: () {
                c.show4Images = !c.show4Images;
                c.update();
              },
            ),
            // K-Space Manipulator
            IconButton(
              icon: const Icon(Icons.auto_graph),
              tooltip: 'K-Space Manipulator',
              onPressed:
                  c.imageStack.isNotEmpty ? () => _openKSpaceManipulator(context) : null,
            ),
            PopupMenuButton<String>(
              onSelected: (v) {
                if (v == 'brain') c.loadBrainData();
                if (v == 'ref') c.loadSampleData();
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
              return _buildWideLayout(c);
            }
            return _buildNarrowLayout(c);
          },
        ),
      ),
    );
  }

  Widget _buildWideLayout(MriScreenController c) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Image area
        Expanded(
          flex: 3,
          child: _buildImageArea(c),
        ),
        // Controls
        SizedBox(
          width: 320,
          child: _buildControlPanel(c),
        ),
      ],
    );
  }

  Widget _buildNarrowLayout(MriScreenController c) {
    return SingleChildScrollView(
      child: Column(
        children: [
          SizedBox(
            height: 300,
            child: _buildImageArea(c),
          ),
          _buildControlPanel(c),
        ],
      ),
    );
  }

  void _openKSpaceManipulator(BuildContext context) {
    if (controller.imageStack.isEmpty) return;
    showDialog(
      context: context,
      builder: (_) => KSpaceManipulator(
        sourceImage: controller.imageStack[controller.selectedImage],
        windowCenter: controller.windowCenter,
        windowWidth: controller.windowWidth,
        onTransferToViewer: (image) {
          controller.addImage(image);
        },
      ),
    );
  }

  Widget _buildImageArea(MriScreenController c) {
    return Container(
      color: Colors.black,
      child: Column(
        children: [
          Expanded(
            child:
                c.show4Images ? _build4ImageGrid(c) : _build1ImageView(c),
          ),
          if (c.isCalculating)
            LinearProgressIndicator(value: c.progress)
          else
            _buildImageStatusBar(c),
        ],
      ),
    );
  }

  Widget _build1ImageView(MriScreenController c) {
    return Center(
      child: AspectRatio(
        aspectRatio: 1,
        child: MriImageDisplay(
          imageData:
              c.imageStack.isNotEmpty ? c.imageStack[c.selectedImage] : null,
          windowCenter: c.windowCenter,
          windowWidth: c.windowWidth,
        ),
      ),
    );
  }

  Widget _build4ImageGrid(MriScreenController c) {
    return Center(
      child: AspectRatio(
        aspectRatio: 1,
        child: Column(
          children: [
            Expanded(
              child: Row(
                children: [
                  Expanded(child: _buildGridCell(c, 0)),
                  const VerticalDivider(width: 1, color: Colors.white),
                  Expanded(child: _buildGridCell(c, 1)),
                ],
              ),
            ),
            const Divider(height: 1, color: Colors.white),
            Expanded(
              child: Row(
                children: [
                  Expanded(child: _buildGridCell(c, 2)),
                  const VerticalDivider(width: 1, color: Colors.white),
                  Expanded(child: _buildGridCell(c, 3)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildGridCell(MriScreenController c, int slot) {
    final idx = c.scrollOffset * 4 + slot;
    final hasImage = idx < c.imageStack.length;
    final isSelected = hasImage && idx == c.selectedImage;
    return GestureDetector(
      onTap: hasImage
          ? () {
              c.selectedImage = idx;
              c.update();
            }
          : null,
      child: Container(
        decoration: isSelected
            ? BoxDecoration(border: Border.all(color: Colors.red, width: 2))
            : null,
        child: MriImageDisplay(
          imageData: hasImage ? c.imageStack[idx] : null,
          windowCenter: c.windowCenter,
          windowWidth: c.windowWidth,
        ),
      ),
    );
  }

  Widget _buildImageStatusBar(MriScreenController c) {
    return Container(
      color: Colors.grey[900],
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      child: Row(
        children: [
          if (c.examData != null)
            Text(c.examData!.title,
                style:
                    const TextStyle(color: Colors.white70, fontSize: 11)),
          const Spacer(),
          if (c.imageStack.isNotEmpty) ...[
            IconButton(
              icon: const Icon(Icons.chevron_left,
                  color: Colors.white70, size: 16),
              padding: EdgeInsets.zero,
              constraints:
                  const BoxConstraints(minWidth: 24, minHeight: 24),
              onPressed: c.selectedImage > 0
                  ? () {
                      c.selectedImage--;
                      c.update();
                    }
                  : null,
            ),
            Text('${c.selectedImage + 1}/${c.imageStack.length}',
                style:
                    const TextStyle(color: Colors.white70, fontSize: 11)),
            IconButton(
              icon: const Icon(Icons.chevron_right,
                  color: Colors.white70, size: 16),
              padding: EdgeInsets.zero,
              constraints:
                  const BoxConstraints(minWidth: 24, minHeight: 24),
              onPressed: c.selectedImage < c.imageStack.length - 1
                  ? () {
                      c.selectedImage++;
                      c.update();
                    }
                  : null,
            ),
            const SizedBox(width: 8),
          ],
          Text('C: ${c.windowCenter}  W: ${c.windowWidth}',
              style:
                  const TextStyle(color: Colors.white70, fontSize: 11)),
        ],
      ),
    );
  }

  Widget _buildControlPanel(MriScreenController c) {
    return Container(
      color: Colors.grey[100],
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _buildSequenceSelector(c),
            const Divider(),
            _buildSequenceParams(c),
            const Divider(),
            _buildCommonParams(c),
            const Divider(),
            _buildWindowingControls(c),
            const Divider(),
            _buildArtifactControls(c),
            const Divider(),
            _buildCalculateButton(c),
          ],
        ),
      ),
    );
  }

  Widget _buildSequenceSelector(MriScreenController c) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Sequence',
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
        const SizedBox(height: 4),
        DropdownButton<SequenceType>(
          isExpanded: true,
          value: c.sequenceType,
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
          onChanged: (v) {
            c.sequenceType = v!;
            c.update();
          },
        ),
      ],
    );
  }

  Widget _buildSequenceParams(MriScreenController c) {
    final widgets = <Widget>[
      const Text('Sequence Parameters',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
    ];

    // TR is common to all
    widgets.add(ParamSlider(
      label: 'TR',
      unit: 'ms',
      min: c.sequenceType == SequenceType.flash ||
              c.sequenceType == SequenceType.geSpoiled ||
              c.sequenceType == SequenceType.geRefocused
          ? 5
          : 100,
      max: 6100,
      value: c.params.tr,
      onChanged: (v) {
        c.params.tr = v;
        c.update();
      },
    ));

    // TE for spin echo and gradient echo sequences
    if (c.sequenceType == SequenceType.spinEcho ||
        c.sequenceType == SequenceType.flash ||
        c.sequenceType == SequenceType.geSpoiled ||
        c.sequenceType == SequenceType.geRefocused) {
      widgets.add(ParamSlider(
        label: 'TE',
        unit: 'ms',
        min: c.sequenceType == SequenceType.spinEcho ? 10 : 2,
        max: c.sequenceType == SequenceType.spinEcho ? 1010 : 1002,
        value: c.params.te,
        onChanged: (v) {
          c.params.te = v;
          c.update();
        },
      ));
    }

    // TI for inversion recovery
    if (c.sequenceType == SequenceType.inversionRecovery) {
      widgets.add(ParamSlider(
        label: 'TI',
        unit: 'ms',
        min: 10,
        max: 6010,
        value: c.params.ti,
        onChanged: (v) {
          c.params.ti = v;
          c.update();
        },
      ));
    }

    // TSE parameters
    if (c.sequenceType == SequenceType.turboSpinEcho) {
      widgets.add(ParamSlider(
        label: 'TE eff',
        unit: 'ms',
        min: 10,
        max: 1010,
        value: c.params.te,
        onChanged: (v) {
          c.params.te = v;
          c.update();
        },
      ));
      widgets.add(ParamSlider(
        label: 'ESP',
        unit: 'ms',
        min: 2,
        max: 102,
        value: c.params.esp,
        onChanged: (v) {
          c.params.esp = v;
          c.update();
        },
      ));
      widgets.add(ParamSlider(
        label: 'ETL',
        unit: '',
        min: 1,
        max: 32,
        value: c.params.etl,
        onChanged: (v) {
          c.params.etl = v;
          c.update();
        },
      ));
    }

    // Flip angle for gradient echo sequences
    if (c.sequenceType == SequenceType.flash ||
        c.sequenceType == SequenceType.geSpoiled ||
        c.sequenceType == SequenceType.geRefocused) {
      widgets.add(ParamSlider(
        label: 'Flip',
        unit: '°',
        min: 1,
        max: 180,
        value: c.params.flipAngle,
        onChanged: (v) {
          c.params.flipAngle = v;
          c.update();
        },
      ));
    }

    return Column(
        crossAxisAlignment: CrossAxisAlignment.start, children: widgets);
  }

  Widget _buildCommonParams(MriScreenController c) {
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
          value: c.params.fov,
          onChanged: (v) {
            c.params.fov = v;
            c.update();
          },
        ),
        ParamSlider(
          label: 'Rect',
          unit: '/8',
          min: 4,
          max: 8,
          value: c.params.rect,
          onChanged: (v) {
            c.params.rect = v;
            c.update();
          },
        ),
        ParamSlider(
          label: 'Thick',
          unit: 'mm',
          min: 1,
          max: 20,
          value: c.params.thickness,
          onChanged: (v) {
            c.params.thickness = v;
            c.update();
          },
        ),
        ParamSlider(
          label: 'NEX',
          unit: '',
          min: 1,
          max: 16,
          value: c.params.nex,
          onChanged: (v) {
            c.params.nex = v;
            c.update();
          },
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
                value: c.params.coil,
                items: const [
                  DropdownMenuItem(
                      value: 0, child: Text('Surface (Organ)')),
                  DropdownMenuItem(value: 1, child: Text('Body')),
                ],
                onChanged: (v) {
                  c.params.coil = v!;
                  c.update();
                },
              ),
            ),
          ],
        ),
        Row(
          children: [
            Expanded(
              child: CheckboxListTile(
                title:
                    const Text('Freq OS', style: TextStyle(fontSize: 11)),
                value: c.params.freqOversample,
                dense: true,
                controlAffinity: ListTileControlAffinity.leading,
                onChanged: (v) {
                  c.params.freqOversample = v!;
                  c.update();
                },
              ),
            ),
            Expanded(
              child: CheckboxListTile(
                title: const Text('Phase OS',
                    style: TextStyle(fontSize: 11)),
                value: c.params.phaseOversample,
                dense: true,
                controlAffinity: ListTileControlAffinity.leading,
                onChanged: (v) {
                  c.params.phaseOversample = v!;
                  c.update();
                },
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildWindowingControls(MriScreenController c) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Text('Window',
                style:
                    TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
            const Spacer(),
            IconButton(
              icon: const Icon(Icons.restart_alt, size: 18),
              tooltip: 'Reset',
              onPressed: () => c.resetWindowing(),
            ),
            IconButton(
              icon: const Icon(Icons.auto_fix_high, size: 18),
              tooltip: 'Optimal',
              onPressed: () => c.optimizeWindowing(),
            ),
          ],
        ),
        ParamSlider(
          label: 'Center',
          unit: '',
          min: 0,
          max: 4095,
          value: c.windowCenter,
          onChanged: (v) {
            c.windowCenter = v;
            c.update();
          },
        ),
        ParamSlider(
          label: 'Width',
          unit: '',
          min: 1,
          max: 4096,
          value: c.windowWidth,
          onChanged: (v) {
            c.windowWidth = v;
            c.update();
          },
        ),
      ],
    );
  }

  Widget _buildArtifactControls(MriScreenController c) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Artifacts',
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
        const SizedBox(height: 4),
        DropdownButton<ArtifactType>(
          isExpanded: true,
          value: c.artifactType,
          items: const [
            DropdownMenuItem(
                value: ArtifactType.none, child: Text('No Artifact')),
            DropdownMenuItem(
                value: ArtifactType.motion,
                child: Text('Motion (Translation)')),
            DropdownMenuItem(
                value: ArtifactType.periodicMotion,
                child: Text('Periodic Motion')),
          ],
          onChanged: (v) {
            c.artifactType = v!;
            c.update();
          },
        ),
        if (c.artifactType == ArtifactType.motion) ...[
          ParamSlider(
            label: 'X Spd',
            unit: 'mm/min',
            min: 0,
            max: 100,
            value: c.motionParams.xSpeed,
            onChanged: (v) {
              c.motionParams.xSpeed = v;
              c.update();
            },
          ),
          ParamSlider(
            label: 'Y Spd',
            unit: 'mm/min',
            min: 0,
            max: 100,
            value: c.motionParams.ySpeed,
            onChanged: (v) {
              c.motionParams.ySpeed = v;
              c.update();
            },
          ),
          ParamSlider(
            label: 'Start',
            unit: 's',
            min: 0,
            max: 99999,
            value: c.motionParams.startTime,
            onChanged: (v) {
              c.motionParams.startTime = v;
              c.update();
            },
          ),
          ParamSlider(
            label: 'Stop',
            unit: 's',
            min: 0,
            max: 99999,
            value: c.motionParams.stopTime,
            onChanged: (v) {
              c.motionParams.stopTime = v;
              c.update();
            },
          ),
        ],
        if (c.artifactType == ArtifactType.periodicMotion) ...[
          ParamSlider(
            label: 'X Amp',
            unit: 'px',
            min: 0,
            max: 50,
            value: c.periodicMotionParams.xAmplitude,
            onChanged: (v) {
              c.periodicMotionParams.xAmplitude = v;
              c.update();
            },
          ),
          ParamSlider(
            label: 'Y Amp',
            unit: 'px',
            min: 0,
            max: 50,
            value: c.periodicMotionParams.yAmplitude,
            onChanged: (v) {
              c.periodicMotionParams.yAmplitude = v;
              c.update();
            },
          ),
          ParamSlider(
            label: 'Freq',
            unit: '/min',
            min: 1,
            max: 60,
            value: c.periodicMotionParams.frequency,
            onChanged: (v) {
              c.periodicMotionParams.frequency = v;
              c.update();
            },
          ),
          ParamSlider(
            label: 'Start',
            unit: 's',
            min: 0,
            max: 99999,
            value: c.periodicMotionParams.startTime,
            onChanged: (v) {
              c.periodicMotionParams.startTime = v;
              c.update();
            },
          ),
          ParamSlider(
            label: 'Stop',
            unit: 's',
            min: 0,
            max: 99999,
            value: c.periodicMotionParams.stopTime,
            onChanged: (v) {
              c.periodicMotionParams.stopTime = v;
              c.update();
            },
          ),
        ],
      ],
    );
  }

  Widget _buildCalculateButton(MriScreenController c) {
    /// Calculate total scan time matching Java logic
    String totalTime() {
      int totalMs;
      if (c.sequenceType == SequenceType.turboSpinEcho) {
        totalMs = 256 * c.params.tr * (c.params.rect ~/ 8) ~/ c.params.etl;
      } else {
        totalMs = 256 * c.params.tr * c.params.rect ~/ 8;
      }
      totalMs *= c.params.nex;
      final minutes = totalMs ~/ 60000;
      final seconds = (totalMs % 60000) ~/ 1000;
      return '${minutes}m ${seconds}s';
    }

    /// Calculate pixel size
    String pixelSize() {
      final ps = c.params.fov / c.params.matrix;
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
          onPressed: c.isCalculating ? null : () => c.calculate(),
          icon: c.isCalculating
              ? const SizedBox(
                  width: 16,
                  height: 16,
                  child: CircularProgressIndicator(strokeWidth: 2))
              : const Icon(Icons.play_arrow),
          label: Text(c.isCalculating ? 'Calculating...' : 'Calculate'),
          style: ElevatedButton.styleFrom(
            padding: const EdgeInsets.symmetric(vertical: 12),
          ),
        ),
      ],
    );
  }
}
