import 'ScheduleItem.dart';
import 'TripItem.dart';

class DailySchedule {
  final int day;
  final List<ScheduleItem> items;

  DailySchedule({required this.day, required this.items});

  String get title => '$day일차 계획';

  factory DailySchedule.fromJson(Map<String, dynamic> json) {
    final List<TripItem> tripItems = (json['items'] as List)
        .map((item) => TripItem.fromJson(item))
        .toList();

    // 기본 시작 시간을 아침 9시로 설정
    DateTime currentTime = DateTime.now().copyWith(hour: 9, minute: 0);

    // 각 아이템을 ScheduleItem으로 변환
    List<ScheduleItem> scheduleItems = tripItems.map((tripItem) {
      // 기본 체류시간 1시간으로 설정
      final Duration duration = Duration(hours: 1);
      // travelTimeMinutes를 Duration으로 변환
      final Duration travelTime = Duration(minutes: tripItem.travelTimeMinutes);

      // ScheduleItem 생성
      ScheduleItem item = ScheduleItem(
        tripItem: tripItem,
        startTime: currentTime,
        duration: duration,
        travelTime: travelTime,
      );

      // 다음 아이템의 시작 시간 계산 (현재 종료시간 + 이동시간)
      currentTime = item.endTime.add(travelTime);

      return item;
    }).toList();

    return DailySchedule(
      day: json['dayNumber'],
      items: scheduleItems,
    );
  }

  Map<String, dynamic> toJson() => {
    'dayNumber': day,
    'items': items.map((item) => item.tripItem.toJson()).toList(),
  };
}