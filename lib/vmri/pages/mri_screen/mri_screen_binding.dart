import 'package:get/get.dart';

import 'mri_screen_controller.dart';

/// Dependency injection for the MRI screen.
class MriScreenBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<MriScreenController>(() => MriScreenController());
  }
}
