/// DICOM Data Element Parser
///
/// # What is a Data Element?
///
/// A DICOM file is essentially a flat sequence of **data elements**. Each
/// element is a tag-length-value triplet:
///
/// ```
/// ┌──────────┬──────────┬──────────────┬──────────────────────┐
/// │ Tag (4B) │ VR (2B)  │ Length (2/4B) │ Value (variable)    │
/// │ group,   │ e.g. US, │ how many      │ actual data bytes   │
/// │ element  │ DS, OW   │ value bytes   │                     │
/// └──────────┴──────────┴──────────────┴──────────────────────┘
/// ```
///
/// - **Tag**: Two 16-bit numbers (group, element) identifying what this
///   data is. Example: (0028,0010) = "Rows" (image height).
/// - **VR** (Value Representation): A 2-letter code describing the data type.
///   Examples: US = unsigned short, DS = decimal string, OW = other word.
///   In Implicit VR transfer syntax, the VR is NOT stored in the file —
///   you'd need a data dictionary to look it up. Our parser handles this
///   by setting VR to an empty string.
/// - **Value Length**: How many bytes the value occupies.
/// - **Value**: The actual data (numbers, strings, pixel bytes, etc.)
///
/// # Transfer Syntax and byte layout
///
/// The exact byte layout of each element depends on the **Transfer Syntax**:
///
/// **Implicit VR Little Endian** (the DICOM default):
/// ```
/// [Tag 4 bytes] [Value Length 4 bytes] [Value ...]
/// ```
/// Total header = 8 bytes. VR is not stored.
///
/// **Explicit VR Little/Big Endian**:
/// For short VRs (US, SS, DS, CS, IS, UI, LO, SH, etc.):
/// ```
/// [Tag 4 bytes] [VR 2 bytes] [Value Length 2 bytes] [Value ...]
/// ```
/// Total header = 8 bytes.
///
/// For long VRs (OB, OW, OF, SQ, UT, UN):
/// ```
/// [Tag 4 bytes] [VR 2 bytes] [Reserved 2 bytes] [Value Length 4 bytes] [Value ...]
/// ```
/// Total header = 12 bytes.
library;

import 'dart:convert';
import 'dart:typed_data';

import 'dicom_exception.dart';

/// Transfer Syntax UIDs supported by the DICOM parser.
///
/// A **Transfer Syntax** defines how a DICOM file encodes its data:
/// - Byte order (little-endian vs. big-endian)
/// - Whether the VR is explicitly stored or implicit
/// - Whether pixel data is compressed (not supported here)
///
/// The Transfer Syntax UID is stored in the File Meta Information header
/// at tag (0002,0010) and applies to the Data Set that follows.
class TransferSyntax {
  /// The DICOM default. VR is not stored in the file, bytes are little-endian.
  static const String implicitVRLittleEndian = '1.2.840.10008.1.2';

  /// The most common modern format. VR is stored, bytes are little-endian.
  static const String explicitVRLittleEndian = '1.2.840.10008.1.2.1';

  /// Rare, officially retired. VR is stored, bytes are big-endian.
  static const String explicitVRBigEndian = '1.2.840.10008.1.2.2';
}

/// Represents a single DICOM Data Element.
///
/// Parses the tag, VR, value length, and value position from raw bytes.
/// Provides typed accessor methods to read the value in the correct format.
///
/// ## How the constructor works
///
/// Given a byte offset and a transfer syntax, the constructor reads the
/// element header and computes:
/// - [tag]: The element's identifier as "GGGG,EEEE" hex string
/// - [vr]: The Value Representation (empty string for Implicit VR)
/// - [startOfValue]: Byte offset where the actual value data begins
/// - [valueLength]: Number of bytes in the value
/// - [elementLength]: Total bytes consumed by this element (header + value)
/// - [endian]: Byte order for reading multi-byte values
///
/// ## Value accessors
///
/// The class provides type-safe accessors like [getAsUS], [getAsDS], [getAsUI]
/// to read the raw bytes as the appropriate Dart types. Which accessor to use
/// depends on the VR:
///
/// | VR | Accessor | Dart type | Example use |
/// |----|----------|-----------|-------------|
/// | US | [getAsUS] | `List<int>` | Rows, Columns |
/// | SS | [getAsSS] | `List<int>` | Signed pixel values |
/// | UL | [getAsUL] | `List<int>` | Group lengths |
/// | DS | [getAsDS] | `List<double>` | TR, TE, Flip Angle |
/// | IS | [getAsIS] | `List<int>` | Number of Frames |
/// | UI | [getAsUI] | `List<String>` | Transfer Syntax UID |
/// | CS | [getAsCS] | `List<String>` | Photometric Interpretation |
/// | LO | [getAsLO] | `List<String>` | Patient Name |
///
/// Reference: DICOM Part 5, Chapter 7 — The Data Set.
class DataElement {
  /// Byte offset where this element starts in the file.
  final int startOfElement;

