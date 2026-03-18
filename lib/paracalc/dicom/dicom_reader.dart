/// DICOM Part 10 File Reader
///
/// # What is DICOM?
///
/// DICOM (Digital Imaging and Communications in Medicine) is the universal
/// standard for medical images. Every MRI scanner, CT scanner, or X-ray
/// machine produces DICOM files. A single DICOM file contains:
///
/// - **Metadata** (patient name, scan parameters like TR, TE, flip angle, ...)
/// - **Pixel data** (the actual image, typically 16-bit grayscale)
///
/// Think of a DICOM file like a JPEG that also stores all the technical
/// details about how the image was acquired.
///
/// # File structure (DICOM Part 10)
///
/// A DICOM file has this layout:
///
/// ```
/// Bytes 0-127:     Preamble (128 zero bytes, usually ignored)
/// Bytes 128-131:   Magic number "DICM" (identifies the file as DICOM)
/// Bytes 132+:      File Meta Information (always Explicit VR Little Endian)
///                  - Contains Transfer Syntax UID (how the rest is encoded)
/// After meta info: Data Set (the actual metadata + pixel data)
///                  - Encoded according to the Transfer Syntax
/// ```
///
/// # How this library is used
///
/// ```dart
/// final bytes = file.readAsBytesSync();
/// final byteData = ByteData.sublistView(Uint8List.fromList(bytes));
/// final reader = DicomReader(byteData);
/// final info = reader.getImageInfo();  // rows, columns, TR, TE, ...
/// final pixels = reader.getPixelData16();  // raw 16-bit pixel values
/// ```
library;

import 'dart:typed_data';

import 'data_element.dart';
import 'dicom_exception.dart';

/// Well-known DICOM tags used for image parsing.
///
/// Every piece of data in a DICOM file is identified by a **tag** — a pair
/// of two 16-bit numbers (group, element), written as "GGGG,EEEE" in hex.
///
/// For example:
/// - `0028,0010` = Rows (image height)
/// - `0028,0011` = Columns (image width)
/// - `7FE0,0010` = Pixel Data (the actual image bytes)
/// - `0018,0080` = Repetition Time (TR, an MRI scan parameter)
///
/// Group `0002` is the File Meta Information (file-level metadata).
/// Group `0028` contains image description attributes.
/// Group `0018` contains acquisition parameters (MRI-specific values).
/// Group `7FE0` contains the pixel data.
class DicomTag {
  static const String fileMetaInformationGroupLength = '0002,0000';
  static const String mediaStorageSopClassUid = '0002,0002';
  static const String transferSyntaxUid = '0002,0010';
  static const String samplesPerPixel = '0028,0002';
  static const String photometricInterpretation = '0028,0004';
  static const String rows = '0028,0010';
  static const String columns = '0028,0011';
  static const String bitsAllocated = '0028,0100';
  static const String bitsStored = '0028,0101';
  static const String highBit = '0028,0102';
  static const String pixelRepresentation = '0028,0103';
  static const String numberOfFrames = '0028,0008';
  static const String rescaleIntercept = '0028,1052';
  static const String rescaleSlope = '0028,1053';
  static const String windowCenter = '0028,1050';
  static const String windowWidth = '0028,1051';
  static const String pixelData = '7fe0,0010';
  static const String repetitionTime = '0018,0080';
  static const String echoTime = '0018,0081';
  static const String flipAngle = '0018,1314';
  static const String echoTrainLength = '0018,0091';
}

/// Parsed DICOM image metadata.
///
/// This is a convenience class that collects the most important attributes
/// from a DICOM file into a single object. The fields fall into categories:
///
/// **Image dimensions:** [rows], [columns], [numberOfFrames]
///
/// **Pixel encoding:**
/// - [bitsAllocated]: How many bits each pixel occupies (usually 16)
/// - [bitsStored]: How many of those bits carry actual data (e.g., 12)
/// - [pixelRepresentation]: 0 = unsigned, 1 = signed
/// - [photometricInterpretation]: "MONOCHROME1" (bright=low) or
///   "MONOCHROME2" (bright=high)
///
/// **Display mapping:**
/// - [rescaleSlope] / [rescaleIntercept]: Linear transform from stored
///   pixel value to real-world units: `realValue = slope * stored + intercept`
/// - [windowCenter] / [windowWidth]: Suggested brightness/contrast for display
///
/// **MRI acquisition parameters** (used by the Parameter Image Calculator):
/// - [repetitionTime] (TR): Time between successive RF pulses, in ms
/// - [echoTime] (TE): Time between excitation and signal readout, in ms
/// - [flipAngle]: RF pulse tip angle in degrees
class DicomImageInfo {
  final int rows;
  final int columns;
  final int bitsAllocated;
  final int bitsStored;
  final int pixelRepresentation;
  final String photometricInterpretation;
  final int numberOfFrames;
  final double rescaleSlope;
  final double rescaleIntercept;
  final double? windowCenter;
  final double? windowWidth;
  final double? repetitionTime;
  final double? echoTime;
  final double? flipAngle;

