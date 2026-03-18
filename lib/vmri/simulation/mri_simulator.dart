import 'dart:math';

import 'fft.dart';

/// MRI sequence parameters used by pulse sequences.
class SequenceParams {
  int tr;
  int te;
  int ti;
  int flipAngle;
  int etl;
  int esp;
  int fov;
  int matrix;
  int rect;
  int thickness;
  int nex;
  int coil; // 0=surface, 1=body
  double snRatio;
  bool freqOversample;
  bool phaseOversample;

  SequenceParams({
    this.tr = 500,
    this.te = 20,
    this.ti = 500,
    this.flipAngle = 90,
    this.etl = 3,
    this.esp = 15,
    this.fov = 256,
    this.matrix = 256,
    this.rect = 8,
    this.thickness = 6,
    this.nex = 1,
    this.coil = 0,
    this.snRatio = 1.0,
    this.freqOversample = false,
    this.phaseOversample = false,
  });

  /// Calculate SNR factor matching Java PulsesequenceUI.getSNRatio()
  double calculateSNRatio() {
    const gyro = 42.6;
    const grad = 10.0;
    const factor = 3.44;

    int nex_ = nex;
    int ny = 256;
    double fov_ = fov.toDouble();

    if (phaseOversample) {
      ny *= 2;
      if (nex_ > 1) nex_ ~/= 2;
    }

    final pixsize = fov_ / matrix;
    final vol = pow(pixsize, 2) * thickness;

    if (freqOversample) {
      fov_ *= 2;
    }

    final bw = gyro * grad * fov_;
    return factor * vol * sqrt(ny * nex_ / bw);
  }
}

/// Enumerates available MRI pulse sequences.
enum SequenceType {
  saturationRecovery,
  inversionRecovery,
  spinEcho,
  turboSpinEcho,
  flash,
  geSpoiled,
  geRefocused,
}

/// Enumerates available artifact types.
enum ArtifactType {
  none,
  motion,
  periodicMotion,
}

/// Motion artifact parameters.
class MotionParams {
  int xSpeed;
  int ySpeed;
  int startTime;
  int stopTime;

  MotionParams({
    this.xSpeed = 1,
    this.ySpeed = 0,
    this.startTime = 0,
    this.stopTime = 99999,
  });
}

/// Periodic motion artifact parameters.
class PeriodicMotionParams {
  int xAmplitude;
  int yAmplitude;
  int frequency;
  int startTime;
  int stopTime;

  PeriodicMotionParams({
    this.xAmplitude = 5,
    this.yAmplitude = 0,
    this.frequency = 5,
    this.startTime = 0,
    this.stopTime = 99999,
  });
}

/// Main MRI simulation engine.
/// All algorithms faithfully reproduce the Java original (1:1).
class MriSimulator {
  static const int _size = 256;

  /// Calculate an MRI image from parameter maps.
  /// Returns the 12-bit result matrix [x][y] with values 0..4095.
  static List<List<int>> calculate({
    required List<List<int>> t1Matrix,
    required List<List<int>> t2Matrix,
    required List<List<int>> pdMatrix,
    required SequenceType sequence,
    required SequenceParams params,
    ArtifactType artifactType = ArtifactType.none,
    MotionParams? motionParams,
    PeriodicMotionParams? periodicMotionParams,
    void Function(double progress)? onProgress,
  }) {
    // Make mutable copies
    final t1 = _copyMatrix(t1Matrix);
    final t2 = _copyMatrix(t2Matrix);
    final pd = _copyMatrix(pdMatrix);

    // Calculate intensity matrix
    List<List<int>> intensity;
    switch (sequence) {
      case SequenceType.saturationRecovery:
        intensity = _calcSaturationRecovery(t1, pd, params, onProgress);
      case SequenceType.inversionRecovery:
        intensity = _calcInversionRecovery(t1, pd, params, onProgress);
      case SequenceType.spinEcho:
        intensity = _calcSpinEcho(t1, t2, pd, params, onProgress);
      case SequenceType.turboSpinEcho:
        intensity = _calcTurboSpinEcho(t1, t2, pd, params, onProgress);
      case SequenceType.flash:
        intensity = _calcFlash(t1, t2, pd, params, onProgress);
      case SequenceType.geSpoiled:
        intensity = _calcGESpoiled(t1, t2, pd, params, onProgress);
      case SequenceType.geRefocused:
        intensity = _calcGERefocused(t1, t2, pd, params, onProgress);
    }

    // Check intensity matrix (clamp negatives to 0)
    _checkIntensityMatrix(intensity);

    // Filter intensity matrix (median-like filter)
    _filterIntensityMatrix(intensity);

    // Apply artifacts
    if (artifactType == ArtifactType.motion && motionParams != null) {
      intensity = _applyMotionArtifact(intensity, params.tr, motionParams);
    } else if (artifactType == ArtifactType.periodicMotion &&
        periodicMotionParams != null) {
      intensity = _applyPeriodicMotionArtifact(
          intensity, params.tr, periodicMotionParams);
    }

    // Add noise
    _addNoise(intensity, params);

    // Frequency aliasing
    _simulateFreqAliasing(intensity, params.fov, params.freqOversample);

    // Phase aliasing
    _simulatePhAliasing(intensity, params.fov, params.phaseOversample);

    // Scale FOV
    _scaleFOV(intensity, params.fov);

    // Rectangular FOV
    _simulateRectFOV(intensity, params.fov, params.rect, params.phaseOversample);

    // Convert to 12-bit
    return _convertTo12Bit(intensity);
  }

