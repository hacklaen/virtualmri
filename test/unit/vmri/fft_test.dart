import 'package:flutter_test/flutter_test.dart';
import 'package:virtual_mri/vmri/simulation/fft.dart';

void main() {
  group('ComplexImage', () {
    test('should create zero-filled image', () {
      final img = ComplexImage(4, 4);
      expect(img.width, 4);
      expect(img.height, 4);
      expect(img.real[0][0], 0.0);
      expect(img.imag[0][0], 0.0);
    });

    test('should create from existing data', () {
      final real = List.generate(4, (x) => List.generate(4, (y) => x * 1.0));
      final imag = List.generate(4, (x) => List.generate(4, (y) => y * 1.0));
      final img = ComplexImage.from(4, 4, real, imag);
      expect(img.real[2][0], 2.0);
      expect(img.imag[0][3], 3.0);
    });
  });

  group('forwardFFT2D', () {
    test('should return zero for zero input', () {
      final input = List.generate(4, (_) => List.filled(4, 0.0));
      final result = forwardFFT2D(input, 4);
      for (int x = 0; x < 4; x++) {
        for (int y = 0; y < 4; y++) {
          expect(result.real[x][y], closeTo(0.0, 1e-10));
          expect(result.imag[x][y], closeTo(0.0, 1e-10));
        }
      }
    });

    test('should compute DC component as sum of all values', () {
      final input = List.generate(4, (_) => List.filled(4, 1.0));
      final result = forwardFFT2D(input, 4);
      // DC component (0,0) should be the sum of all elements = 16
      expect(result.real[0][0], closeTo(16.0, 1e-10));
      expect(result.imag[0][0], closeTo(0.0, 1e-10));
    });

    test('should handle 256x256 input without error', () {
      final input = List.generate(256, (_) => List.filled(256, 0.0));
      input[128][128] = 1.0; // Single point
      final result = forwardFFT2D(input, 256);
      expect(result.width, 256);
      expect(result.height, 256);
    });
  });

  group('inverseFFT2D', () {
    test('should reconstruct original signal', () {
      final input = List.generate(4, (x) => List.generate(4, (y) => (x + y) * 10.0));
      final freq = forwardFFT2D(input, 4);
      final reconstructed = inverseFFT2D(freq);

      for (int x = 0; x < 4; x++) {
        for (int y = 0; y < 4; y++) {
          expect(reconstructed.real[x][y], closeTo(input[x][y], 1e-8));
          expect(reconstructed.imag[x][y], closeTo(0.0, 1e-8));
        }
      }
    });
  });

  group('FFT roundtrip', () {
    test('should preserve energy (Parseval theorem)', () {
      final input = List.generate(8, (x) => List.generate(8, (y) => (x * y + 1.0)));

      // Energy in spatial domain
      double spatialEnergy = 0;
      for (int x = 0; x < 8; x++) {
        for (int y = 0; y < 8; y++) {
          spatialEnergy += input[x][y] * input[x][y];
        }
      }

      final freq = forwardFFT2D(input, 8);

      // Energy in frequency domain (scaled by N)
      double freqEnergy = 0;
      for (int x = 0; x < 8; x++) {
        for (int y = 0; y < 8; y++) {
          freqEnergy += freq.real[x][y] * freq.real[x][y] +
              freq.imag[x][y] * freq.imag[x][y];
        }
      }
      freqEnergy /= (8 * 8); // Normalize

      expect(freqEnergy, closeTo(spatialEnergy, 1e-6));
    });
  });
}
