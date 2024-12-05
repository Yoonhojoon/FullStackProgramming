class DestinationModel {
  final String name;
  final String address;
  final double latitude;
  final double longitude;
  final String type;
  final String? transitDetails;
  final String? lastTransitDetails;
  final String? drivingDetails;
  final String? lastDrivingDetails;
  final int? orderInDay;
  final double? distanceToNext;
  final double? timeToNext;

  DestinationModel({
    required this.name,
    required this.address,
    required this.latitude,
    required this.longitude,
    required this.type,
    this.transitDetails,
    this.lastTransitDetails,
    this.drivingDetails,
    this.lastDrivingDetails,
    this.orderInDay,
    this.distanceToNext,
    this.timeToNext,
  });

  Map<String, dynamic> toJson() => {
    'name': name,
    'address': address,
    'latitude': latitude,
    'longitude': longitude,
    'type': type,
    'transitDetails': transitDetails,
    'lastTransitDetails': lastTransitDetails,
    'drivingDetails': drivingDetails,
    'lastDrivingDetails': lastDrivingDetails,
    'orderInDay': orderInDay,
    'distanceToNext': distanceToNext,
    'timeToNext': timeToNext,
  };
}