import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'navigation/app_pages.dart';
import 'navigation/app_routes.dart';

void main() {
  runApp(const ParameterImageCalculatorApp());
}

class ParameterImageCalculatorApp extends StatelessWidget {
  const ParameterImageCalculatorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      title: 'Parameter Image Calculator',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorSchemeSeed: Colors.blueGrey,
        useMaterial3: true,
        brightness: Brightness.light,
      ),
      darkTheme: ThemeData(
        colorSchemeSeed: Colors.blueGrey,
        useMaterial3: true,
        brightness: Brightness.dark,
      ),
      initialRoute: AppRoutes.calculator,
      getPages: AppPages.pages,
    );
  }
}
