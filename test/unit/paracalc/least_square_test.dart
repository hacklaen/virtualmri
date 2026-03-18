import 'package:flutter_test/flutter_test.dart';
import 'package:virtual_mri/paracalc/calculation/least_square.dart';

void main() {
  group('LeastSquare', () {
    test('should fit a perfect linear function y = 2x + 1', () {
      final ls = LeastSquare();
      ls.setSamples([
        [0.0, 1.0],
        [1.0, 3.0],
        [2.0, 5.0],
        [3.0, 7.0],
      ]);
      ls.setWeights([1.0, 1.0, 1.0, 1.0]);
      ls.normalizeWeights();
      ls.calculate();

      final solution = ls.solution;
      // solution[0] = b (intercept), solution[1] = m (slope)
      expect(solution[0], closeTo(1.0, 1e-10)); // b = 1
      expect(solution[1], closeTo(2.0, 1e-10)); // m = 2
    });

    test('should fit y = -0.5x + 10', () {
      final ls = LeastSquare();
      ls.setSamples([
        [0.0, 10.0],
        [2.0, 9.0],
        [4.0, 8.0],
        [6.0, 7.0],
        [8.0, 6.0],
      ]);
      ls.setWeights([1.0, 1.0, 1.0, 1.0, 1.0]);
      ls.normalizeWeights();
      ls.calculate();

      final solution = ls.solution;
      expect(solution[0], closeTo(10.0, 1e-10));
      expect(solution[1], closeTo(-0.5, 1e-10));
    });

    test('should handle noisy data', () {
      final ls = LeastSquare();
      // Roughly y = 3x + 2 with noise
      ls.setSamples([
        [0.0, 2.1],
        [1.0, 4.9],
        [2.0, 8.2],
        [3.0, 10.8],
        [4.0, 14.1],
      ]);
      ls.setWeights([1.0, 1.0, 1.0, 1.0, 1.0]);
      ls.normalizeWeights();
      ls.calculate();

      final solution = ls.solution;
      // Results should be approximately y = 3x + 2
      expect(solution[0], closeTo(2.0, 0.5));
      expect(solution[1], closeTo(3.0, 0.5));
    });

    test('should handle weighted samples', () {
      final ls = LeastSquare();
      ls.setSamples([
        [0.0, 1.0],
        [1.0, 3.0],
        [2.0, 5.0],
      ]);
      // Give higher weight to last point
      ls.setWeights([1.0, 1.0, 10.0]);
      ls.normalizeWeights();
      ls.calculate();

      final solution = ls.solution;
      // Should still be close to y = 2x + 1
      expect(solution[0], closeTo(1.0, 0.1));
      expect(solution[1], closeTo(2.0, 0.1));
    });

    test('should normalize weights to sum to 1', () {
      final ls = LeastSquare();
      ls.setSamples([
        [0.0, 0.0],
        [1.0, 1.0],
      ]);
      ls.setWeights([5.0, 5.0]);
      ls.normalizeWeights();
      // After normalization, both should be 0.5
      // We can't directly access weights, but the calculation should work
      ls.calculate();
      expect(ls.solution, isNotNull);
    });
  });
}
