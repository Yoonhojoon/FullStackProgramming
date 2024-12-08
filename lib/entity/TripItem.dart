import 'Guide.dart';

class TripItem {
  final String name;
  final String address;
  final String type;
  final String color;
  final int travelTimeMinutes;
  final double distanceToNext;
  final List<Guide> guides;  // guide를 guides로 변경

  TripItem({
    required this.name,
    required this.address,
    required this.type,
    required this.color,
    required this.travelTimeMinutes,
    required this.distanceToNext,
    required this.guides,  // required로 변경하고 기본값을 빈 리스트로
  });

  factory TripItem.fromJson(Map<String, dynamic> json) {
    return TripItem(
      name: json['name'] ?? '',
      address: json['address'] ?? '',
      type: json['type'] ?? '',
      color: json['color'] ?? 'red',
      travelTimeMinutes: json['travelTimeMinutes'] ?? 0,
      distanceToNext: (json['distanceToNext'] ?? 0).toDouble(),
      guides: json['guides'] != null
          ? List<Guide>.from(json['guides'].map((x) => Guide.fromJson(x)))
          : [],
    );
  }

  Map<String, dynamic> toJson() => {
    'name': name,
    'address': address,
    'type': type,
    'color': color,
    'travelTimeMinutes': travelTimeMinutes,
    'distanceToNext': distanceToNext,
    'guides': guides.map((guide) => guide.toJson()).toList(),
  };
}