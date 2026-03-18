import 'package:flutter_test/flutter_test.dart';
import 'package:virtual_mri/vmri/models/exam_data.dart';

void main() {
  group('ExamData.createReferenceData', () {
    late ExamData data;

    setUpAll(() {
      data = ExamData.createReferenceData();
    });

    test('should create 256x256 matrices', () {
      expect(data.width, 256);
      expect(data.height, 256);
      expect(data.t1Matrix.length, 256);
      expect(data.t1Matrix[0].length, 256);
      expect(data.t2Matrix.length, 256);
      expect(data.pdMatrix.length, 256);
    });

    test('should set title to Reference Data', () {
      expect(data.title, 'Reference Data');
    });

    test('should contain non-zero values in test pattern area', () {
      // Fat rectangle at (16,16) with size 32x32: T1=280, T2=50, PD=80
      expect(data.t1Matrix[20][20], 280);
      expect(data.t2Matrix[20][20], 50);
    });

    test('should have zero values in empty areas', () {
      expect(data.t1Matrix[0][0], 0);
      expect(data.t2Matrix[0][0], 0);
      expect(data.pdMatrix[0][0], 0);
    });

    test('should apply PD scaling (x25)', () {
      // Fat at (20,20): PD=80, scaled by 65530*80/100 = 52424, then *25
      // pdMatrix[20][20] = (65530 * 80 / 100) * 25
      final rawPd = (65530 * 80) ~/ 100; // 52424
      expect(data.pdMatrix[20][20], rawPd * 25);
    });

    test('should create Pure Water reference point', () {
      // Pure water at (208,80) size 32x32: T1=4000, T2=4000, PD=100
      expect(data.t1Matrix[210][82], 4000);
      expect(data.t2Matrix[210][82], 4000);
    });

    test('should create cross pattern in fat rectangle', () {
      // Vertical cross at x=63, y=144-239: T1=0
      expect(data.t1Matrix[63][150], 0);
      // Fat rectangle around the cross
      expect(data.t1Matrix[30][150], 280);
    });
  });
}