  // ==================== Sequence Calculations ====================

  static List<List<int>> _calcSaturationRecovery(
      List<List<int>> t1, List<List<int>> pd, SequenceParams p,
      void Function(double)? onProgress) {
    final intensity = _newMatrix();
    for (int x = 0; x < _size; x++) {
      onProgress?.call(x / _size);
      for (int y = 0; y < _size; y++) {
        if (t1[x][y] == 0) t1[x][y] = 1;
        intensity[x][y] =
            (pd[x][y] * (1 - exp(-p.tr / t1[x][y].toDouble()))).toInt();
      }
    }
    return intensity;
  }

  static List<List<int>> _calcInversionRecovery(
      List<List<int>> t1, List<List<int>> pd, SequenceParams p,
      void Function(double)? onProgress) {
    final intensity = _newMatrix();
    for (int x = 0; x < _size; x++) {
      onProgress?.call(x / _size);
      for (int y = 0; y < _size; y++) {
        if (t1[x][y] == 0) t1[x][y] = 1;
        // 1:1 from Java - note: bug in original uses -trTime/tiTime instead of -trTime/T1
        intensity[x][y] = (pd[x][y] *
                (1 -
                    2 * exp(-p.ti / t1[x][y].toDouble()) +
                    exp(-p.tr / p.ti.toDouble())))
            .abs()
            .toInt();
      }
    }
    return intensity;
  }

  static List<List<int>> _calcSpinEcho(List<List<int>> t1, List<List<int>> t2,
      List<List<int>> pd, SequenceParams p, void Function(double)? onProgress) {
    final intensity = _newMatrix();
    for (int x = 0; x < _size; x++) {
      onProgress?.call(x / _size);
      for (int y = 0; y < _size; y++) {
        if (t1[x][y] == 0) t1[x][y] = 1;
        if (t2[x][y] == 0) t2[x][y] = 1;
        intensity[x][y] = (pd[x][y] *
                exp(-p.te / t2[x][y].toDouble()) *
                (1 - exp(-p.tr / t1[x][y].toDouble())))
            .toInt();
      }
    }
    return intensity;
  }

