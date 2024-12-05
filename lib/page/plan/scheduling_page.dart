import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:mytour/place_provider.dart';  // PlaceProvider import

class SchedulingPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // PlaceProvider에서 places 배열 가져오기
    final places = Provider.of<PlaceProvider>(context).places;

    return Scaffold(
      appBar: AppBar(title: Text('여행 리스트')),
      body: ListView.builder(
        itemCount: places.length,
        itemBuilder: (context, index) {
          final place = places[index];
          return ListTile(
            title: Text(place['name'] ?? '이름 없음'),
            subtitle: Text(place['address'] ?? '주소 없음'),
          );
        },
      ),
    );
  }
}
