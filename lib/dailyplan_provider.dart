import 'package:flutter/material.dart';
import 'entity/DailyPlan.dart';

class DailyPlanProvider extends ChangeNotifier {
  List<DailyPlan> _plans = [];

  List<DailyPlan> get plans => _plans;

  void setPlans(List<DailyPlan> plans) {
    _plans = plans;
    notifyListeners();
  }
}