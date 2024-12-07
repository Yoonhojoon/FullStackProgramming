import 'package:flutter/material.dart';
import 'package:mytour/entity/ScheduleItem.dart';
import 'entity/DailyPlan.dart';
import 'entity/DailySchedule.dart';

class DailyPlanProvider extends ChangeNotifier {
  List<DailySchedule> _schedules = [];

  List<DailySchedule> get schedules => _schedules;

  void setSchedules(List<DailySchedule> schedules) {
    _schedules = schedules;
    notifyListeners();
  }

  void updateSchedules(List<DailySchedule> newSchedules) {
    _schedules = newSchedules;
    notifyListeners();
  }

  // 특정 아이템의 시간을 업데이트하는 메서드 추가
  void updateItemTime(ScheduleItem targetItem, DateTime newStartTime, Duration newDuration) {
    for (var schedule in _schedules) {
      final index = schedule.items.indexWhere((item) =>
      item.tripItem.name == targetItem.tripItem.name &&
          item.tripItem.address == targetItem.tripItem.address
      );

      if (index != -1) {
        schedule.items[index].updateTimes(
          newStart: newStartTime,
          newDuration: newDuration,
        );
        notifyListeners();
        return;
      }
    }
  }
}