  /// Byte order for reading multi-byte values (little or big endian).
  late final Endian endian;

  /// Total number of bytes this element occupies (header + value).
  late final int elementLength;

  /// Element tag as "GGGG,EEEE" hex string (e.g., "0028,0010" for Rows).
  late final String tag;

  /// Value Representation — a 2-letter code like "US", "DS", "OW".
  /// Empty string for Implicit VR transfer syntax.
  late final String vr;

  /// Byte offset where the value data begins.
  late final int startOfValue;

  /// Number of bytes in the value field.
  late final int valueLength;

  /// Parses a single data element from [bytes] at [startOfElement].
  ///
  /// The [transferSyntax] determines how bytes are laid out (see class doc).
  DataElement(ByteData bytes, this.startOfElement, String transferSyntax) {
    switch (transferSyntax) {
      // Implicit VR: No VR stored in the file.
      // Layout: [Tag 4B] [Value Length 4B] [Value ...]
      case TransferSyntax.implicitVRLittleEndian:
        endian = Endian.little;
        vr = '';
        valueLength = bytes.getUint32(startOfElement + 4, endian);
        startOfValue = startOfElement + 8;
        elementLength = valueLength + 8;
        break;

      // Explicit VR: VR is stored as 2 ASCII characters after the tag.
      // The header size depends on whether the VR is "short" or "long".
      case TransferSyntax.explicitVRLittleEndian:
      case TransferSyntax.explicitVRBigEndian:
        endian = (transferSyntax == TransferSyntax.explicitVRLittleEndian)
            ? Endian.little
            : Endian.big;
        vr = _toAscii(bytes, startOfElement + 4, 2);
        switch (vr) {
          // Long-form VRs: these can hold very large values (e.g., pixel data).
          // Layout: [Tag 4B] [VR 2B] [Reserved 2B] [Value Length 4B] [Value ...]
          case 'OB': // Other Byte — raw bytes
          case 'OW': // Other Word — raw 16-bit words (pixel data!)
          case 'OF': // Other Float
          case 'SQ': // Sequence of Items
          case 'UT': // Unlimited Text
          case 'UN': // Unknown
            valueLength = bytes.getUint32(startOfElement + 8, endian);
            startOfValue = startOfElement + 12;
            elementLength = 12 + valueLength;
            break;
          // Short-form VRs: most common metadata types.
          // Layout: [Tag 4B] [VR 2B] [Value Length 2B] [Value ...]
          default:
            valueLength = bytes.getUint16(startOfElement + 6, endian);
            startOfValue = startOfElement + 8;
            elementLength = 8 + valueLength;
            break;
        }
        break;

      default:
        throw const TransferSyntaxNotSupportedException();
    }

    final group = bytes.getUint16(startOfElement, endian);
    final element = bytes.getUint16(startOfElement + 2, endian);
    tag =
        '${group.toRadixString(16).padLeft(4, '0')},${element.toRadixString(16).padLeft(4, '0')}';
  }

