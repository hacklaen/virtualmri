import 'package:flutter/material.dart';

import 'widgets/mri_screen.dart';

void main() {
  runApp(const VirtualMriApp());
}

class VirtualMriApp extends StatelessWidget {
  const VirtualMriApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Virtual MRI Scanner',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blueGrey),
        useMaterial3: true,
      ),
      home: const MriScreen(),
    );
  }
}
