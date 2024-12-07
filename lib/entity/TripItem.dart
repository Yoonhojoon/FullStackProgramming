import 'Guide.dart';

class TripItem {
  final String name;
  final String address;
  final String type;
  final String color;
  final int travelTimeMinutes;
  final double distanceToNext;
  final Guide? guide;  // null 가능하도록 변경

  TripItem({
    required this.name,
    required this.address,
    required this.type,
    required this.color,
    required this.travelTimeMinutes,
    required this.distanceToNext,
    this.guide,  // required 제거
  });

  factory TripItem.fromJson(Map<String, dynamic> json) {
    return TripItem(
      name: json['name'] ?? '',
      address: json['address'] ?? '',
      type: json['type'] ?? '',
      color: json['color'] ?? 'red',
      travelTimeMinutes: json['travelTimeMinutes'] ?? 0,
      distanceToNext: (json['distanceToNext'] ?? 0).toDouble(),
      guide: json['guide'] != null ? Guide.fromJson(json['guide']) : null,
    );
  }
}