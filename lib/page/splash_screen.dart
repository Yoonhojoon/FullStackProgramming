import 'package:flutter/material.dart';
import 'package:mytour/page/login_page.dart';

class SplashScreen extends StatelessWidget {
  const SplashScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 1초 후에 LoginPage로 이동
    Future.delayed(const Duration(seconds: 2), () {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const LoginPage()),
      );
    });

    return MaterialApp(
      theme: ThemeData.light().copyWith(
        scaffoldBackgroundColor: Colors.white,
      ),
      home: Scaffold(
        body: Center(
          child: ListView(
            shrinkWrap: true,
            children: [
              Image3(),
            ],
          ),
        ),
      ),
    );
  }
}

class Image3 extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          width: 362,
          height: 300,
          decoration: BoxDecoration(
            image: DecorationImage(
              image: AssetImage('assets/images/icon.png'),
              fit: BoxFit.fill,
            ),
          ),
        ),
      ],
    );
  }
}
