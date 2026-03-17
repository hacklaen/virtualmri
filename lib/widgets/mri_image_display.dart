import 'dart:typed_data';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';

/// Displays a 12-bit MRI image with windowing (center/width).
class MriImageDisplay extends StatelessWidget {
  final List<List<int>>? imageData;
  final int windowCenter;
  final int windowWidth;

  const MriImageDisplay({
    super.key,
    required this.imageData,
    required this.windowCenter,
    required this.windowWidth,
  });

  @override
  Widget build(BuildContext context) {
    if (imageData == null) {
      return Container(
        color: Colors.black,
        child: const Center(
          child: Text('No image', style: TextStyle(color: Colors.grey)),
        ),
      );
    }
    return FutureBuilder<ui.Image>(
      future: _createImage(imageData!, windowCenter, windowWidth),
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return RawImage(
            image: snapshot.data,
            fit: BoxFit.contain,
            filterQuality: FilterQuality.none,
          );
        }
        return Container(color: Colors.black);
      },
    );
  }

  static Future<ui.Image> _createImage(
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

    final completer = ui.ImmutableBuffer.fromUint8List(pixels);
    final buffer = await completer;
    final descriptor = ui.ImageDescriptor.raw(
      buffer,
      width: size,
      height: size,
      pixelFormat: ui.PixelFormat.rgba8888,
    );
    final codec = await descriptor.instantiateCodec();
    final frame = await codec.getNextFrame();
    return frame.image;
  }
}
