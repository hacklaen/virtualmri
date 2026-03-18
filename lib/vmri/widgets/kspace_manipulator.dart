import 'dart:math';
import 'dart:typed_data';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../simulation/fft.dart';

/// Controller for the K-Space Manipulator dialog.
class _KSpaceController extends GetxController {
  final List<List<int>> sourceImage;
  final int windowCenter;
  final int windowWidth;

  // K-space data (original)
  late ComplexImage kSpace;
  late List<List<double>> kSpaceMag; // magnitude for display

  // Manipulation parameters
  bool deleteMargins = false;
  int marginLeft = 0, marginRight = 0, marginTop = 0, marginBottom = 0;

  bool deleteRect = false;
  int rectWidth = 0, rectHeight = 0;

  bool deleteLines = false;
  int everyNRow = 2, everyNCol = 2;

  // Result
  List<List<int>>? resultImage;
  int resultCenter = 2048;
  int resultWidth = 4096;

  _KSpaceController({
    required this.sourceImage,
    required this.windowCenter,
    required this.windowWidth,
  });

  @override
  void onInit() {
    super.onInit();
    _computeKSpace();
  }

  void _computeKSpace() {
    const size = 256;
    // Convert source image (int) to double for FFT
    final input = List.generate(
        size, (x) => List.generate(size, (y) => sourceImage[x][y].toDouble()));

    kSpace = forwardFFT2D(input, size);

    // Compute magnitude image (log-scaled) for display
    kSpaceMag = computeMagnitude(kSpace);
  }

  List<List<double>> computeMagnitude(ComplexImage kSpaceData) {
    const size = 256;
    final mag = List.generate(size, (_) => List.filled(size, 0.0));
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        // Shift: swap quadrants for centered display
        final sx = (x + size ~/ 2) % size;
        final sy = (y + size ~/ 2) % size;
        final r = kSpaceData.real[sx][sy];
        final i = kSpaceData.imag[sx][sy];
        mag[x][y] = log(1 + sqrt(r * r + i * i));
      }
    }
    return mag;
  }

  ComplexImage getManipulatedKSpace() {
    const size = 256;
    final manip = ComplexImage(size, size);

    // Copy original
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        manip.real[x][y] = kSpace.real[x][y];
        manip.imag[x][y] = kSpace.imag[x][y];
      }
    }

    // Delete margins
    if (deleteMargins) {
      for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
          if (x < marginLeft || x >= size - marginRight ||
              y < marginTop || y >= size - marginBottom) {
            manip.real[x][y] = 0;
            manip.imag[x][y] = 0;
          }
        }
      }
    }

    // Delete lines (every N-th row/column)
    if (deleteLines) {
      for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
          if (y % everyNRow == 0 || x % everyNCol == 0) {
            manip.real[x][y] = 0;
            manip.imag[x][y] = 0;
          }
        }
      }
    }

    // Delete center rectangle
    if (deleteRect) {
      final cx = size ~/ 2;
      final cy = size ~/ 2;
      final hw = rectWidth ~/ 2;
      final hh = rectHeight ~/ 2;
      for (int x = cx - hw; x <= cx + hw && x < size; x++) {
        for (int y = cy - hh; y <= cy + hh && y < size; y++) {
          if (x >= 0 && y >= 0) {
            manip.real[x][y] = 0;
            manip.imag[x][y] = 0;
          }
        }
      }
    }

    return manip;
  }

  void reverseTransform() {
    const size = 256;
    final manip = getManipulatedKSpace();
    final inv = inverseFFT2D(manip);

    // Convert to magnitude image (12-bit)
    final result = List.generate(size, (_) => List.filled(size, 0));
    double maxVal = 0;
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        final r = inv.real[x][y];
        final i = inv.imag[x][y];
        final mag = sqrt(r * r + i * i);
        if (mag > maxVal) maxVal = mag;
        result[x][y] = mag.round();
      }
    }

    // Scale to 12-bit range
    if (maxVal > 0) {
      final scale = 4095.0 / maxVal;
      for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
          result[x][y] = (result[x][y] * scale).round().clamp(0, 4095);
        }
      }
    }

    // Auto-window
    int minV = 4095, maxV = 0;
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        if (result[x][y] < minV) minV = result[x][y];
        if (result[x][y] > maxV) maxV = result[x][y];
      }
    }

    resultImage = result;
    resultWidth = max(1, maxV - minV);
    resultCenter = (minV + maxV) ~/ 2;
    update();
  }
}

/// K-Space Manipulator dialog.
/// Shows 4 panels: original image, original k-space,
/// manipulated k-space, and inverse-transformed result.
class KSpaceManipulator extends StatelessWidget {
  final List<List<int>> sourceImage;
  final int windowCenter;
  final int windowWidth;
  final void Function(List<List<int>> image)? onTransferToViewer;

