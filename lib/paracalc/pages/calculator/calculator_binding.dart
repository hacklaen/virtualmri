import 'package:get/get.dart';

import 'calculator_controller.dart';

/// Dependency injection for the calculator screen.
class CalculatorBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<CalculatorController>(() => CalculatorController());
  }
}
