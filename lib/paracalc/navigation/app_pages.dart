import 'package:get/get.dart';

import '../pages/calculator/calculator_binding.dart';
import '../pages/calculator/calculator_page.dart';
import 'app_routes.dart';

/// Route definitions for the Parameter Image Calculator app.
class AppPages {
  static final pages = [
    GetPage(
      name: AppRoutes.calculator,
      page: () => const CalculatorPage(),
      binding: CalculatorBinding(),
    ),
  ];
}
