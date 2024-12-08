import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:mytour/entity/ScheduleItem.dart';
import 'package:share_plus/share_plus.dart';
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

  void deleteAllSchedules() {
    _schedules = [];
    notifyListeners();
  }

  void updateItemTimeWithCascade(ScheduleItem currentItem, DateTime newStartTime, Duration duration) {
    int currentIndex = -1;
    DailySchedule? targetSchedule;

    // 현재 아이템이 속한 스케줄과 인덱스 찾기
    for (var schedule in schedules) {
      currentIndex = schedule.items.indexOf(currentItem);
      if (currentIndex != -1) {
        targetSchedule = schedule;
        break;
      }
    }

    if (targetSchedule == null) return;

    // 현재 아이템부터 순차적으로 시간 업데이트
    DateTime nextStartTime = newStartTime;

    for (int i = currentIndex; i < targetSchedule.items.length; i++) {
      var item = targetSchedule.items[i];

      // 현재 아이템 시간 업데이트
      if (i == currentIndex) {
        item.updateTimes(
            newStart: nextStartTime,
            newDuration: duration
        );
      } else {
        // 이전 아이템의 종료 시간 + 이동 시간을 시작 시간으로
        item.updateTimes(
            newStart: nextStartTime,
            newDuration: item.endTime.difference(item.startTime)  // 기존 체류 시간 유지
        );
      }

      // 다음 아이템의 시작 시간 계산
      if (i < targetSchedule.items.length - 1) {
        nextStartTime = item.endTime.add(targetSchedule.items[i + 1].travelTime);
      }
    }

    notifyListeners();
  }

  Future<void> shareScheduleWithDeeplink(BuildContext context) async {
    // schedules를 JSON으로 변환
    final scheduleData = jsonEncode(_schedules.map((s) => s.toJson()).toList());
    final encodedData = base64Url.encode(utf8.encode(scheduleData));

    // 딥링크 생성
    final deeplink = 'mytour://schedule?data=$encodedData';
    print(deeplink);

    // 공유하기
    await Share.share('내 여행 일정을 확인해보세요!\n$deeplink');
  }
}