  /// Read value as Unsigned Short (US): 16-bit unsigned integers.
  ///
  /// Used for image dimensions (Rows, Columns), Bits Allocated, etc.
  /// A single US value occupies 2 bytes; multi-valued elements return
  /// multiple integers.
  List<int> getAsUS(ByteData bytes) {
    final result = <int>[];
    if (valueLength < 2 || valueLength.remainder(2) != 0) return result;
    final n = valueLength ~/ 2;
    for (int i = 0; i < n; i++) {
      result.add(bytes.getUint16(startOfValue + i * 2, endian));
    }
    return result;
  }

  /// Read value as Signed Short (SS): 16-bit signed integers (-32768..32767).
  List<int> getAsSS(ByteData bytes) {
    final result = <int>[];
    if (valueLength < 2 || valueLength.remainder(2) != 0) return result;
    final n = valueLength ~/ 2;
    for (int i = 0; i < n; i++) {
      result.add(bytes.getInt16(startOfValue + i * 2, endian));
    }
    return result;
  }

  /// Read value as Unsigned Long (UL): 32-bit unsigned integers.
  ///
  /// Used for Group Length elements (e.g., File Meta Information length).
  List<int> getAsUL(ByteData bytes) {
    final result = <int>[];
    if (valueLength < 4 || valueLength.remainder(4) != 0) return result;
    final n = valueLength ~/ 4;
    for (int i = 0; i < n; i++) {
      result.add(bytes.getUint32(startOfValue + i * 4, endian));
    }
    return result;
  }

  /// Read value as Unique Identifier (UI): UID string.
  ///
  /// UIDs are dot-separated numeric strings like "1.2.840.10008.1.2.1".
  /// DICOM pads them with a trailing null byte (\0) which is stripped here.
  /// Multiple UIDs are separated by backslash (\\).
  List<String> getAsUI(ByteData bytes) {
    int len = valueLength;
    if (len > 0 && bytes.getUint8(startOfValue + len - 1) == 0) {
      len--;
    }
    return _toAscii(bytes, startOfValue, len).split('\\');
  }

  /// Read value as Code String (CS): short uppercase keyword strings.
  ///
  /// Used for enumerated values like Photometric Interpretation
  /// ("MONOCHROME1", "MONOCHROME2") or Modality ("MR", "CT").
  /// Values are trimmed of trailing spaces and split by backslash.
  List<String> getAsCS(ByteData bytes) {
    return _toAscii(bytes, startOfValue, valueLength)
        .split('\\')
        .map((s) => s.trim())
        .toList();
  }

  /// Read value as Decimal String (DS): numeric values stored as ASCII text.
  ///
  /// DICOM stores many floating-point values (TR, TE, Flip Angle, Window
  /// Center/Width) as human-readable ASCII strings rather than binary floats.
  /// Example: the bytes `"1500.0"` represent TR = 1500.0 ms.
  /// Multiple values are separated by backslash (\\).
  List<double> getAsDS(ByteData bytes) {
    return _toAscii(bytes, startOfValue, valueLength)
        .split('\\')
        .where((s) => s.trim().isNotEmpty)
        .map((s) => double.parse(s.trim()))
        .toList();
  }

  /// Read value as Integer String (IS): integers stored as ASCII text.
  ///
  /// Similar to DS but for integer values. Used for Number of Frames, etc.
  List<int> getAsIS(ByteData bytes) {
    return _toAscii(bytes, startOfValue, valueLength)
        .split('\\')
        .where((s) => s.trim().isNotEmpty)
        .map((s) => int.parse(s.trim()))
        .toList();
  }

  /// Read value as Long String (LO): text up to 64 characters.
  ///
  /// Used for descriptive text fields like Institution Name, Patient ID, etc.
  List<String> getAsLO(ByteData bytes) {
    return _toAscii(bytes, startOfValue, valueLength)
        .split('\\')
        .map((s) => s.trim())
        .toList();
  }

  /// Decode [length] bytes at [offset] as ASCII text.
  ///
  /// DICOM text values are stored as ASCII (or in rare cases, other character
  /// sets). Invalid bytes are replaced rather than causing an error.
  static String _toAscii(ByteData bytes, int offset, int length) {
    final list = bytes.buffer.asUint8List(offset, length);
    return const AsciiCodec().decode(list, allowInvalid: true);
  }
}
