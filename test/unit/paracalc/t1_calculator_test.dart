import 'package:flutter_test/flutter_test.dart';
import 'package:virtual_mri/paracalc/calculation/t1_calculator.dart';

void main() {
  group('T1Calculator.calculate', () {
    test('should return correct size output', () {
      const size = 16;
      final image1 = List.generate(size, (_) => List.filled(size, 100));
      final image2 = List.generate(size, (_) => List.filled(size, 80));

      final result = T1Calculator.calculate(
        image1: image1,
        image2: image2,
        tr: 25.0,
        alpha1: 5.0,
        alpha2: 20.0,
      );

      expect(result.length, size);
      expect(result[0].length, size);
    });

    test('should return zero for background pixels', () {
      const size = 8;
      final image1 = List.generate(size, (_) => List.filled(size, 0));
      final image2 = List.generate(size, (_) => List.filled(size, 0));

      final result = T1Calculator.calculate(
        image1: image1,
        image2: image2,
        tr: 25.0,
        alpha1: 5.0,
        alpha2: 20.0,
      );

      expect(result[0][0], 0.0);
    });

    test('should clamp T1 values to 0-8000 ms range', () {
      const size = 8;
      final image1 = List.generate(size, (_) => List.filled(size, 200));
      final image2 = List.generate(size, (_) => List.filled(size, 180));

      final result = T1Calculator.calculate(
        image1: image1,
        image2: image2,
        tr: 25.0,
        alpha1: 5.0,
        alpha2: 20.0,
      );

      for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
          expect(result[y][x], greaterThanOrEqualTo(0.0));
          expect(result[y][x], lessThanOrEqualTo(8000.0));
        }
      }
    });

    test('should produce non-zero T1 for valid tissue signal', () {
      const size = 4;
      // Simulate two FLASH images with known signal
      final image1 = List.generate(size, (_) => List.filled(size, 500));
      final image2 = List.generate(size, (_) => List.filled(size, 400));

      final result = T1Calculator.calculate(
        image1: image1,
        image2: image2,
        tr: 25.0,
        alpha1: 5.0,
        alpha2: 20.0,
      );

      // At least some pixels should have non-zero T1
      expect(result[0][0], greaterThan(0.0));
    });

    test('should respect backgroundSignal threshold', () {
      const size = 4;
      final image1 = List.generate(size, (_) => List.filled(size, 10));
      final image2 = List.generate(size, (_) => List.filled(size, 8));

      // With high threshold, everything should be background
      final result = T1Calculator.calculate(
        image1: image1,
        image2: image2,
        tr: 25.0,
        alpha1: 5.0,
        alpha2: 20.0,
        backgroundSignal: 20,
      );

      expect(result[0][0], 0.0);
    });
  });

  group('T1Calculator.calculateBackground', () {
    test('should calculate background from top-left corner', () {
      final image = List.generate(30, (_) => List.filled(30, 0));
      // Set top-left 20x20 to value 10
      for (int y = 0; y < 20; y++) {
        for (int x = 0; x < 20; x++) {
          image[y][x] = 10;
        }
      }
      final bg = T1Calculator.calculateBackground([image]);
      // Mean of 20x20 area = 10, result = 2 * mean = 20
      expect(bg, 20);
    });

    test('should handle images smaller than 20x20', () {
      final image = List.generate(5, (_) => List.filled(5, 6));
      final bg = T1Calculator.calculateBackground([image]);
      // Mean = 6, result = 2 * 6 = 12
      expect(bg, 12);
    });

    test('should average across multiple images', () {
      final image1 = List.generate(20, (_) => List.filled(20, 10));
      final image2 = List.generate(20, (_) => List.filled(20, 20));
      final bg = T1Calculator.calculateBackground([image1, image2]);
      // Mean = (10 + 20) / 2 = 15, result = 2 * 15 = 30
      expect(bg, 30);
    });
  });
}
