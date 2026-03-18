import 'package:flutter_test/flutter_test.dart';
import 'package:virtual_mri/paracalc/dicom/dicom_exception.dart';

void main() {
  group('DICOM Exceptions', () {
    test('should create NotADicomFileException with default message', () {
      final ex = NotADicomFileException();
      expect(ex.toString(), contains('Not a DICOM file'));
    });

    test('should create NotADicomFileException with custom message', () {
      final ex = NotADicomFileException('test.dat');
      expect(ex.toString(), contains('test.dat'));
    });

    test('should create TransferSyntaxNotSupportedException', () {
      final ex = TransferSyntaxNotSupportedException('1.2.3.4');
      expect(ex.toString(), contains('1.2.3.4'));
    });

    test('should create NoFileMetaInformationException', () {
      final ex = NoFileMetaInformationException();
      expect(ex.toString(), contains('No File Meta Information'));
    });
  });
}
