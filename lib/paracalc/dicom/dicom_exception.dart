/// Exception thrown when the file lacks the DICOM signature.
///
/// A valid DICOM Part 10 file must have the ASCII string "DICM" at byte
/// offset 128. If this signature is missing, the file is either not a DICOM
/// file at all, or it uses the legacy format without preamble (not supported).
class NotADicomFileException implements Exception {
  final String message;
  const NotADicomFileException([this.message = 'Not a DICOM file']);
  @override
  String toString() => 'NotADicomFileException: $message';
}

/// Exception thrown when the File Meta Information header is missing.
///
/// The File Meta Information (group 0002) is the first section after the
/// DICOM preamble. It must start with (0002,0000) Group Length and contain
/// (0002,0010) Transfer Syntax UID. Without it, the parser cannot determine
/// how to read the rest of the file.
class NoFileMetaInformationException implements Exception {
  final String message;
  const NoFileMetaInformationException(
      [this.message = 'No File Meta Information found']);
  @override
  String toString() => 'NoFileMetaInformationException: $message';
}

/// Exception thrown when the Transfer Syntax is not supported.
///
/// This library supports only uncompressed transfer syntaxes (Implicit VR
/// Little Endian, Explicit VR Little Endian, Explicit VR Big Endian).
/// Compressed formats like JPEG, JPEG 2000, or RLE are not supported.
class TransferSyntaxNotSupportedException implements Exception {
  final String message;
  const TransferSyntaxNotSupportedException(
      [this.message = 'Transfer Syntax not supported']);
  @override
  String toString() => 'TransferSyntaxNotSupportedException: $message';
}
