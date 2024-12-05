import 'package:flutter/material.dart';
import 'category/travel_category.dart';
import 'popular_travel.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '윤호준님,~~)',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          TravelCategory(), // 여행 컨셉
          const SizedBox(height: 16),
          PopularTravel(), // 요즘 핫한 여행루트
        ],
      ),
    );
  }
}
