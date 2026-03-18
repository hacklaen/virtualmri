import 'package:flutter_test/flutter_test.dart';
import 'package:virtual_mri/paracalc/calculation/image_filters.dart';

void main() {
  group('ImageFilters.selectiveMedianFilter', () {
    test('should not modify pixels below limit', () {
      // Uniform 5x5 image with value 10
      final image = List.generate(5, (_) => List.filled(5, 10.0));
      final result = ImageFilters.selectiveMedianFilter(image);

      for (int y = 0; y < 5; y++) {
        for (int x = 0; x < 5; x++) {
          expect(result[y][x], 10.0);
        }
      }
    });

    test('should replace outlier pixel exceeding limit factor', () {
      // Uniform image with one outlier
      final image = List.generate(5, (_) => List.filled(5, 10.0));
      image[2][2] = 100.0; // Outlier: 100 > 10 * 3.0
      final result = ImageFilters.selectiveMedianFilter(image);

      // The outlier should be replaced with median of neighbors (10.0)
      expect(result[2][2], 10.0);
    });

    test('should preserve pixel just below limit', () {
      final image = List.generate(5, (_) => List.filled(5, 10.0));
      image[2][2] = 29.0; // Just below 10 * 3.0 = 30.0
      final result = ImageFilters.selectiveMedianFilter(image);

      expect(result[2][2], 29.0);
    });

    test('should handle custom limit factor', () {
      final image = List.generate(5, (_) => List.filled(5, 10.0));
      image[2][2] = 25.0; // 25 > 10 * 2.0 = 20
      final result =
          ImageFilters.selectiveMedianFilter(image, limitFactor: 2.0);

      expect(result[2][2], 10.0);
    });

    test('should handle edge pixels correctly', () {
      final image = List.generate(3, (_) => List.filled(3, 5.0));
      image[0][0] = 100.0; // Corner outlier
      final result = ImageFilters.selectiveMedianFilter(image);

      // Corner pixel should be replaced
      expect(result[0][0], 5.0);
    });
  });

  group('ImageFilters.selectiveSmoothFilter', () {
    test('should not modify pixels above cutoff', () {
      final image = List.generate(7, (_) => List.filled(7, 100.0));
      final result = ImageFilters.selectiveSmoothFilter(image);

      for (int y = 0; y < 7; y++) {
        for (int x = 0; x < 7; x++) {
          expect(result[y][x], 100.0);
        }
      }
    });

    test('should smooth pixel below cutoff value', () {
      final image = List.generate(7, (_) => List.filled(7, 100.0));
      image[3][3] = 0.0; // Below default cutOff of 2.0
      final result = ImageFilters.selectiveSmoothFilter(image);

      // The center pixel should be replaced with weighted neighborhood mean
      // which should be close to 100.0
      expect(result[3][3], greaterThan(0.0));
      expect(result[3][3], closeTo(100.0, 5.0));
    });

    test('should handle custom cutoff', () {
      final image = List.generate(7, (_) => List.filled(7, 100.0));
      image[3][3] = 5.0; // Above default 2.0, below custom 10.0
      final result =
          ImageFilters.selectiveSmoothFilter(image, cutOff: 10.0);

      // Should be smoothed
      expect(result[3][3], greaterThan(5.0));
    });
  });
}
