import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'navigation/app_pages.dart';
import 'navigation/app_routes.dart';

void main() {
  runApp(const VirtualMriApp());
}

class VirtualMriApp extends StatelessWidget {
  const VirtualMriApp({super.key});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      title: 'Virtual MRI Scanner',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blueGrey),
        useMaterial3: true,
      ),
      initialRoute: AppRoutes.mriScreen,
      getPages: AppPages.pages,
    );
  }
}
