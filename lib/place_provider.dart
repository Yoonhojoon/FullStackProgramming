import 'package:flutter/material.dart';

class PlaceProvider with ChangeNotifier {
  List<Map<String, dynamic>> _places = [];

  List<Map<String, dynamic>> get places => _places;

  void addPlace(Map<String, dynamic> place) {
    _places.add(place);
    notifyListeners();  // 상태 변경 시 UI 갱신
  }

  void removePlace(Map<String, dynamic> place) {
    _places.remove(place);
    notifyListeners();  // 상태 변경 시 UI 갱신
  }
}
