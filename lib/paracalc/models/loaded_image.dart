import '../dicom/dicom_reader.dart';

/// Loaded DICOM image with metadata.
class LoadedImage {
  final String fileName;
  final DicomReader reader;
  final List<List<int>> pixelData;

  LoadedImage({
    required this.fileName,
    required this.reader,
    required this.pixelData,
  });
}
