import 'package:flutter/material.dart';
import 'entity/DailyPlan.dart';

class DailyPlanProvider extends ChangeNotifier {
  List<DailyPlan> _plans = [];

  List<DailyPlan> get plans => _plans;

  void setPlans(List<DailyPlan> plans) {
    _plans = plans;
    notifyListeners();
  }

  void updatePlans(List<DailyPlan> newPlans) {
    _plans = newPlans;
    notifyListeners();  // UI 업데이트를 위해 리스너들에게 알림
  }
}