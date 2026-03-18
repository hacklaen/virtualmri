import 'dart:async';
import 'dart:js_interop';
import 'dart:typed_data';

import 'package:web/web.dart' as web;

import '../dicom/dicom_reader.dart';
import '../models/loaded_image.dart';

/// Handles DICOM file loading via the browser file picker (Web only).
class DicomLoader {
  /// Opens the browser file picker and loads selected DICOM files.
  static Future<List<LoadedImage>> pickAndLoadFiles() async {
    final completer = Completer<List<LoadedImage>>();

    final input = web.HTMLInputElement()
      ..type = 'file'
      ..multiple = true
      ..accept = '.dcm,.dicom,.DCM,.DICOM,*/*';

    input.addEventListener(
      'change',
      (web.Event event) {
        _handleFileSelection(input, completer);
      }.toJS,
    );

    input.click();
    return completer.future;
  }

  static Future<void> _handleFileSelection(
    web.HTMLInputElement input,
    Completer<List<LoadedImage>> completer,
  ) async {
    final files = input.files;
    if (files == null || files.length == 0) {
      completer.complete([]);
      return;
    }

    final results = <LoadedImage>[];
    for (int i = 0; i < files.length; i++) {
      final file = files.item(i);
      if (file == null) continue;
      try {
        final loaded = await _loadFile(file);
        results.add(loaded);
      } catch (e) {
        if (results.isEmpty && files.length == 1) {
          completer.completeError(e);
          return;
        }
      }
    }
    completer.complete(results);
  }

  static Future<LoadedImage> _loadFile(web.File file) async {
    final reader = web.FileReader();
    final completer = Completer<Uint8List>();

    reader.addEventListener(
      'loadend',
      (web.Event event) {
        final result = reader.result;
        if (result == null) {
          completer.completeError(Exception('File read returned null'));
          return;
        }
        final buffer = result as JSArrayBuffer;
        final bytes = buffer.toDart.asUint8List();
        completer.complete(bytes);
      }.toJS,
    );

    reader.addEventListener(
      'error',
      (web.Event event) {
        completer.completeError(
            Exception('Failed to read file: ${file.name}'));
      }.toJS,
    );

    reader.readAsArrayBuffer(file);
    final bytes = await completer.future;
    final byteData = ByteData.sublistView(bytes);

    final dicomReader = DicomReader(byteData);
    final pixelData = dicomReader.getPixelDataAsMatrix();

    return LoadedImage(
      fileName: file.name,
      reader: dicomReader,
      pixelData: pixelData,
    );
  }
}
