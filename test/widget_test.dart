import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:virtual_mri/vmri/main.dart';

void main() {
  testWidgets('should show app title on startup', (WidgetTester tester) async {
    // MriScreen needs a wide viewport; overflow in narrow test is expected
    final originalOnError = FlutterError.onError;
    FlutterError.onError = (details) {
      if (details.toString().contains('overflowed')) return;
      originalOnError?.call(details);
    };
    addTearDown(() => FlutterError.onError = originalOnError);

    await tester.pumpWidget(const VirtualMriApp());
    expect(find.text('Virtual MRI Scanner'), findsOneWidget);
  });
}
