import 'package:flutter/material.dart';

class DestinationProvider with ChangeNotifier {
  List<Map<String, dynamic>> _destinations = [];

  List<Map<String, dynamic>> get destinations => _destinations;

  void addDestination(Map<String, dynamic> destination) {
    _destinations.add(destination);
    notifyListeners();
  }

  void removeDestination(Map<String, dynamic> destination) {
    _destinations.remove(destination);
    notifyListeners();
  }
}