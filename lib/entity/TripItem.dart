import 'Guide.dart';

class TripItem {
  final String name;
  final String address;
  final String type;
  final String color;
  final int travelTimeMinutes;
  final double distanceToNext;
  final Guide guide;

TripItem({
  required this.name,
  required this.address,
  required this.type,
  required this.color,
  required this.travelTimeMinutes,
  required this.distanceToNext,
  required this.guide,
});

factory TripItem.fromJson(Map<String, dynamic> json) {
return TripItem(
name: json['name'],
address: json['address'],
type: json['type'],
color: json['color'],
travelTimeMinutes: json['travelTimeMinutes'],
distanceToNext: json['distanceToNext'].toDouble(),
guide: Guide.fromJson(json['guide']),
);
}
}