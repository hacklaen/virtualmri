import 'dart:typed_data';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';

/// Renders a grayscale image from double values with windowing (center/width).
///
/// Converts the 2D double array to an 8-bit grayscale image using
/// the specified windowing parameters.
class ParameterImageDisplay extends StatelessWidget {
  final List<List<double>> imageData;
  final double windowCenter;
  final double windowWidth;
  final String label;

  const ParameterImageDisplay({
    super.key,
    required this.imageData,
    required this.windowCenter,
    required this.windowWidth,
    this.label = '',
  });

  @override
  Widget build(BuildContext context) {
    if (imageData.isEmpty || imageData[0].isEmpty) {
      return const Center(child: Text('No image data'));
    }

    return Column(
      children: [
        if (label.isNotEmpty)
          Padding(
            padding: const EdgeInsets.only(bottom: 4),
            child: Text(label,
                style: Theme.of(context).textTheme.titleSmall),
          ),
        Expanded(
          child: FutureBuilder<ui.Image>(
            future: _createImage(),
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                return CustomPaint(
                  painter: _ImagePainter(snapshot.data!),
                  size: Size.infinite,
                );
              }
              if (snapshot.hasError) {
                return Center(child: Text('Error: ${snapshot.error}'));
              }
              return const Center(child: CircularProgressIndicator());
            },
          ),
        ),
      ],
    );
  }

  Future<ui.Image> _createImage() async {
    final height = imageData.length;
    final width = imageData[0].length;
    final pixels = Uint8List(width * height * 4);

    final wc = windowCenter;
    final ww = windowWidth;

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        final v = imageData[y][x];
        double grey;
        if (ww <= 0) {
          grey = v > wc ? 255 : 0;
        } else if (v <= wc - 0.5 - (ww - 1) / 2) {
          grey = 0;
        } else if (v > wc - 0.5 + (ww - 1) / 2) {
          grey = 255;
        } else {
          grey = ((v - (wc - 0.5)) / (ww - 1) + 0.5) * 255;
        }
        final g = grey.clamp(0, 255).toInt();
        final idx = (y * width + x) * 4;
        pixels[idx] = g;
        pixels[idx + 1] = g;
        pixels[idx + 2] = g;
        pixels[idx + 3] = 255;
      }
    }

    final completer = ui.ImmutableBuffer.fromUint8List(pixels);
    final buffer = await completer;
    final descriptor = ui.ImageDescriptor.raw(
      buffer,
      width: width,
      height: height,
      pixelFormat: ui.PixelFormat.rgba8888,
    );
    final codec =
        await descriptor.instantiateCodec(targetWidth: width, targetHeight: height);
    final frame = await codec.getNextFrame();
    return frame.image;
  }
}

class _ImagePainter extends CustomPainter {
  final ui.Image image;
  _ImagePainter(this.image);

  @override
  void paint(Canvas canvas, Size size) {
    final src = Rect.fromLTWH(
        0, 0, image.width.toDouble(), image.height.toDouble());
    // Maintain aspect ratio
    final scale = (size.width / image.width)
        .clamp(0.0, size.height / image.height);
    final dstWidth = image.width * scale;
    final dstHeight = image.height * scale;
    final dst = Rect.fromLTWH(
      (size.width - dstWidth) / 2,
      (size.height - dstHeight) / 2,
      dstWidth,
      dstHeight,
    );
    canvas.drawImageRect(image, src, dst, Paint());
  }

  @override
  bool shouldRepaint(_ImagePainter oldDelegate) => oldDelegate.image != image;
}
