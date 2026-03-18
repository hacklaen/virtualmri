import 'dart:math';

/// 2D FFT implementation for 256x256 complex images.
/// Mirrors the jigl FFT library used in the Java version.
class ComplexImage {
  final int width;
  final int height;
  final List<List<double>> real;
  final List<List<double>> imag;

  ComplexImage(this.width, this.height)
      : real = List.generate(width, (_) => List.filled(height, 0.0)),
        imag = List.generate(width, (_) => List.filled(height, 0.0));

  ComplexImage.from(this.width, this.height, this.real, this.imag);
}

/// Perform forward 2D FFT on a real-valued image.
ComplexImage forwardFFT2D(List<List<double>> image, int size) {
  final result = ComplexImage(size, size);

  // Copy input to result real part
  for (int x = 0; x < size; x++) {
    for (int y = 0; y < size; y++) {
      result.real[x][y] = image[x][y];
    }
  }

  // FFT along rows (for each y, transform along x)
  for (int y = 0; y < size; y++) {
    final rowReal = List<double>.generate(size, (x) => result.real[x][y]);
    final rowImag = List<double>.generate(size, (x) => result.imag[x][y]);
    _fft1d(rowReal, rowImag, false);
    for (int x = 0; x < size; x++) {
      result.real[x][y] = rowReal[x];
      result.imag[x][y] = rowImag[x];
    }
  }

  // FFT along columns (for each x, transform along y)
  for (int x = 0; x < size; x++) {
    final colReal = List<double>.from(result.real[x]);
    final colImag = List<double>.from(result.imag[x]);
    _fft1d(colReal, colImag, false);
    result.real[x] = colReal;
    result.imag[x] = colImag;
  }

  return result;
}

/// Perform inverse 2D FFT and return a ComplexImage.
ComplexImage inverseFFT2D(ComplexImage input) {
  final size = input.width;
  final result = ComplexImage(size, size);

  // Copy input
  for (int x = 0; x < size; x++) {
    for (int y = 0; y < size; y++) {
      result.real[x][y] = input.real[x][y];
      result.imag[x][y] = input.imag[x][y];
    }
  }

  // Inverse FFT along rows
  for (int y = 0; y < size; y++) {
    final rowReal = List<double>.generate(size, (x) => result.real[x][y]);
    final rowImag = List<double>.generate(size, (x) => result.imag[x][y]);
    _fft1d(rowReal, rowImag, true);
    for (int x = 0; x < size; x++) {
      result.real[x][y] = rowReal[x];
      result.imag[x][y] = rowImag[x];
    }
  }

  // Inverse FFT along columns
  for (int x = 0; x < size; x++) {
    final colReal = List<double>.from(result.real[x]);
    final colImag = List<double>.from(result.imag[x]);
    _fft1d(colReal, colImag, true);
    result.real[x] = colReal;
    result.imag[x] = colImag;
  }

  return result;
}

/// Cooley-Tukey radix-2 FFT in-place.
/// [inverse] = true for inverse FFT.
void _fft1d(List<double> real, List<double> imag, bool inverse) {
  final n = real.length;
  if (n <= 1) return;

  // Bit-reversal permutation
  int j = 0;
  for (int i = 0; i < n - 1; i++) {
    if (i < j) {
      double tmp = real[i];
      real[i] = real[j];
      real[j] = tmp;
      tmp = imag[i];
      imag[i] = imag[j];
      imag[j] = tmp;
    }
    int k = n >> 1;
    while (k <= j) {
      j -= k;
      k >>= 1;
    }
    j += k;
  }

  // Butterfly
  final sign = inverse ? 1.0 : -1.0;
  int step = 2;
  while (step <= n) {
    final halfStep = step >> 1;
    final angle = sign * pi / halfStep;
    final wReal = cos(angle);
    final wImag = sin(angle);

    for (int i = 0; i < n; i += step) {
      double curReal = 1.0;
      double curImag = 0.0;
      for (int k = 0; k < halfStep; k++) {
        final tReal = curReal * real[i + k + halfStep] - curImag * imag[i + k + halfStep];
        final tImag = curReal * imag[i + k + halfStep] + curImag * real[i + k + halfStep];
        real[i + k + halfStep] = real[i + k] - tReal;
        imag[i + k + halfStep] = imag[i + k] - tImag;
        real[i + k] += tReal;
        imag[i + k] += tImag;
        final newCurReal = curReal * wReal - curImag * wImag;
        curImag = curReal * wImag + curImag * wReal;
        curReal = newCurReal;
      }
    }
    step <<= 1;
  }

  // Normalize for inverse
  if (inverse) {
    for (int i = 0; i < n; i++) {
      real[i] /= n;
      imag[i] /= n;
    }
  }
}