  const DicomImageInfo({
    required this.rows,
    required this.columns,
    required this.bitsAllocated,
    required this.bitsStored,
    required this.pixelRepresentation,
    required this.photometricInterpretation,
    this.numberOfFrames = 1,
    this.rescaleSlope = 1.0,
    this.rescaleIntercept = 0.0,
    this.windowCenter,
    this.windowWidth,
    this.repetitionTime,
    this.echoTime,
    this.flipAngle,
  });
}

/// Reads and parses DICOM Part 10 files.
///
/// This is the main entry point for reading DICOM files. The constructor
/// takes the raw file bytes, validates the DICOM preamble, and parses all
/// data elements into a searchable map.
///
/// ## Parsing strategy
///
/// 1. Verify the "DICM" magic number at byte 128
/// 2. Parse the File Meta Information header (group 0002) — this is always
///    encoded as Explicit VR Little Endian, regardless of the actual image data
/// 3. Read the Transfer Syntax UID from the header to determine how the
///    rest of the file is encoded
/// 4. Parse the remaining Data Set using that transfer syntax
///
/// ## Supported transfer syntaxes
///
/// - **Implicit VR Little Endian** (1.2.840.10008.1.2): VR not stored in file,
///   must be looked up from a dictionary. Bytes in little-endian order.
/// - **Explicit VR Little Endian** (1.2.840.10008.1.2.1): VR stored explicitly
///   in each element. Most common modern format.
/// - **Explicit VR Big Endian** (1.2.840.10008.1.2.2): Same as explicit LE but
///   with big-endian byte order. Rare, officially retired since DICOM 2024.
///
/// Compressed transfer syntaxes (JPEG, JPEG2000, etc.) are **not** supported.
///
/// ## Pixel data
///
/// Only 16-bit grayscale (MONOCHROME1 / MONOCHROME2) pixel data is supported,
/// which covers virtually all MRI images. [getPixelData16] returns the raw
/// 16-bit values; [getWindowed8BitData] applies brightness/contrast windowing
/// for display.
class DicomReader {
  final ByteData _bytes;
  final Map<String, DataElement> _elementMap = {};

  /// Creates a new reader by parsing the given DICOM file bytes.
  ///
  /// Throws [NotADicomFileException] if the bytes don't start with a valid
  /// DICOM preamble (128 bytes + "DICM").
  /// Throws [NoFileMetaInformationException] if the File Meta Information
  /// header is missing or malformed.
  DicomReader(this._bytes) {
    _parse();
  }

  /// Check whether [bytes] starts with a valid DICOM preamble.
  ///
  /// Every DICOM Part 10 file begins with 128 bytes (often zeros), followed
  /// by the ASCII string "DICM". This method checks for that signature.
  static bool isDicom(ByteData bytes) {
    if (bytes.lengthInBytes < 132 + 12) return false;
    final preambleEnd = bytes.buffer.asUint8List(128, 4);
    return String.fromCharCodes(preambleEnd) == 'DICM';
  }

