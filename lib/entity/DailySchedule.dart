// 일정을 날짜별로 그룹화하는 클래스
import 'ScheduleItem.dart';

class DailySchedule {
  final int day;
  final List<ScheduleItem> items;

  DailySchedule({required this.day, required this.items});

  String get title => '$day일차 계획';
}