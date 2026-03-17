import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/services.dart' show rootBundle;

/// Holds the T1, T2, and PD parameter maps for an MRI examination.
class ExamData {
  final String title;
  final String descriptionHtml;
  final int width;
  final int height;
  final List<List<int>> t1Matrix;
  final List<List<int>> t2Matrix;
  final List<List<int>> pdMatrix;

  ExamData({
    required this.title,
    required this.descriptionHtml,
    required this.width,
    required this.height,
    required this.t1Matrix,
    required this.t2Matrix,
    required this.pdMatrix,
  });

  /// Load exam data from a JSON asset file.
  /// The JSON contains base64-encoded big-endian uint16 arrays.
  static Future<ExamData> loadFromAsset(String assetPath) async {
    final jsonStr = await rootBundle.loadString(assetPath);
    final map = json.decode(jsonStr) as Map<String, dynamic>;

    final width = map['width'] as int;
    final height = map['height'] as int;

    return ExamData(
      title: map['title'] as String,
      descriptionHtml: map['description_html'] as String,
      width: width,
      height: height,
      t1Matrix: _decodeMatrix(map['t1'] as String, width, height),
      t2Matrix: _decodeMatrix(map['t2'] as String, width, height),
      pdMatrix: _decodeMatrix(map['pd'] as String, width, height),
    );
  }

  static List<List<int>> _decodeMatrix(String b64, int width, int height) {
    final bytes = base64Decode(b64);
    final data = ByteData.sublistView(Uint8List.fromList(bytes));
    final matrix = List.generate(width, (_) => List.filled(height, 0));

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        // Data is stored row-major: pixel at (x, y) = data[y * width + x]
        // as big-endian uint16
        final offset = (y * width + x) * 2;
        matrix[x][y] = data.getUint16(offset, Endian.big);
      }
    }
    return matrix;
  }

  /// Generate the built-in reference data set (geometric test pattern).
  static ExamData createReferenceData() {
    const size = 256;
    final t1 = List.generate(size, (_) => List.filled(size, 0));
    final t2 = List.generate(size, (_) => List.filled(size, 0));
    final pd = List.generate(size, (_) => List.filled(size, 0));

    void drawRect(int x, int y, int b, int h, int t1v, int t2v, int pdv) {
      final clampedPd = pdv > 100 ? 100 : pdv;
      for (int ix = x; ix < x + b; ix++) {
        for (int iy = y; iy < y + h; iy++) {
          t1[ix][iy] = t1v;
          t2[ix][iy] = t2v;
          pd[ix][iy] = (65530 * clampedPd) ~/ 100;
        }
      }
    }

    // Test pattern matching Java loadReferenceData()
    drawRect(16, 16, 32, 32, 280, 50, 80);     // Fat
    drawRect(80, 16, 32, 32, 460, 106, 86);    // Methemoglobin
    drawRect(144, 16, 32, 32, 510, 67, 54);    // White matter
    drawRect(208, 16, 32, 32, 760, 77, 62);    // Gray matter
    drawRect(16, 80, 32, 32, 900, 126, 77);    // Brain edema
    drawRect(80, 80, 32, 32, 1080, 215, 100);  // Cyst
    drawRect(144, 80, 32, 32, 2650, 280, 89);  // CSF
    drawRect(208, 80, 32, 32, 4000, 4000, 100);// Pure water
    // Rectangle with fat and 3px cross
    drawRect(16, 144, 96, 96, 280, 50, 80);
    drawRect(63, 144, 3, 96, 0, 0, 0);
    drawRect(16, 191, 96, 3, 0, 0, 0);
    // Rectangle with water and 3px dot
    drawRect(144, 144, 96, 96, 4000, 4000, 100);
    drawRect(191, 191, 3, 3, 0, 0, 0);

    // PD scaling (like FileLoader: pdMatrix *= 25)
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        pd[x][y] *= 25;
      }
    }

    return ExamData(
      title: 'Reference Data',
      descriptionHtml: '<p>Built-in reference test pattern</p>',
      width: size,
      height: size,
      t1Matrix: t1,
      t2Matrix: t2,
      pdMatrix: pd,
    );
  }
}
