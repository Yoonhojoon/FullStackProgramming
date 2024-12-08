import 'package:intl/intl.dart';

import 'TripItem.dart';


class ScheduleItem {
  final TripItem tripItem;
  DateTime startTime;
  DateTime endTime;
  final Duration travelTime;

  ScheduleItem({
    required this.tripItem,
    required this.startTime,
    required Duration duration,
    required this.travelTime,
  }) : endTime = startTime.add(duration);  // 이렇게 초기화


  // 시간 수정을 위한 메서드
  void updateTimes({DateTime? newStart, Duration? newDuration}) {
    if (newStart != null) {
      startTime = newStart;
      endTime = newStart.add(endTime.difference(startTime));
    }
    if (newDuration != null) {
      endTime = startTime.add(newDuration);
    }
  }
//포맷된 문자열
  String get timeRangeString {
    return '${_formatTime(startTime)} - ${_formatTime(endTime)}';
  }

  String get travelTimeString {
    return '${travelTime.inMinutes}분';
  }

  int get duration {
    return endTime.difference(startTime).inMinutes;
  }

  String _formatTime(DateTime time) {
    return '${time.hour.toString().padLeft(2, '0')}:${time.minute.toString().padLeft(2, '0')}';
  }
}