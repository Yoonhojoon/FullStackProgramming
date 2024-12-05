import 'package:flutter/material.dart';

class PlaceData extends ChangeNotifier {
  List<Map<String, dynamic>> _places = [];

  List<Map<String, dynamic>> get places => _places;

  // 장소 추가
  void addPlace(Map<String, dynamic> placeData) {
    _places.add(placeData);
    notifyListeners();  // 상태 변경을 알려줌
  }
}
