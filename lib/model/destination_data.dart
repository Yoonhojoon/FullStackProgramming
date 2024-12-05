import 'package:flutter/material.dart';

class DestinationData extends ChangeNotifier {
  List<Map<String, dynamic>> _destinations = [];

  List<Map<String, dynamic>> get destinations => _destinations;

  // 장소 추가
  void addPlace(Map<String, dynamic> placeData) {
    _destinations.add(placeData);
    notifyListeners();  // 상태 변경을 알려줌
  }
}
