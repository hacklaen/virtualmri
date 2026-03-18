import 'dart:math';
import 'package:flutter_test/flutter_test.dart';
import 'package:virtual_mri/paracalc/calculation/t2_calculator.dart';

void main() {
  group('T2PdResult', () {
    test('should hold t2Map and pdMap', () {
      final t2Map = [
        [100.0, 200.0]
      ];
      final pdMap = [
        [500.0, 600.0]
      ];
      final result = T2PdResult(t2Map: t2Map, pdMap: pdMap);
      expect(result.t2Map[0][0], 100.0);
      expect(result.pdMap[0][1], 600.0);
    });
  });

  group('T2Calculator.calculate', () {
    test('should return correct size output', () {
      const size = 8;
      // 4 echo images
      final images = List.generate(
        4,
        (_) => List.generate(size, (_) => List.filled(size, 100)),
      );

      final result = T2Calculator.calculate(
        images: images,
        teFirst: 20.0,
        teStep: 20.0,
      );

      expect(result.t2Map.length, size);
      expect(result.t2Map[0].length, size);
      expect(result.pdMap.length, size);
      expect(result.pdMap[0].length, size);
    });

    test('should return zero for background pixels', () {
      const size = 4;
      final images = List.generate(
        3,
        (_) => List.generate(size, (_) => List.filled(size, 0)),
      );

      final result = T2Calculator.calculate(
        images: images,
        teFirst: 20.0,
        teStep: 20.0,
      );

      expect(result.t2Map[0][0], 0.0);
      expect(result.pdMap[0][0], 0.0);
    });

    test('should calculate T2 from exponential decay', () {
      const size = 4;
      const t2True = 100.0; // 100ms T2
      const initialSignal = 1000.0;
      const teFirst = 20.0;
      const teStep = 20.0;
      const numEchoes = 5;

      // Generate synthetic exponential decay: S = S0 * exp(-TE/T2)
      final images = List.generate(numEchoes, (echo) {
        final te = teFirst + echo * teStep;
        final signal = (initialSignal * exp(-te / t2True)).round();
        return List.generate(size, (_) => List.filled(size, signal));
      });

      final result = T2Calculator.calculate(
        images: images,
        teFirst: teFirst,
        teStep: teStep,
        backgroundSignal: 1,
      );

      // T2 should be close to 100ms for non-background pixels
      expect(result.t2Map[0][0], closeTo(t2True, 5.0));
    });

    test('should clamp T2 to 0-8000 ms range', () {
      const size = 4;
      // Constant signal (infinite T2 would be needed)
      final images = List.generate(
        3,
        (_) => List.generate(size, (_) => List.filled(size, 100)),
      );

      final result = T2Calculator.calculate(
        images: images,
        teFirst: 20.0,
        teStep: 20.0,
        backgroundSignal: 1,
      );

      for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
          expect(result.t2Map[y][x], greaterThanOrEqualTo(0.0));
          expect(result.t2Map[y][x], lessThanOrEqualTo(8000.0));
        }
      }
    });

    test('should clamp PD to 0-1000 range', () {
      const size = 4;
      final images = List.generate(3, (echo) {
        final te = 20.0 + echo * 20.0;
        final signal = (500 * exp(-te / 80.0)).round();
        return List.generate(size, (_) => List.filled(size, signal));
      });

      final result = T2Calculator.calculate(
        images: images,
        teFirst: 20.0,
        teStep: 20.0,
        backgroundSignal: 1,
      );

      for (int y = 0; y < size; y++) {
        for (int x = 0; x < size; x++) {
          expect(result.pdMap[y][x], greaterThanOrEqualTo(0.0));
          expect(result.pdMap[y][x], lessThanOrEqualTo(1000.0));
        }
      }
    });
  });

  group('T2Calculator.calculateBackground', () {
    test('should calculate background from top-left corner', () {
      final image = List.generate(30, (_) => List.filled(30, 0));
      for (int y = 0; y < 20; y++) {
        for (int x = 0; x < 20; x++) {
          image[y][x] = 8;
        }
      }
      final bg = T2Calculator.calculateBackground([image]);
      expect(bg, 16); // 2 * mean(8) = 16
    });
  });
}
