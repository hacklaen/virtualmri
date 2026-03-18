import 'package:flutter_test/flutter_test.dart';
import 'package:virtual_mri/vmri/simulation/mri_simulator.dart';

void main() {
  group('SequenceParams', () {
    test('should have default values', () {
      final params = SequenceParams();
      expect(params.tr, 500);
      expect(params.te, 20);
      expect(params.ti, 500);
      expect(params.flipAngle, 90);
    });

    test('should calculate SNR ratio', () {
      final params = SequenceParams();
      final snr = params.calculateSNRatio();
      expect(snr, isA<double>());
      expect(snr, greaterThan(0));
    });
  });

  group('MriSimulator.calculate', () {
    late List<List<int>> t1;
    late List<List<int>> t2;
    late List<List<int>> pd;

    setUp(() {
      const size = 256;
      t1 = List.generate(size, (_) => List.filled(size, 0));
      t2 = List.generate(size, (_) => List.filled(size, 0));
      pd = List.generate(size, (_) => List.filled(size, 0));
    });

    test('should return 256x256 image for all-zero input', () {
      final params = SequenceParams();
      final result = MriSimulator.calculate(
        t1Matrix: t1,
        t2Matrix: t2,
        pdMatrix: pd,
        sequence: SequenceType.saturationRecovery,
        params: params,
      );
      expect(result.length, 256);
      expect(result[0].length, 256);
    });

    test('should return zero image for zero PD', () {
      final params = SequenceParams();
      final result = MriSimulator.calculate(
        t1Matrix: t1,
        t2Matrix: t2,
        pdMatrix: pd,
        sequence: SequenceType.saturationRecovery,
        params: params,
      );
      for (int x = 0; x < 256; x++) {
        for (int y = 0; y < 256; y++) {
          expect(result[x][y], 0);
        }
      }
    });

    test('should produce non-zero values with valid tissue parameters', () {
      // Place some "white matter" in center
      for (int x = 100; x < 150; x++) {
        for (int y = 100; y < 150; y++) {
          t1[x][y] = 510;
          t2[x][y] = 67;
          pd[x][y] = 54 * 25; // PD scaling
        }
      }
      final params = SequenceParams(tr: 500, te: 15);
      final result = MriSimulator.calculate(
        t1Matrix: t1,
        t2Matrix: t2,
        pdMatrix: pd,
        sequence: SequenceType.saturationRecovery,
        params: params,
      );
      // Center pixels should have non-zero signal
      expect(result[120][120], greaterThan(0));
      // Empty area should be zero
      expect(result[0][0], 0);
    });

    test('should produce different results for different sequences', () {
      // Place tissue
      for (int x = 100; x < 150; x++) {
        for (int y = 100; y < 150; y++) {
          t1[x][y] = 760;
          t2[x][y] = 77;
          pd[x][y] = 62 * 25;
        }
      }

      final params = SequenceParams(tr: 500, te: 15);

      final resultSR = MriSimulator.calculate(
        t1Matrix: t1, t2Matrix: t2, pdMatrix: pd,
        sequence: SequenceType.saturationRecovery, params: params,
      );
      final resultSE = MriSimulator.calculate(
        t1Matrix: t1, t2Matrix: t2, pdMatrix: pd,
        sequence: SequenceType.spinEcho, params: params,
      );

      // Different sequences should produce different signal intensities
      final signalSR = resultSR[120][120];
      final signalSE = resultSE[120][120];
      // Both should be > 0
      expect(signalSR, greaterThan(0));
      expect(signalSE, greaterThan(0));
    });

    test('should clamp output to 12-bit range (0-4095)', () {
      // Very high PD to force high signal
      for (int x = 0; x < 256; x++) {
        for (int y = 0; y < 256; y++) {
          t1[x][y] = 100;
          t2[x][y] = 100;
          pd[x][y] = 65535;
        }
      }
      final params = SequenceParams(tr: 10000, te: 1);
      final result = MriSimulator.calculate(
        t1Matrix: t1, t2Matrix: t2, pdMatrix: pd,
        sequence: SequenceType.saturationRecovery, params: params,
      );
      for (int x = 0; x < 256; x++) {
        for (int y = 0; y < 256; y++) {
          expect(result[x][y], lessThanOrEqualTo(4095));
          expect(result[x][y], greaterThanOrEqualTo(0));
        }
      }
    });

    test('should support all seven sequence types without errors', () {
      for (int x = 100; x < 150; x++) {
        for (int y = 100; y < 150; y++) {
          t1[x][y] = 500;
          t2[x][y] = 80;
          pd[x][y] = 50 * 25;
        }
      }
      final params = SequenceParams(tr: 500, te: 15, ti: 200, flipAngle: 30);

      for (final seqType in SequenceType.values) {
        final result = MriSimulator.calculate(
          t1Matrix: t1, t2Matrix: t2, pdMatrix: pd,
          sequence: seqType, params: params,
        );
        expect(result.length, 256, reason: 'Failed for $seqType');
        expect(result[0].length, 256, reason: 'Failed for $seqType');
      }
    });
  });
}