  static List<List<int>> _calcTurboSpinEcho(List<List<int>> t1,
      List<List<int>> t2, List<List<int>> pd, SequenceParams p,
      void Function(double)? onProgress) {
    // Prevent division by zero
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        if (t1[x][y] == 0) t1[x][y] = 1;
        if (t2[x][y] == 0) t2[x][y] = 1;
      }
    }

    final finalKSpace = ComplexImage(_size, _size);

    // Determine which echo goes in the center of k-space
    int diff = 100000;
    int bestecho = 9999;
    for (int i = 0; i < p.etl; i++) {
      final hlp = (p.esp * (i + 1) - p.te).abs();
      if (hlp < diff) {
        diff = hlp;
        bestecho = i + 1;
      }
    }

    // Build echo line assignment array
    final echolines = List.filled(_size, 0);
    int uppos = 127;
    int downpos = 128;
    final range = _size ~/ (2 * p.etl);

    for (int i = 0; i <= max(bestecho, p.etl - bestecho); i++) {
      int zeile = 0;
      if ((bestecho + i) <= p.etl) {
        for (zeile = uppos; zeile > uppos - range; zeile--) {
          echolines[zeile] = bestecho + i - 1;
        }
        uppos = zeile;
        for (zeile = downpos; zeile < downpos + range; zeile++) {
          echolines[zeile] = bestecho + i - 1;
        }
      }
      downpos = zeile;
      if (i == 0) continue;
      if ((bestecho - i) > 0) {
        for (zeile = uppos; zeile > uppos - range; zeile--) {
          echolines[zeile] = bestecho - i - 1;
        }
        uppos = zeile;
        for (zeile = downpos; zeile < downpos + range; zeile++) {
          echolines[zeile] = bestecho - i - 1;
        }
        downpos = zeile;
      }
    }

    // For each echo, calculate intensity, FFT, and copy lines to final k-space
    for (int n = 0; n < p.etl; n++) {
      onProgress?.call(n / p.etl);

      final echoImage =
          List.generate(_size, (_) => List.filled(_size, 0.0));

      for (int x = 0; x < _size; x++) {
        for (int y = 0; y < _size; y++) {
          echoImage[x][y] = (pd[x][y] *
              exp(-(n + 1).toDouble() * p.esp / t2[x][y].toDouble()) *
              (1 - exp(-p.tr / t1[x][y].toDouble())));
        }
      }

      // Forward FFT
      final transformed = forwardFFT2D(echoImage, _size);

      // Copy matching lines to final k-space
      for (int x = 0; x < _size; x++) {
        for (int y = 0; y < _size; y++) {
          if (echolines[y] == n) {
            finalKSpace.real[x][y] = transformed.real[x][y];
            finalKSpace.imag[x][y] = transformed.imag[x][y];
          }
        }
      }
    }

    // Inverse FFT
    final result = inverseFFT2D(finalKSpace);

    // Magnitude image
    final intensity = _newMatrix();
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        intensity[x][y] = sqrt(
                result.real[x][y] * result.real[x][y] +
                result.imag[x][y] * result.imag[x][y])
            .toInt();
      }
    }
    return intensity;
  }

  static List<List<int>> _calcFlash(List<List<int>> t1, List<List<int>> t2,
      List<List<int>> pd, SequenceParams p, void Function(double)? onProgress) {
    const gyro = 42.6;
    const deltaB = 19.5e-9;

    final dangel = p.flipAngle * pi / 180;
    final sinangel = sin(dangel);
    // 1:1 from Java: cosangel = Math.sin(dangel) — this is the original bug
    final cosangel = sin(dangel);

    final intensity = _newMatrix();
    for (int x = 0; x < _size; x++) {
      onProgress?.call(x / _size);
      for (int y = 0; y < _size; y++) {
        if (t1[x][y] == 0) t1[x][y] = 1;
        if (t2[x][y] == 0) t2[x][y] = 1;
        final t2x = 1.0 / (1.0 / t2[x][y] + gyro * deltaB);
        final a = exp(-p.tr / t1[x][y].toDouble());
        final b = exp(-p.tr / t2x);
        final c = exp(-p.te / t2x);
        intensity[x][y] =
            (pd[x][y] * (c * (1 - a) * sinangel) / ((1 - a * cosangel) + b * (cosangel - a))).toInt();
      }
    }
    return intensity;
  }

  static List<List<int>> _calcGESpoiled(List<List<int>> t1, List<List<int>> t2,
      List<List<int>> pd, SequenceParams p, void Function(double)? onProgress) {
    const gyro = 42.6;
    const deltaB = 19.5e-9;

    final dangel = p.flipAngle * pi / 180;
    final sinangel = sin(dangel);
    // 1:1 from Java: cosangel = Math.sin(dangel) — same bug
    final cosangel = sin(dangel);

    final intensity = _newMatrix();
    for (int x = 0; x < _size; x++) {
      onProgress?.call(x / _size);
      for (int y = 0; y < _size; y++) {
        if (t1[x][y] == 0) t1[x][y] = 1;
        if (t2[x][y] == 0) t2[x][y] = 1;
        final t2x = 1.0 / (1.0 / t2[x][y] + gyro * deltaB);
        final a = exp(-p.tr / t1[x][y].toDouble());
        final c = exp(-p.te / t2x);
        intensity[x][y] =
            (pd[x][y] * c * (1 - a) * sinangel / (1 - a * cosangel)).toInt();
      }
    }
    return intensity;
  }

  static List<List<int>> _calcGERefocused(List<List<int>> t1,
      List<List<int>> t2, List<List<int>> pd, SequenceParams p,
      void Function(double)? onProgress) {
    const gyro = 42.6;
    const deltaB = 19.5e-9;

    final dangel = p.flipAngle * pi / 180;
    final sinangel = sin(dangel);
    // 1:1 from Java: cosangel = Math.sin(dangel) — same bug
    final cosangel = sin(dangel);

    final intensity = _newMatrix();
    for (int x = 0; x < _size; x++) {
      onProgress?.call(x / _size);
      for (int y = 0; y < _size; y++) {
        if (t1[x][y] == 0) t1[x][y] = 1;
        if (t2[x][y] == 0) t2[x][y] = 1;
        final t2x = 1.0 / (1.0 / t2[x][y] + gyro * deltaB);
        // 1:1 from Java: denominator simplifies trivially but kept as-is
        intensity[x][y] = (pd[x][y] *
                sinangel /
                (1 + (t1[x][y] / t2x) - ((t1[x][y] / t2x) - 1) + cosangel))
            .toInt();
      }
    }
    return intensity;
  }

  // ==================== Post-processing ====================

  static void _checkIntensityMatrix(List<List<int>> m) {
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        if (m[x][y] < 0) m[x][y] = 0;
      }
    }
  }

  static void _filterIntensityMatrix(List<List<int>> m) {
    for (int x = 1; x < _size - 1; x++) {
      for (int y = 1; y < _size - 1; y++) {
        final medianUmgebung = (m[x - 1][y - 1] +
                m[x][y - 1] +
                m[x + 1][y - 1] +
                m[x - 1][y] +
                m[x + 1][y] +
                m[x - 1][y + 1] +
                m[x][y + 1] +
                m[x + 1][y + 1]) ~/
            8;
        int diff = medianUmgebung - m[x][y];
        if (diff < 0) diff = -diff;
        if (diff > 2 * m[x][y]) {
          m[x][y] = medianUmgebung;
        }
      }
    }
  }

  static void _addNoise(List<List<int>> m, SequenceParams params) {
    final random = Random();
    double sum = 0;
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        sum += m[x][y];
      }
    }
    final meanIntensity = sum / (_size * _size);

    double signalNoise = 1.0 - params.snRatio;
    if (signalNoise < 0) signalNoise = 0;
    signalNoise *= 0.10;

    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        double baseNoise;
        switch (params.coil) {
          case 0:
            baseNoise = 0.0;
          case 1:
            baseNoise = meanIntensity * 0.05;
          default:
            baseNoise = 0.0;
        }
        final val = m[x][y];
        final bn = (baseNoise * _nextGaussian(random)).toInt();
        final sn = (val * signalNoise * _nextGaussian(random)).toInt();
        int lVal = val + bn + sn;
        if (lVal < 0) lVal = 0;
        m[x][y] = lVal;
      }
    }
  }

  static double _nextGaussian(Random r) {
    // Box-Muller transform
    double u1, u2;
    do {
      u1 = r.nextDouble();
    } while (u1 == 0);
    u2 = r.nextDouble();
    return sqrt(-2.0 * log(u1)) * cos(2.0 * pi * u2);
  }

  static void _simulateFreqAliasing(
      List<List<int>> m, int freqFOV, bool freqOS) {
    if (!freqOS && freqFOV < 256) {
      final notvisible = 256 - freqFOV;
      final notvisibleDiv2 = notvisible ~/ 2;
      for (int y = 0; y < 256; y++) {
        for (int x = notvisibleDiv2; x < 256 - notvisibleDiv2; x++) {
          final hlpindex1 = x + freqFOV;
          final hlpindex2 = x - freqFOV;
          int hlpval1 = 0;
          int hlpval2 = 0;
          if (hlpindex1 < 256) hlpval1 = m[hlpindex1][y];
          if (hlpindex2 >= 0) hlpval2 = m[hlpindex2][y];
          m[x][y] = m[x][y] + hlpval1 + hlpval2;
        }
      }
    }
  }

  static void _simulatePhAliasing(
      List<List<int>> m, int phFOV, bool phOS) {
    if (!phOS && phFOV < 256) {
      final notvisible = 256 - phFOV;
      final notvisibleDiv2 = notvisible ~/ 2;
      for (int x = 0; x < 256; x++) {
        for (int y = notvisibleDiv2; y < 256 - notvisibleDiv2; y++) {
          final hlpindex1 = y + phFOV;
          final hlpindex2 = y - phFOV;
          int hlpval1 = 0;
          int hlpval2 = 0;
          if (hlpindex1 < 256) hlpval1 = m[x][hlpindex1];
          if (hlpindex2 >= 0) hlpval2 = m[x][hlpindex2];
          m[x][y] = m[x][y] + hlpval1 + hlpval2;
        }
      }
    }
  }

  static void _scaleFOV(List<List<int>> m, int fov) {
    final resultMatrix = _newMatrix();
    final xFOV = (256 - fov) ~/ 2 - 1;
    final yFOV = (256 - fov) ~/ 2 - 1;
    final xScale = 256.0 / fov;
    final yScale = 256.0 / fov;

    for (int y = 0; y < 256; y++) {
      final ys = y / yScale + yFOV;
      for (int x = 0; x < 256; x++) {
        final xs = x / xScale + xFOV;
        resultMatrix[x][y] = _getInterpolatedPixel(xs, ys, m);
      }
    }

    for (int x = 0; x < 256; x++) {
      for (int y = 0; y < 256; y++) {
        m[x][y] = resultMatrix[x][y];
      }
    }
  }

  static int _getInterpolatedPixel(double x, double y, List<List<int>> pixel) {
    final xbase = x.toInt();
    final ybase = y.toInt();
    final xFraction = x - xbase;
    final yFraction = y - ybase;
    try {
      final lowerLeft = pixel[xbase][ybase];
      final lowerRight = pixel[xbase + 1][ybase];
      final upperLeft = pixel[xbase][ybase + 1];
      final upperRight = pixel[xbase + 1][ybase + 1];
      final upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
      final lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);
      return (lowerAverage + yFraction * (upperAverage - lowerAverage)).toInt();
    } catch (_) {
      try {
        return pixel[xbase][ybase];
      } catch (_) {
        return 0;
      }
    }
  }

  static void _simulateRectFOV(
      List<List<int>> m, int fov, int rect, bool phOS) {
    if (rect != 8) {
      final phFOV = fov * rect ~/ 8;
      // Simulate phase aliasing for rect FOV
      _simulatePhAliasing(m, phFOV, phOS);
      // Black out unmeasured parts
      final notvisible = (256 - phFOV) ~/ 2;
      for (int y = 0; y < 256; y++) {
        if (y < notvisible || y >= phFOV + notvisible) {
          for (int x = 0; x < 256; x++) {
            m[x][y] = 0;
          }
        }
      }
    }
  }

  // ==================== Artifacts ====================

  static List<List<int>> _applyMotionArtifact(
      List<List<int>> intensity, int trTime, MotionParams params) {
    const numSteps = 16;
    final kSpace = ComplexImage(_size, _size);

    for (int i = 0; i < numSteps; i++) {
      double t = i * (_size ~/ numSteps) * trTime / 1000.0;
      if (t >= params.stopTime) t = params.stopTime.toDouble();

      int dx = 0, dy = 0;
      if (t >= params.startTime) {
        dx = ((t - params.startTime) / 60 * params.xSpeed).toInt();
        dy = ((t - params.startTime) / 60 * params.ySpeed).toInt();
      }

      final movedImage = _moveImage(intensity, dx, dy);
      final realImage =
          List.generate(_size, (x) =>
              List.generate(_size, (y) => movedImage[x][y].toDouble()));

      final transformed = forwardFFT2D(realImage, _size);

      for (int line = i * (_size ~/ numSteps);
          line < (_size ~/ numSteps) * (i + 1);
          line++) {
        for (int x = 0; x < _size; x++) {
          kSpace.real[x][line] = transformed.real[x][line];
          kSpace.imag[x][line] = transformed.imag[x][line];
        }
      }
    }

    final invImg = inverseFFT2D(kSpace);
    final res = _newMatrix();
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        res[x][y] = sqrt(
                invImg.real[x][y] * invImg.real[x][y] +
                invImg.imag[x][y] * invImg.imag[x][y])
            .toInt();
      }
    }
    return res;
  }

  static List<List<int>> _applyPeriodicMotionArtifact(
      List<List<int>> intensity, int trTime, PeriodicMotionParams params) {
    const numSteps = 16;
    final kSpace = ComplexImage(_size, _size);

    for (int i = 0; i < numSteps; i++) {
      double t = i * (_size ~/ numSteps) * trTime / 1000.0;
      if (t >= params.stopTime) t = params.stopTime.toDouble();

      int dx = 0, dy = 0;
      if (t >= params.startTime) {
        final relTime = (t - params.startTime).toInt();
        final absTime = 60 ~/ params.frequency;
        final absTimeDiv2 = 60 ~/ params.frequency ~/ 2;

        if (absTimeDiv2 > 0) {
          if ((relTime % absTime) < absTimeDiv2) {
            dx = (relTime % absTime) * params.xAmplitude ~/ absTimeDiv2;
            dy = (relTime % absTime) * params.yAmplitude ~/ absTimeDiv2;
          } else {
            dx = ((relTime % absTime) - absTimeDiv2) *
                params.xAmplitude ~/
                absTimeDiv2;
            dy = ((relTime % absTime) - absTimeDiv2) *
                params.yAmplitude ~/
                absTimeDiv2;
          }
        }
      }

      final movedImage = _moveImage(intensity, dx, dy);
      final realImage =
          List.generate(_size, (x) =>
              List.generate(_size, (y) => movedImage[x][y].toDouble()));

      final transformed = forwardFFT2D(realImage, _size);

      for (int line = i * (_size ~/ numSteps);
          line < (_size ~/ numSteps) * (i + 1);
          line++) {
        for (int x = 0; x < _size; x++) {
          kSpace.real[x][line] = transformed.real[x][line];
          kSpace.imag[x][line] = transformed.imag[x][line];
        }
      }
    }

    final invImg = inverseFFT2D(kSpace);
    final res = _newMatrix();
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        res[x][y] = sqrt(
                invImg.real[x][y] * invImg.real[x][y] +
                invImg.imag[x][y] * invImg.imag[x][y])
            .toInt();
      }
    }
    return res;
  }

  static List<List<int>> _moveImage(List<List<int>> img, int h, int v) {
    final moved = _newMatrix();
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        if ((x - h) > 0 && (y - v) > 0 && (x - h) < _size && (y - v) < _size) {
          moved[x][y] = img[x - h][y - v];
        }
      }
    }
    return moved;
  }

  // ==================== 12-Bit Conversion ====================

  static List<List<int>> _convertTo12Bit(List<List<int>> m) {
    const originBitNum = 65536; // 2^16
    const resultBitNum = 4096;  // 2^12

    // Find max
    int maxint = 0;
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        if (m[x][y] > maxint) maxint = m[x][y];
      }
    }

    // Scale down to 16-bit if needed
    if (maxint > originBitNum - 1) {
      final histfactor = originBitNum.toDouble() / maxint;
      for (int x = 0; x < _size; x++) {
        for (int y = 0; y < _size; y++) {
          int hlp = (m[x][y] * histfactor).toInt();
          if (hlp >= 65536) hlp = 65535;
          m[x][y] = hlp;
        }
      }
    }

    // Build histogram
    final histogramm = List.filled(originBitNum, 0);
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        final idx = m[x][y].abs();
        if (idx < originBitNum) histogramm[idx]++;
      }
    }

    const min = 0;
    final maxH = histogramm.length - 1;
    final width = maxH - min;
    int center = (min + maxH) ~/ 2;

    int minImageValue = center - width ~/ 2;
    int maxImageValue = center + width ~/ 2;

    if (center > originBitNum) center = originBitNum;
    if (center < 0) center = 0;

    if (minImageValue < 0) minImageValue = 0;
    if (maxImageValue > originBitNum) maxImageValue = originBitNum;

    final step = resultBitNum / (maxImageValue - minImageValue + 1.0);

    final result = _newMatrix();
    for (int x = 0; x < _size; x++) {
      for (int y = 0; y < _size; y++) {
        int value = m[x][y] - minImageValue;
        if (value < 0) value = 0;
        int mapped = (value * step).toInt();
        if (mapped >= resultBitNum) mapped = resultBitNum - 1;
        result[x][y] = mapped;
      }
    }
    return result;
  }

  // ==================== Helpers ====================

  static List<List<int>> _newMatrix() =>
      List.generate(_size, (_) => List.filled(_size, 0));

  static List<List<int>> _copyMatrix(List<List<int>> src) =>
      List.generate(src.length, (x) => List<int>.from(src[x]));
}
