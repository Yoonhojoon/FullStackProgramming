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
  }) {
    endTime = startTime.add(duration);
  }

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

  // 포맷된 시간 문자열 얻기
  String get timeRangeString {
    final startFormatted = DateFormat('HH:mm').format(startTime);
    final endFormatted = DateFormat('HH:mm').format(endTime);
    return '$startFormatted ~ $endFormatted';
  }

  String get travelTimeString {
    return '${travelTime.inMinutes}분';
  }
}