  /// Main parse routine. Called once from the constructor.
  ///
  /// The parsing proceeds in three phases:
  ///
  /// 1. **Validate preamble**: Check for "DICM" at byte 128.
  /// 2. **Parse File Meta Information** (group 0002): The first data element
  ///    at byte 132 must be (0002,0000) which tells us the total length of
  ///    the meta information block. This block is always encoded as Explicit
  ///    VR Little Endian — it's the one fixed rule in DICOM. From this block
  ///    we extract the Transfer Syntax UID, which tells us how the rest of
  ///    the file is encoded.
  /// 3. **Parse Data Set**: Everything after the meta information block is
  ///    the actual image metadata and pixel data, encoded according to the
  ///    Transfer Syntax.
  void _parse() {
    if (!isDicom(_bytes)) {
      throw const NotADicomFileException();
    }

    // The first element after the preamble (at byte 132) must be
    // (0002,0000) File Meta Information Group Length. Its value tells us
    // how many bytes the entire meta information block occupies.
    final metaLenElm =
        DataElement(_bytes, 132, TransferSyntax.explicitVRLittleEndian);
    if (metaLenElm.tag != DicomTag.fileMetaInformationGroupLength) {
      throw const NoFileMetaInformationException();
    }

    // 12 = size of the (0002,0000) element itself (4 tag + 2 VR + 2 reserved + 4 length)
    // Wait — actually for UL with explicit VR short form, it's 4+2+2+4=12? No:
    // UL is a short-form VR: 4 (tag) + 2 (VR) + 2 (value length) + 4 (value) = 12 bytes.
    final metaInfoLength = 12 + metaLenElm.getAsUL(_bytes)[0];

    // Parse File Meta Information (always Explicit VR Little Endian)
    _parseSubset(
        128 + 4, metaInfoLength, TransferSyntax.explicitVRLittleEndian);

    // The Transfer Syntax UID (0002,0010) tells us how the Data Set
    // (everything after the meta information) is encoded.
    final tsElm = _elementMap[DicomTag.transferSyntaxUid];
    if (tsElm == null) {
      throw const NoFileMetaInformationException();
    }
    final transferSyntax = tsElm.getAsUI(_bytes)[0];

    // Parse Data Set with the file's actual transfer syntax
    _parseSubset(128 + 4 + metaInfoLength, _bytes.lengthInBytes, transferSyntax);
  }

  /// Parses consecutive data elements from [startOffset] for [length] bytes.
  ///
  /// Each data element is read, stored in [_elementMap] by its tag, and the
  /// offset advances by the element's total length. Parsing stops when we
  /// run out of bytes or encounter a malformed element.
  void _parseSubset(int startOffset, int length, String transferSyntax) {
    final endOffset =
        (startOffset + length).clamp(0, _bytes.lengthInBytes);
    int pos = startOffset;
    while (pos < endOffset) {
      // Safety: stop if not enough bytes for a minimal element header
      if (pos + 8 > _bytes.lengthInBytes) break;
      try {
        final elm = DataElement(_bytes, pos, transferSyntax);
        _elementMap[elm.tag] = elm;
        if (elm.elementLength <= 0) break;
        pos += elm.elementLength;
      } catch (_) {
        break;
      }
    }
  }

  /// Access the raw element map.
  Map<String, DataElement> get elementMap => _elementMap;

  /// Extract image metadata.
  DicomImageInfo getImageInfo() {
    return DicomImageInfo(
      rows: _getUS(DicomTag.rows) ?? 0,
      columns: _getUS(DicomTag.columns) ?? 0,
      bitsAllocated: _getUS(DicomTag.bitsAllocated) ?? 16,
      bitsStored: _getUS(DicomTag.bitsStored) ?? 16,
      pixelRepresentation: _getUS(DicomTag.pixelRepresentation) ?? 0,
      photometricInterpretation:
          _getCS(DicomTag.photometricInterpretation) ?? 'MONOCHROME2',
      numberOfFrames: _getIS(DicomTag.numberOfFrames) ?? 1,
      rescaleSlope: _getDS(DicomTag.rescaleSlope) ?? 1.0,
      rescaleIntercept: _getDS(DicomTag.rescaleIntercept) ?? 0.0,
      windowCenter: _getDS(DicomTag.windowCenter),
      windowWidth: _getDS(DicomTag.windowWidth),
      repetitionTime: _getDS(DicomTag.repetitionTime),
      echoTime: _getDS(DicomTag.echoTime),
      flipAngle: _getDS(DicomTag.flipAngle),
    );
  }

