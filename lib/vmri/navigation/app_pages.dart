import 'package:get/get.dart';

import '../pages/mri_screen/mri_screen_binding.dart';
import '../pages/mri_screen/mri_screen_page.dart';
import 'app_routes.dart';

/// Route definitions for the Virtual MRI Scanner app.
class AppPages {
  static final pages = [
    GetPage(
      name: AppRoutes.mriScreen,
      page: () => const MriScreenPage(),
      binding: MriScreenBinding(),
    ),
  ];
}
