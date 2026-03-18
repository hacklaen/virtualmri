/// Image filters for parameter image post-processing.
///
/// Ported from the Java ImageJ plugins by Thomas Hacklaender.
class ImageFilters {
  /// Selective Median Filter: replaces pixels whose value exceeds
  /// (median of 8 nearest neighbors × factor) with that median.
  ///
  /// Used to remove outlier pixels from calculated parameter images.
  static List<List<double>> selectiveMedianFilter(
    List<List<double>> image, {
    double limitFactor = 3.0,
  }) {
    final height = image.length;
    final width = image[0].length;
    final result =
        List.generate(height, (y) => List<double>.from(image[y]));

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        final s = image[y][x];
        final median = _calcMedianNeighborhood(image, x, y, width, height);
        if (s > median * limitFactor) {
          result[y][x] = median;
        }
      }
    }
    return result;
  }

  /// Selective Smooth Filter: replaces pixels below a cutoff value with
  /// a weighted mean of 48 neighbors (7×7 kernel).
  ///
  /// Used to smooth near-zero or negative outlier pixels.
  static List<List<double>> selectiveSmoothFilter(
    List<List<double>> image, {
    double cutOff = 2.0,
  }) {
    final height = image.length;
    final width = image[0].length;
    final result =
        List.generate(height, (y) => List<double>.from(image[y]));

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (image[y][x] < cutOff) {
          result[y][x] = _calcWeightedNeighborhood(image, x, y, width, height);
        }
      }
    }
    return result;
  }

  static double _calcMedianNeighborhood(
      List<List<double>> image, int x, int y, int width, int height) {
    final values = <double>[];
    for (int nx = x - 1; nx <= x + 1; nx++) {
      for (int ny = y - 1; ny <= y + 1; ny++) {
        if (nx == x && ny == y) continue;
        if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
        values.add(image[ny][nx]);
      }
    }
    if (values.isEmpty) return 0;
    values.sort();
    final n = values.length;
    if (n.isEven) {
      return (values[n ~/ 2] + values[n ~/ 2 - 1]) / 2;
    } else {
      return values[n ~/ 2];
    }
  }

  static const List<List<int>> _smoothWeights = [
    [1, 1, 1, 1, 1, 1, 1],
    [1, 3, 3, 3, 3, 3, 1],
    [1, 3, 7, 7, 7, 3, 1],
    [1, 3, 7, 0, 7, 3, 1],
    [1, 3, 7, 7, 7, 3, 1],
    [1, 3, 3, 3, 3, 3, 1],
    [1, 1, 1, 1, 1, 1, 1],
  ];

  static double _calcWeightedNeighborhood(
      List<List<double>> image, int x, int y, int width, int height) {
    double mean = 0;
    int n = 0;
    for (int xOff = -3; xOff <= 3; xOff++) {
      for (int yOff = -3; yOff <= 3; yOff++) {
        final nx = x + xOff;
        final ny = y + yOff;
        if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
        final w = _smoothWeights[xOff + 3][yOff + 3];
        mean += w * image[ny][nx];
        n += w;
      }
    }
    return n > 0 ? mean / n : 0;
  }
}