  /// Get raw 16-bit pixel data as a flat list of integer values.
  ///
  /// MRI images are stored as 16-bit values (not 8-bit like JPEG/PNG),
  /// because medical images need a much larger dynamic range. A 12-bit
  /// stored MRI image has values from 0 to 4095.
  ///
  /// The method reads directly from the binary data at the position of the
  /// Pixel Data element (7FE0,0010), respecting the byte order (endianness)
  /// defined by the transfer syntax and whether values are signed or unsigned.
  ///
  /// For multi-frame images, all frames are concatenated into one list.
  List<int> getPixelData16() {
    final elm = _elementMap[DicomTag.pixelData];
    if (elm == null) throw Exception('No pixel data found');
    final info = getImageInfo();
    final pixelCount = info.rows * info.columns * info.numberOfFrames;
    final result = List<int>.filled(pixelCount, 0);
    final signed = info.pixelRepresentation == 1;

    for (int i = 0; i < pixelCount; i++) {
      final offset = elm.startOfValue + i * 2;
      if (offset + 2 > _bytes.lengthInBytes) break;
      if (signed) {
        result[i] = _bytes.getInt16(offset, elm.endian);
      } else {
        result[i] = _bytes.getUint16(offset, elm.endian);
      }
    }
    return result;
  }

  /// Get pixel data as a 2D array [y][x] for a specific frame (0-based).
  List<List<int>> getPixelDataAsMatrix({int frame = 0}) {
    final info = getImageInfo();
    final allPixels = getPixelData16();
    final frameSize = info.rows * info.columns;
    final frameOffset = frame * frameSize;

    return List.generate(info.rows, (y) {
      return List.generate(info.columns, (x) {
        final idx = frameOffset + y * info.columns + x;
        return idx < allPixels.length ? allPixels[idx] : 0;
      });
    });
  }

  /// Get windowed 8-bit grayscale data for display.
  ///
  /// Medical images have a much wider value range than a monitor can display.
  /// A 12-bit image has 4096 gray levels, but a screen shows only 256.
  /// **Windowing** maps a subset of the full range to the displayable 0-255:
  ///
  /// ```
  /// |-----[====WINDOW====]-----|  (full 12-bit range)
  ///        center - w/2  center + w/2
  ///          ↓ maps to 0    ↓ maps to 255
  /// ```
  ///
  /// - Values below the window → black (0)
  /// - Values above the window → white (255)
  /// - Values inside → linearly scaled
  ///
  /// MONOCHROME1 inverts the result (bright values = low pixel values),
  /// which is the traditional X-ray display convention.
  Uint8List getWindowed8BitData({
    double? windowCenter,
    double? windowWidth,
    int frame = 0,
  }) {
    final info = getImageInfo();
    final matrix = getPixelDataAsMatrix(frame: frame);
    final isMonochrome1 =
        info.photometricInterpretation.contains('MONOCHROME1');

    final wc = windowCenter ?? info.windowCenter ?? _autoWindowCenter(info);
    final ww = windowWidth ?? info.windowWidth ?? _autoWindowWidth(info);

    final result = Uint8List(info.rows * info.columns);
    for (int y = 0; y < info.rows; y++) {
      for (int x = 0; x < info.columns; x++) {
        final v = matrix[y][x].toDouble();
        double grey;
        if (v <= wc - 0.5 - (ww - 1) / 2) {
          grey = 0;
        } else if (v > wc - 0.5 + (ww - 1) / 2) {
          grey = 255;
        } else {
          grey = ((v - (wc - 0.5)) / (ww - 1) + 0.5) * 255;
        }
        if (isMonochrome1) grey = 255 - grey;
        result[y * info.columns + x] = grey.clamp(0, 255).toInt();
      }
    }
    return result;
  }

  double _autoWindowCenter(DicomImageInfo info) {
    return (1 << info.bitsStored) / 2.0;
  }

  double _autoWindowWidth(DicomImageInfo info) {
    return (1 << info.bitsStored).toDouble();
  }

  int? _getUS(String tag) {
    final elm = _elementMap[tag];
    if (elm == null) return null;
    final vals = elm.getAsUS(_bytes);
    return vals.isNotEmpty ? vals[0] : null;
  }

  int? _getIS(String tag) {
    final elm = _elementMap[tag];
    if (elm == null) return null;
    try {
      final vals = elm.getAsIS(_bytes);
      return vals.isNotEmpty ? vals[0] : null;
    } catch (_) {
      return null;
    }
  }

  double? _getDS(String tag) {
    final elm = _elementMap[tag];
    if (elm == null) return null;
    try {
      final vals = elm.getAsDS(_bytes);
      return vals.isNotEmpty ? vals[0] : null;
    } catch (_) {
      return null;
    }
  }

  String? _getCS(String tag) {
    final elm = _elementMap[tag];
    if (elm == null) return null;
    final vals = elm.getAsCS(_bytes);
    return vals.isNotEmpty ? vals[0] : null;
  }
}
