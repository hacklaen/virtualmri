import 'dart:math';
import 'dart:typed_data';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';

import '../simulation/fft.dart';

/// K-Space Manipulator dialog.
/// Shows 4 panels: original image, original k-space,
/// manipulated k-space, and inverse-transformed result.
class KSpaceManipulator extends StatefulWidget {
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
  State<KSpaceManipulator> createState() => _KSpaceManipulatorState();
}

class _KSpaceManipulatorState extends State<KSpaceManipulator> {
  // K-space data (original)
  late ComplexImage _kSpace;
  late List<List<double>> _kSpaceMag; // magnitude for display

  // Manipulation parameters
  bool _deleteMargins = false;
  int _marginLeft = 0, _marginRight = 0, _marginTop = 0, _marginBottom = 0;

  bool _deleteRect = false;
  int _rectWidth = 0, _rectHeight = 0;

  bool _deleteLines = false;
  int _everyNRow = 2, _everyNCol = 2;

  // Result
  List<List<int>>? _resultImage;
  int _resultCenter = 2048;
  int _resultWidth = 4096;

  @override
  void initState() {
    super.initState();
    _computeKSpace();
  }

  void _computeKSpace() {
    const size = 256;
    // Convert source image (int) to double for FFT
    final input = List.generate(
        size, (x) => List.generate(size, (y) => widget.sourceImage[x][y].toDouble()));

    _kSpace = forwardFFT2D(input, size);

    // Compute magnitude image (log-scaled) for display
    _kSpaceMag = _computeMagnitude(_kSpace);
  }

  List<List<double>> _computeMagnitude(ComplexImage kSpace) {
    const size = 256;
    final mag = List.generate(size, (_) => List.filled(size, 0.0));
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        // Shift: swap quadrants for centered display
        final sx = (x + size ~/ 2) % size;
        final sy = (y + size ~/ 2) % size;
        final r = kSpace.real[sx][sy];
        final i = kSpace.imag[sx][sy];
        mag[x][y] = log(1 + sqrt(r * r + i * i));
      }
    }
    return mag;
  }

  ComplexImage _getManipulatedKSpace() {
    const size = 256;
    final manip = ComplexImage(size, size);

    // Copy original
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        manip.real[x][y] = _kSpace.real[x][y];
        manip.imag[x][y] = _kSpace.imag[x][y];
      }
    }

    // Delete margins
    if (_deleteMargins) {
      for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
          if (x < _marginLeft || x >= size - _marginRight ||
              y < _marginTop || y >= size - _marginBottom) {
            manip.real[x][y] = 0;
            manip.imag[x][y] = 0;
          }
        }
      }
    }

    // Delete lines (every N-th row/column)
    if (_deleteLines) {
      for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
          if (y % _everyNRow == 0 || x % _everyNCol == 0) {
            manip.real[x][y] = 0;
            manip.imag[x][y] = 0;
          }
        }
      }
    }

    // Delete center rectangle
    if (_deleteRect) {
      final cx = size ~/ 2;
      final cy = size ~/ 2;
      final hw = _rectWidth ~/ 2;
      final hh = _rectHeight ~/ 2;
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

  void _reverseTransform() {
    const size = 256;
    final manip = _getManipulatedKSpace();
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

    setState(() {
      _resultImage = result;
      _resultWidth = max(1, maxV - minV);
      _resultCenter = (minV + maxV) ~/ 2;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
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
                    onPressed: () => Navigator.of(context).pop(),
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
                    child: _buildImageGrid(),
                  ),
                  // Controls
                  SizedBox(
                    width: 280,
                    child: _buildControls(),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildImageGrid() {
    final manipKSpace = _getManipulatedKSpace();
    final manipMag = _computeMagnitude(manipKSpace);

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
                    _buildIntImage(widget.sourceImage, widget.windowCenter, widget.windowWidth),
                  )),
                  const VerticalDivider(width: 2, color: Colors.white),
                  Expanded(child: _buildPanel(
                    'Original k-space',
                    _buildFloatImage(_kSpaceMag),
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
                    _resultImage != null ? 'Reverse transformed' : 'Press "Reverse transform"',
                    _resultImage != null
                        ? _buildIntImage(_resultImage!, _resultCenter, _resultWidth)
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

  Widget _buildControls() {
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
              value: _deleteMargins,
              onChanged: (v) => setState(() => _deleteMargins = v!),
            ),
            if (_deleteMargins) ...[
              _slider('Left', _marginLeft, 0, 127, (v) => _marginLeft = v),
              _slider('Right', _marginRight, 0, 127, (v) => _marginRight = v),
              _slider('Top', _marginTop, 0, 127, (v) => _marginTop = v),
              _slider('Bottom', _marginBottom, 0, 127, (v) => _marginBottom = v),
            ],
            const Divider(),
            // Delete center rectangle
            CheckboxListTile(
              dense: true,
              title: const Text('Delete center rect', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
              value: _deleteRect,
              onChanged: (v) => setState(() => _deleteRect = v!),
            ),
            if (_deleteRect) ...[
              _slider('Width', _rectWidth, 0, 255, (v) => _rectWidth = v),
              _slider('Height', _rectHeight, 0, 255, (v) => _rectHeight = v),
            ],
            const Divider(),
            // Delete lines
            CheckboxListTile(
              dense: true,
              title: const Text('Delete every N lines', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
              value: _deleteLines,
              onChanged: (v) => setState(() => _deleteLines = v!),
            ),
            if (_deleteLines) ...[
              _slider('Every N row', _everyNRow, 2, 256, (v) => _everyNRow = v),
              _slider('Every N col', _everyNCol, 2, 256, (v) => _everyNCol = v),
            ],
            const Divider(),
            const SizedBox(height: 8),
            ElevatedButton.icon(
              icon: const Icon(Icons.transform),
              label: const Text('Reverse transform'),
              onPressed: _reverseTransform,
            ),
            const SizedBox(height: 8),
            ElevatedButton.icon(
              icon: const Icon(Icons.send),
              label: const Text('Transfer to viewer'),
              onPressed: _resultImage != null && widget.onTransferToViewer != null
                  ? () {
                      widget.onTransferToViewer!(_resultImage!);
                      Navigator.of(context).pop();
                    }
                  : null,
            ),
          ],
        ),
      ),
    );
  }

  Widget _slider(String label, int value, int min, int max, void Function(int) setter) {
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
              onChanged: (v) => setState(() => setter(v.round())),
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