  const KSpaceManipulator({
    super.key,
    required this.sourceImage,
    this.windowCenter = 2048,
    this.windowWidth = 4096,
    this.onTransferToViewer,
  });

  @override
  Widget build(BuildContext context) {
    // Register a scoped controller for this dialog
    final tag = 'kspace_${identityHashCode(this)}';
    Get.put(
      _KSpaceController(
        sourceImage: sourceImage,
        windowCenter: windowCenter,
        windowWidth: windowWidth,
      ),
      tag: tag,
    );

    return _KSpaceManipulatorBody(
      tag: tag,
      onTransferToViewer: onTransferToViewer,
    );
  }
}

/// Inner body of the K-Space dialog, uses GetBuilder to rebuild on state changes.
class _KSpaceManipulatorBody extends StatelessWidget {
  final String tag;
  final void Function(List<List<int>> image)? onTransferToViewer;

  const _KSpaceManipulatorBody({
    required this.tag,
    this.onTransferToViewer,
  });

  @override
  Widget build(BuildContext context) {
    return GetBuilder<_KSpaceController>(
      tag: tag,
      builder: (c) => Dialog(
        insetPadding: const EdgeInsets.all(16),
        child: ConstrainedBox(
          constraints: const BoxConstraints(maxWidth: 900, maxHeight: 700),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Title bar
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                color: Theme.of(context).colorScheme.inversePrimary,
                child: Row(
                  children: [
                    const Text('K-Space Manipulator',
                        style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    const Spacer(),
                    IconButton(
                      icon: const Icon(Icons.close),
                      onPressed: () {
                        Get.delete<_KSpaceController>(tag: tag);
                        Navigator.of(context).pop();
                      },
                    ),
                  ],
                ),
              ),
              // Content
              Flexible(
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // 4 image panels
                    Expanded(
                      flex: 3,
                      child: _buildImageGrid(c),
                    ),
                    // Controls
                    SizedBox(
                      width: 280,
                      child: _buildControls(context, c),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildImageGrid(_KSpaceController c) {
    final manipKSpace = c.getManipulatedKSpace();
    final manipMag = c.computeMagnitude(manipKSpace);

    return Container(
      color: Colors.black,
      child: AspectRatio(
        aspectRatio: 1,
        child: Column(
          children: [
            Expanded(
              child: Row(
                children: [
                  Expanded(child: _buildPanel(
                    'Original image',
                    _buildIntImage(c.sourceImage, c.windowCenter, c.windowWidth),
                  )),
                  const VerticalDivider(width: 2, color: Colors.white),
                  Expanded(child: _buildPanel(
                    'Original k-space',
                    _buildFloatImage(c.kSpaceMag),
                  )),
                ],
              ),
            ),
            const Divider(height: 2, color: Colors.white),
            Expanded(
              child: Row(
                children: [
                  Expanded(child: _buildPanel(
                    'Manipulated k-space',
                    _buildFloatImage(manipMag),
                  )),
                  const VerticalDivider(width: 2, color: Colors.white),
                  Expanded(child: _buildPanel(
                    c.resultImage != null ? 'Reverse transformed' : 'Press "Reverse transform"',
                    c.resultImage != null
                        ? _buildIntImage(c.resultImage!, c.resultCenter, c.resultWidth)
                        : null,
                  )),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPanel(String label, Future<ui.Image>? imageFuture) {
    return Stack(
      fit: StackFit.expand,
      children: [
        if (imageFuture != null)
          FutureBuilder<ui.Image>(
            future: imageFuture,
            builder: (context, snap) {
              if (snap.hasData) {
                return RawImage(image: snap.data, fit: BoxFit.contain, filterQuality: FilterQuality.none);
              }
              return const SizedBox.shrink();
            },
          )
        else
          Container(color: Colors.black),
        Positioned(
          left: 4, bottom: 2,
          child: Text(label,
              style: const TextStyle(color: Colors.white54, fontSize: 9)),
        ),
      ],
    );
  }

  Widget _buildControls(BuildContext context, _KSpaceController c) {
    return Container(
      color: Colors.grey[100],
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Delete Margins
            CheckboxListTile(
              dense: true,
              title: const Text('Delete margins', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
              value: c.deleteMargins,
              onChanged: (v) {
                c.deleteMargins = v!;
                c.update();
              },
            ),
            if (c.deleteMargins) ...[
              _slider('Left', c.marginLeft, 0, 127, (v) { c.marginLeft = v; c.update(); }),
              _slider('Right', c.marginRight, 0, 127, (v) { c.marginRight = v; c.update(); }),
              _slider('Top', c.marginTop, 0, 127, (v) { c.marginTop = v; c.update(); }),
              _slider('Bottom', c.marginBottom, 0, 127, (v) { c.marginBottom = v; c.update(); }),
            ],
            const Divider(),
            // Delete center rectangle
            CheckboxListTile(
              dense: true,
              title: const Text('Delete center rect', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
              value: c.deleteRect,
              onChanged: (v) {
                c.deleteRect = v!;
                c.update();
              },
            ),
            if (c.deleteRect) ...[
              _slider('Width', c.rectWidth, 0, 255, (v) { c.rectWidth = v; c.update(); }),
              _slider('Height', c.rectHeight, 0, 255, (v) { c.rectHeight = v; c.update(); }),
            ],
            const Divider(),
            // Delete lines
            CheckboxListTile(
              dense: true,
              title: const Text('Delete every N lines', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
              value: c.deleteLines,
              onChanged: (v) {
                c.deleteLines = v!;
                c.update();
              },
            ),
            if (c.deleteLines) ...[
              _slider('Every N row', c.everyNRow, 2, 256, (v) { c.everyNRow = v; c.update(); }),
              _slider('Every N col', c.everyNCol, 2, 256, (v) { c.everyNCol = v; c.update(); }),
            ],
            const Divider(),
            const SizedBox(height: 8),
            ElevatedButton.icon(
              icon: const Icon(Icons.transform),
              label: const Text('Reverse transform'),
              onPressed: () => c.reverseTransform(),
            ),
            const SizedBox(height: 8),
            ElevatedButton.icon(
              icon: const Icon(Icons.send),
              label: const Text('Transfer to viewer'),
              onPressed: c.resultImage != null && onTransferToViewer != null
                  ? () {
                      onTransferToViewer!(c.resultImage!);
                      Get.delete<_KSpaceController>(tag: tag);
                      Navigator.of(context).pop();
                    }
                  : null,
            ),
          ],
        ),
      ),
    );
  }

  Widget _slider(String label, int value, int min, int max, void Function(int) onChanged) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      child: Row(
        children: [
          SizedBox(width: 70, child: Text(label, style: const TextStyle(fontSize: 11))),
          Expanded(
            child: Slider(
              value: value.toDouble(),
              min: min.toDouble(),
              max: max.toDouble(),
              divisions: max - min,
              onChanged: (v) => onChanged(v.round()),
            ),
          ),
          SizedBox(width: 32, child: Text('$value', style: const TextStyle(fontSize: 11))),
        ],
      ),
    );
  }

  // Create ui.Image from int matrix with windowing
  static Future<ui.Image> _buildIntImage(
      List<List<int>> data, int center, int width) async {
    const size = 256;
    final pixels = Uint8List(size * size * 4);
    final minVal = center - width ~/ 2;
    final maxVal = center + width ~/ 2;
    final range = maxVal - minVal;
    final step = range > 0 ? 255.0 / range : 0.0;

    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        final idx = (y * size + x) * 4;
        int value = data[x][y];
        int gray;
        if (value <= minVal) {
          gray = 0;
        } else if (value >= maxVal) {
          gray = 255;
        } else {
          gray = ((value - minVal) * step).toInt().clamp(0, 255);
        }
        pixels[idx] = gray;
        pixels[idx + 1] = gray;
        pixels[idx + 2] = gray;
        pixels[idx + 3] = 255;
      }
    }

    final buffer = await ui.ImmutableBuffer.fromUint8List(pixels);
    final descriptor = ui.ImageDescriptor.raw(buffer,
        width: size, height: size, pixelFormat: ui.PixelFormat.rgba8888);
    final codec = await descriptor.instantiateCodec();
    final frame = await codec.getNextFrame();
    return frame.image;
  }

  // Create ui.Image from float matrix (auto-windowed)
  static Future<ui.Image> _buildFloatImage(List<List<double>> data) async {
    const size = 256;
    double minVal = double.infinity, maxVal = double.negativeInfinity;
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        if (data[x][y] < minVal) minVal = data[x][y];
        if (data[x][y] > maxVal) maxVal = data[x][y];
      }
    }
    final range = maxVal - minVal;
    final pixels = Uint8List(size * size * 4);

    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        final idx = (y * size + x) * 4;
        final gray = range > 0
            ? ((data[x][y] - minVal) / range * 255).toInt().clamp(0, 255)
            : 0;
        pixels[idx] = gray;
        pixels[idx + 1] = gray;
        pixels[idx + 2] = gray;
        pixels[idx + 3] = 255;
      }
    }

    final buffer = await ui.ImmutableBuffer.fromUint8List(pixels);
    final descriptor = ui.ImageDescriptor.raw(buffer,
        width: size, height: size, pixelFormat: ui.PixelFormat.rgba8888);
    final codec = await descriptor.instantiateCodec();
    final frame = await codec.getNextFrame();
    return frame.image;
  }
}
