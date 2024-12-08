import 'package:flutter/material.dart';

class DestinationProvider with ChangeNotifier {
  List<Map<String, dynamic>> _destinations = [];

  List<Map<String, dynamic>> get destinations => _destinations;

  void addDestination(Map<String, dynamic> destination) {
    _destinations.add(destination);
    notifyListeners();
  }

  void removeDestinations(List<int> indices) {
    // 인덱스를 내림차순으로 정렬 (뒤에서부터 삭제하기 위해)
    indices.sort((a, b) => b.compareTo(a));

    // 선택된 인덱스의 장소들을 삭제
    for (int index in indices) {
      if (index < destinations.length) {
        destinations.removeAt(index);
      }
    }
    notifyListeners();
  }
}