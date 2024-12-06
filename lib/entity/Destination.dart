import 'dart:ffi';

class Destination {
  final int id;
  final DestinationType type;
  final String name;
  final String address;
  final double latitude;
  final double longitude;
  final String? transitDetails;
  final String? lastTransitDetails;
  final String? drivingDetails;
  final String? lastDrivingDetails;
  final int? orderInDay;
  final double? distanceToNext;
  final int? timeToNext;

  Destination({
    required this.id,
    required this.type,
    required this.name,
    required this.address,
    required this.latitude,
    required this.longitude,
    this.transitDetails,
    this.lastTransitDetails,
    this.drivingDetails,
    this.lastDrivingDetails,
    this.orderInDay,
    this.distanceToNext,
    this.timeToNext,
  });

  factory Destination.fromJson(Map<String, dynamic> json) {
    return Destination(
      id: json['destinationId'] ?? 0,  // null이면 0으로 처리
      type: DestinationType.values.firstWhere(
              (e) => e.toString() == 'DestinationType.${json['type']}'
      ),
      name: json['name'],
      address: json['address'],
      latitude: json['latitude'],
      longitude: json['longitude'],
      transitDetails: json['transitDetails'],
      lastTransitDetails: json['lastTransitDetails'],
      drivingDetails: json['drivingDetails'],
      lastDrivingDetails: json['lastDrivingDetails'],
      orderInDay: json['orderInDay'],
      distanceToNext: json['distanceToNext'],
      timeToNext: json['timeToNext'],
    );
  }

  Map<String, dynamic> toJson() => {
    'destinationId': id,
    'type': type.toString().split('.').last,
    'name': name,
    'address': address,
    'latitude': latitude,
    'longitude': longitude,
    'transitDetails': transitDetails,
    'lastTransitDetails': lastTransitDetails,
    'drivingDetails': drivingDetails,
    'lastDrivingDetails': lastDrivingDetails,
    'orderInDay': orderInDay,
    'distanceToNext': distanceToNext,
    'timeToNext': timeToNext,
  };
}

enum DestinationType {
  ACCOMMODATION,
  SPOT,
}