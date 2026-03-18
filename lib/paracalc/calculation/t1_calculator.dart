import 'dart:math';

/// Calculates a T1 parameter image from two FLASH (gradient echo) images
/// acquired with different flip angles.
///
/// Formula: t1 = TR / ln((sin(α₁)cos(α₂) - q·sin(α₂)cos(α₁)) / (sin(α₁) - q·sin(α₂)))
/// where q = S₁/S₂
///
/// Ported from the Java ImageJ plugin by Thomas Hacklaender.
class T1Calculator {
  /// Calculate T1 map from two images with different flip angles.
  ///
  /// [image1] and [image2] are 2D pixel arrays \[y\]\[x\] (16-bit).
  /// [tr] repetition time in ms.
  /// [alpha1] flip angle of first image in degrees.
  /// [alpha2] flip angle of second image in degrees.
  /// [backgroundSignal] threshold below which pixels are treated as background.
  ///
  /// Returns 2D array with T1 values in ms, clamped to \[0, 8000\].
  static List<List<double>> calculate({
    required List<List<int>> image1,
    required List<List<int>> image2,
    required double tr,
    required double alpha1,
    required double alpha2,
    int backgroundSignal = 5,
  }) {
    final height = image1.length;
    final width = image1[0].length;

    final sina1 = sin(pi * alpha1 / 180.0);
    final cosa1 = cos(pi * alpha1 / 180.0);
    final sina2 = sin(pi * alpha2 / 180.0);
    final cosa2 = cos(pi * alpha2 / 180.0);

    final result =
        List.generate(height, (_) => List<double>.filled(width, 0.0));

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        final s1 = image1[y][x];
        final s2 = image2[y][x];

        if (s1 <= backgroundSignal || s2 <= backgroundSignal) continue;

        final q = s1.toDouble() / s2.toDouble();
        final numerator = sina1 * cosa2 - q * sina2 * cosa1;
        final denominator = sina1 - q * sina2;

        if (denominator == 0 || numerator / denominator <= 0) continue;

        var t1 = tr / log(numerator / denominator);

        t1 = t1.clamp(0.0, 8000.0);
        result[y][x] = t1;
      }
    }

    return result;
  }

  /// Auto-detect background signal from the top-left 20×20 corner.
  static int calculateBackground(List<List<List<int>>> images) {
    double mean = 0;
    int n = 0;
    for (final image in images) {
      final maxY = image.length < 20 ? image.length : 20;
      final maxX = image[0].length < 20 ? image[0].length : 20;
      for (int y = 0; y < maxY; y++) {
        for (int x = 0; x < maxX; x++) {
          mean += image[y][x];
          n++;
        }
      }
    }
    if (n == 0) return 5;
    mean /= n;
    return (2 * mean).round();
  }
}
