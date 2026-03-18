/// Linear least squares fit: y(x) = m * x + b
///
/// Uses Gaussian elimination with pivot search.
/// Ported from the Java original by Christian Schalla & Andreas Truemper.
class LeastSquare {
  late List<List<double>> _samples;
  late List<double> _weights;
  late List<List<double>> _matrix;
  late List<List<double>> _vectors;
  late List<double> _solution;

  /// Set the matrix of input pairs [x, y].
  void setSamples(List<List<double>> samples) {
    _samples = samples;
  }

  /// Set the weight vector for the input pairs.
  void setWeights(List<double> weights) {
    _weights = weights;
  }

  /// Normalize weights so they sum to 1.
  void normalizeWeights() {
    double sum = 0;
    for (final w in _weights) {
      sum += w;
    }
    if (sum == 0) return;
    for (int i = 0; i < _weights.length; i++) {
      _weights[i] /= sum;
    }
  }

  /// Perform the least squares calculation.
  void calculate() {
    _createLeastSquareMatrix();
    _eliminate();
    _substitute();
  }

  /// Returns \[b, m\] where y = m * x + b.
  List<double> get solution => _solution;

  void _createLeastSquareMatrix() {
    _matrix = List.generate(2, (_) => List.filled(3, 0.0));
    const m = 2;
    final n = _samples.length;

    _fillVectors();

    for (int i = 0; i < m; i++) {
      for (int j = 0; j < m + 1; j++) {
        double t = 0;
        for (int k = 0; k < n; k++) {
          t += _vectors[i][k] * _vectors[j][k] * _weights[k];
        }
        _matrix[i][j] = t;
      }
    }
  }

  void _fillVectors() {
    _vectors = List.generate(3, (_) => List.filled(_samples.length, 0.0));

    // x^0 = 1
    for (int i = 0; i < _samples.length; i++) {
      _vectors[0][i] = 1;
    }

    // x values
    for (int i = 0; i < _samples.length; i++) {
      _vectors[1][i] = _samples[i][0];
    }

    // y values
    for (int i = 0; i < _samples.length; i++) {
      _vectors[2][i] = _samples[i][1];
    }
  }

  void _eliminate() {
    const n = 2;
    for (int i = 0; i < n; i++) {
      int maxIdx = i;
      for (int j = i + 1; j < n; j++) {
        if (_matrix[j][i].abs() > _matrix[maxIdx][i].abs()) {
          maxIdx = j;
        }
      }
      // Swap rows
      for (int k = i; k < n + 1; k++) {
        final t = _matrix[i][k];
        _matrix[i][k] = _matrix[maxIdx][k];
        _matrix[maxIdx][k] = t;
      }
      // Eliminate
      for (int j = i + 1; j < n; j++) {
        if (_matrix[i][i] == 0) continue;
        for (int k = n; k >= i; k--) {
          _matrix[j][k] -=
              _matrix[i][k] * _matrix[j][i] / _matrix[i][i];
        }
      }
    }
  }

  void _substitute() {
    const n = 2;
    _solution = List.filled(2, 0.0);
    for (int j = n - 1; j >= 0; j--) {
      double t = 0;
      for (int k = j + 1; k < n; k++) {
        t += _matrix[j][k] * _solution[k];
      }
      if (_matrix[j][j] != 0) {
        _solution[j] = (_matrix[j][n] - t) / _matrix[j][j];
      }
    }
  }
}
