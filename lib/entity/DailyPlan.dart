
import 'dart:ffi';

import 'Destination.dart';

class DailyPlan {
  final int id;
  final int dayNumber;
  final Destination? accommodation;
  final List<Destination> destinations;
  final double totalDistance;
  final int totalTravelTime;

  DailyPlan({
    required this.id,
    required this.dayNumber,
    this.accommodation,
    required this.destinations,
    required this.totalDistance,
    required this.totalTravelTime,
  });

  factory DailyPlan.fromJson(Map<String, dynamic> json) {
    return DailyPlan(
      id: json['id'],
      dayNumber: json['dayNumber'],
      accommodation: json['accommodation'] != null
          ? Destination.fromJson(json['accommodation'])
          : null,
      destinations: (json['destinations'] as List)
          .map((x) => Destination.fromJson(x))
          .toList(),
      totalDistance: (json['totalDistance'] as num?)?.toDouble() ?? 0.0,
      totalTravelTime: (json['totalTravelTime'] as num?)?.toInt() ?? 0,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'dayNumber': dayNumber,
    'accommodation': accommodation?.toJson(),
    'destinations': destinations.map((x) => x.toJson()).toList(),
    'totalDistance': totalDistance,
    'totalTravelTime': totalTravelTime,
  };
}