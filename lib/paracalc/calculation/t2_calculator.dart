import 'dart:math';

import 'least_square.dart';

/// Result of T2/PD calculation.
class T2PdResult {
  final List<List<double>> t2Map;
  final List<List<double>> pdMap;
  const T2PdResult({required this.t2Map, required this.pdMap});
}

/// Calculates T2 and proton density (PD) parameter images from a multi-echo
/// spin-echo image stack using linear least squares fit on semi-log data.
///
/// Formula: ln(S) = ln(PD) - TE/T2
/// The fit yields: b = ln(PD), m = -1/T2
///
/// Ported from the Java ImageJ plugin by Thomas Hacklaender.
class T2Calculator {
  /// Calculate T2 and PD maps from a stack of ≥3 echo images.
  ///
  /// [images] list of 2D pixel arrays \[y\]\[x\] (at least 3).
  /// [teFirst] echo time of first image in ms.
  /// [teStep] echo time increment between images in ms.
  /// [backgroundSignal] threshold for background detection.
  ///
  /// Returns [T2PdResult] with T2 in ms (clamped to \[0, 8000\]) and
  /// PD as relative values (0–1000).
  static T2PdResult calculate({
    required List<List<List<int>>> images,
    required double teFirst,
    required double teStep,
    int backgroundSignal = 5,
  }) {
    final numSlices = images.length;
    final height = images[0].length;
    final width = images[0][0].length;

    final t2Map =
        List.generate(height, (_) => List<double>.filled(width, 0.0));
    final pdRaw =
        List.generate(height, (_) => List<double>.filled(width, 0.0));
    final pdMap =
        List.generate(height, (_) => List<double>.filled(width, 0.0));

    final ls = LeastSquare();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        // Build samples: [TE, ln(signal)]
        final samples =
            List.generate(numSlices, (_) => List<double>.filled(2, 0.0));
        final weights = List<double>.filled(numSlices, 1.0);
        double meanSignal = 0;

        for (int slice = 0; slice < numSlices; slice++) {
          final te = teFirst + slice * teStep;
          final signal = images[slice][y][x];
          meanSignal += signal;
          samples[slice][0] = te;
          samples[slice][1] = signal > 0 ? log(signal.toDouble()) : 0.0;
        }
        meanSignal /= numSlices;
        if (meanSignal < backgroundSignal) continue;

        ls.setSamples(samples);
        ls.setWeights(weights);
        ls.normalizeWeights();
        ls.calculate();
        final solution = ls.solution;

        // b = ln(PD) => PD_raw = exp(b)
        pdRaw[y][x] = exp(solution[0]);

        // m = -1/T2 => T2 = -1/m
        var t2 = solution[1] != 0 ? -1.0 / solution[1] : 0.0;
        t2 = t2.clamp(0.0, 8000.0);
        t2Map[y][x] = t2;
      }

      // Normalize PD per row (as in the Java original)
      double maxPd = 0;
      for (int x = 0; x < width; x++) {
        if (pdRaw[y][x] > maxPd) maxPd = pdRaw[y][x];
      }
      final factor = maxPd > 0 ? 1000.0 / maxPd : 0.0;
      for (int x = 0; x < width; x++) {
        pdMap[y][x] = (pdRaw[y][x] * factor).clamp(0.0, 1000.0);
      }
    }

    return T2PdResult(t2Map: t2Map, pdMap: pdMap);
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
