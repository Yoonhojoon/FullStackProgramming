import 'package:flutter/material.dart';

class TravelCategory extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              '여행 컨셉 😊',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            TextButton(
              onPressed: () {},
              child: const Text('See all'),
            ),
          ],
        ),
        const SizedBox(height: 8),
        SizedBox(
          height: 80,
          child: ListView(
            scrollDirection: Axis.horizontal,
            children: [
              _buildConceptItem('호캉스', 'assets/images/hotel.jpg'),
              _buildConceptItem('자연', 'assets/images/nature.jpg'),
              _buildConceptItem('가족', 'assets/images/family.jpg'),
              _buildConceptItem('데이트', 'assets/images/date.jpg'),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildConceptItem(String label, String imagePath) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8.0),
      child: Column(
        children: [
          CircleAvatar(
            radius: 30,
            backgroundImage: AssetImage(imagePath),
          ),
          const SizedBox(height: 4),
          Text(label),
        ],
      ),
    );
  }
}
