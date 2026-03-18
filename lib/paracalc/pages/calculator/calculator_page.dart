import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../widgets/param_slider.dart';
import '../../widgets/parameter_image_display.dart';
import 'calculator_controller.dart';

/// Main screen of the Parameter Image Calculator.
class CalculatorPage extends GetView<CalculatorController> {
  const CalculatorPage({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CalculatorController>(
      builder: (c) => Scaffold(
        appBar: AppBar(
          title: const Text('Parameter Image Calculator 2.0'),
          actions: [
            IconButton(
              icon: const Icon(Icons.help_outline),
              tooltip: 'Info',
              onPressed: () => _showInfoDialog(context),
            ),
          ],
        ),
        body: LayoutBuilder(
          builder: (context, constraints) {
            final isWide = constraints.maxWidth > 900;
            if (isWide) {
              return Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SizedBox(width: 320, child: _buildControlPanel(context, c)),
                  const VerticalDivider(width: 1),
                  Expanded(child: _buildResultPanel(context, c)),
                ],
              );
            } else {
              return Column(
                children: [
                  SizedBox(
                      height: 360,
                      child: _buildControlPanel(context, c)),
                  const Divider(height: 1),
                  Expanded(child: _buildResultPanel(context, c)),
                ],
              );
            }
          },
        ),
      ),
    );
  }

  Widget _buildControlPanel(BuildContext context, CalculatorController c) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          // File loading
          _buildSection(context, 'DICOM Images', [
            ElevatedButton.icon(
              onPressed: c.calculating ? null : () => c.loadImages(),
              icon: const Icon(Icons.folder_open),
              label: const Text('Load DICOM files'),
            ),
            const SizedBox(height: 8),
            Text(
              c.images.isEmpty
                  ? 'No images loaded'
                  : '${c.images.length} image(s) loaded',
              style: Theme.of(context).textTheme.bodySmall,
            ),
            if (c.images.isNotEmpty) ...[
              const SizedBox(height: 4),
              ...c.images.map((img) => Padding(
                    padding: const EdgeInsets.only(left: 8),
                    child: Row(
                      children: [
                        Expanded(
                          child: Text(img.fileName,
                              style: Theme.of(context).textTheme.bodySmall,
                              overflow: TextOverflow.ellipsis),
                        ),
                        IconButton(
                          icon: const Icon(Icons.close, size: 16),
                          padding: EdgeInsets.zero,
                          constraints: const BoxConstraints(),
                          onPressed: () => c.removeImage(img),
                        ),
                      ],
                    ),
                  )),
              const SizedBox(height: 8),
              ElevatedButton.icon(
                onPressed: c.images.isEmpty ? null : () => c.clearImages(),
                icon: const Icon(Icons.delete_outline),
                label: const Text('Clear all'),
                style: ElevatedButton.styleFrom(
                  backgroundColor:
                      Theme.of(context).colorScheme.errorContainer,
                  foregroundColor:
                      Theme.of(context).colorScheme.onErrorContainer,
                ),
              ),
            ],
          ]),

          const SizedBox(height: 16),

          // Mode selection
          _buildSection(context, 'Calculation Mode', [
            SegmentedButton<String>(
              segments: const [
                ButtonSegment(value: 'T1', label: Text('T1')),
                ButtonSegment(value: 'T2', label: Text('T2 / PD')),
              ],
              selected: {c.mode},
              onSelectionChanged: (v) {
                c.mode = v.first;
                c.update();
              },
            ),
          ]),

          const SizedBox(height: 16),

          // Parameters
          if (c.mode == 'T1')
            _buildSection(context, 'T1 Parameters', [
              ParamSlider(
                label: 'TR',
                unit: 'ms',
                value: c.tr,
                min: 1,
                max: 500,
                onChanged: (v) {
                  c.tr = v;
                  c.update();
                },
              ),
              ParamSlider(
                label: 'Alpha 1',
                unit: '°',
                value: c.alpha1,
                min: 1,
                max: 90,
                onChanged: (v) {
                  c.alpha1 = v;
                  c.update();
                },
              ),
              ParamSlider(
                label: 'Alpha 2',
                unit: '°',
                value: c.alpha2,
                min: 1,
                max: 90,
                onChanged: (v) {
                  c.alpha2 = v;
                  c.update();
                },
              ),
            ])
          else
            _buildSection(context, 'T2 Parameters', [
              ParamSlider(
                label: 'TE first',
                unit: 'ms',
                value: c.teFirst,
                min: 1,
                max: 500,
                onChanged: (v) {
                  c.teFirst = v;
                  c.update();
                },
              ),
              ParamSlider(
                label: 'TE step',
                unit: 'ms',
                value: c.teStep,
                min: 1,
                max: 500,
                onChanged: (v) {
                  c.teStep = v;
                  c.update();
                },
              ),
            ]),

          const SizedBox(height: 8),

          _buildSection(context, 'Background', [
            ParamSlider(
              label: 'Background',
              value: c.backgroundSignal.toDouble(),
              min: 0,
              max: 100,
              decimals: 0,
              onChanged: (v) {
                c.backgroundSignal = v.round();
                c.update();
              },
            ),
            TextButton(
              onPressed: c.images.isEmpty ? null : () => c.autoBackground(),
              child: const Text('Auto-detect'),
            ),
          ]),

          const SizedBox(height: 16),

          // Filters
          _buildSection(context, 'Post-Processing Filters', [
            CheckboxListTile(
              title: const Text('Selective Median Filter'),
              dense: true,
              value: c.applyMedianFilter,
              onChanged: (v) {
                c.applyMedianFilter = v ?? false;
                c.update();
              },
            ),
            if (c.applyMedianFilter)
              Padding(
                padding: const EdgeInsets.only(left: 16),
                child: ParamSlider(
                  label: 'Factor',
                  value: c.medianFactor,
                  min: 1,
                  max: 10,
                  onChanged: (v) {
                    c.medianFactor = v;
                    c.update();
                  },
                ),
              ),
            CheckboxListTile(
              title: const Text('Selective Smooth Filter'),
              dense: true,
              value: c.applySmoothFilter,
              onChanged: (v) {
                c.applySmoothFilter = v ?? false;
                c.update();
              },
            ),
            if (c.applySmoothFilter)
              Padding(
                padding: const EdgeInsets.only(left: 16),
                child: ParamSlider(
                  label: 'Cut Off',
                  value: c.smoothCutOff,
                  min: 0,
                  max: 100,
                  onChanged: (v) {
                    c.smoothCutOff = v;
                    c.update();
                  },
                ),
              ),
          ]),

          const SizedBox(height: 16),

          // Calculate button
          ElevatedButton.icon(
            onPressed:
                c.canCalculate() && !c.calculating ? () => c.calculate() : null,
            icon: c.calculating
                ? const SizedBox(
                    width: 16,
                    height: 16,
                    child: CircularProgressIndicator(strokeWidth: 2))
                : const Icon(Icons.calculate),
            label: Text(c.calculating ? 'Calculating...' : 'Calculate'),
            style: ElevatedButton.styleFrom(
              padding: const EdgeInsets.symmetric(vertical: 14),
            ),
          ),

          const SizedBox(height: 8),

          // Status
          Text(c.statusMessage,
              style: Theme.of(context)
                  .textTheme
                  .bodySmall
                  ?.copyWith(color: Theme.of(context).colorScheme.outline)),

          // Windowing controls for results
          if (c.t1Result != null ||
              c.t2Result != null ||
              c.pdResult != null) ...[
            const SizedBox(height: 16),
            _buildWindowingControls(context, c),
          ],
        ],
      ),
    );
  }

  Widget _buildWindowingControls(
      BuildContext context, CalculatorController c) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        if (c.t1Result != null)
          _buildSection(context, 'T1 Window', [
            ParamSlider(
              label: 'Center',
              value: c.t1WinCenter,
              min: 0,
              max: 4000,
              decimals: 0,
              onChanged: (v) {
                c.t1WinCenter = v;
                c.update();
              },
            ),
            ParamSlider(
              label: 'Width',
              value: c.t1WinWidth,
              min: 1,
              max: 8000,
              decimals: 0,
              onChanged: (v) {
                c.t1WinWidth = v;
                c.update();
              },
            ),
          ]),
        if (c.t2Result != null)
          _buildSection(context, 'T2 Window', [
            ParamSlider(
              label: 'Center',
              value: c.t2WinCenter,
              min: 0,
              max: 4000,
              decimals: 0,
              onChanged: (v) {
                c.t2WinCenter = v;
                c.update();
              },
            ),
            ParamSlider(
              label: 'Width',
              value: c.t2WinWidth,
              min: 1,
              max: 8000,
              decimals: 0,
              onChanged: (v) {
                c.t2WinWidth = v;
                c.update();
              },
            ),
          ]),
        if (c.pdResult != null)
          _buildSection(context, 'PD Window', [
            ParamSlider(
              label: 'Center',
              value: c.pdWinCenter,
              min: 0,
              max: 1000,
              decimals: 0,
              onChanged: (v) {
                c.pdWinCenter = v;
                c.update();
              },
            ),
            ParamSlider(
              label: 'Width',
              value: c.pdWinWidth,
              min: 1,
              max: 2000,
              decimals: 0,
              onChanged: (v) {
                c.pdWinWidth = v;
                c.update();
              },
            ),
          ]),
      ],
    );
  }

  Widget _buildResultPanel(BuildContext context, CalculatorController c) {
    if (c.t1Result == null && c.t2Result == null && c.pdResult == null) {
      return Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.image_outlined,
                size: 64, color: Theme.of(context).colorScheme.outline),
            const SizedBox(height: 16),
            Text(
              'Load DICOM images and click Calculate\nto generate parameter images.',
              textAlign: TextAlign.center,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: Theme.of(context).colorScheme.outline,
                  ),
            ),
          ],
        ),
      );
    }

    final resultWidgets = <Widget>[];
    if (c.t1Result != null) {
      resultWidgets.add(ParameterImageDisplay(
        imageData: c.t1Result!,
        windowCenter: c.t1WinCenter,
        windowWidth: c.t1WinWidth,
        label: 'T1 [ms]',
      ));
    }
    if (c.t2Result != null) {
      resultWidgets.add(ParameterImageDisplay(
        imageData: c.t2Result!,
        windowCenter: c.t2WinCenter,
        windowWidth: c.t2WinWidth,
        label: 'T2 [ms]',
      ));
    }
    if (c.pdResult != null) {
      resultWidgets.add(ParameterImageDisplay(
        imageData: c.pdResult!,
        windowCenter: c.pdWinCenter,
        windowWidth: c.pdWinWidth,
        label: 'PD [‰]',
      ));
    }

    if (resultWidgets.length == 1) {
      return Padding(
        padding: const EdgeInsets.all(12),
        child: resultWidgets[0],
      );
    }

    return Padding(
      padding: const EdgeInsets.all(12),
      child: LayoutBuilder(
        builder: (context, constraints) {
          final crossCount = constraints.maxWidth > 600 ? 2 : 1;
          return GridView.count(
            crossAxisCount: crossCount,
            mainAxisSpacing: 12,
            crossAxisSpacing: 12,
            children: resultWidgets,
          );
        },
      ),
    );
  }

  Widget _buildSection(
      BuildContext context, String title, List<Widget> children) {
    return Card(
      margin: const EdgeInsets.only(bottom: 4),
      child: Padding(
        padding: const EdgeInsets.all(10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(title, style: Theme.of(context).textTheme.titleSmall),
            const SizedBox(height: 6),
            ...children,
          ],
        ),
      ),
    );
  }

  void _showInfoDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Parameter Image Calculator 2.0'),
        content: const SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              Text('Flutter port of the Parameter Image Calculator '
                  'originally developed as an ImageJ plugin.'),
              SizedBox(height: 12),
              Text('T1 Calculation:',
                  style: TextStyle(fontWeight: FontWeight.bold)),
              Text(
                  'Requires 2 FLASH images with different flip angles.\n'
                  'Formula: T1 = TR / ln((sin(α₁)cos(α₂) - q·sin(α₂)cos(α₁)) / (sin(α₁) - q·sin(α₂)))\n'
                  'where q = S₁/S₂'),
              SizedBox(height: 8),
              Text('T2/PD Calculation:',
                  style: TextStyle(fontWeight: FontWeight.bold)),
              Text(
                  'Requires ≥3 multi-echo Spin-Echo images.\n'
                  'Uses least squares fit on: ln(S) = ln(PD) - TE/T2'),
              SizedBox(height: 12),
              Text('© Thomas Hacklaender, IFTM GmbH\n'
                  'Flutter port 2024'),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }
}
