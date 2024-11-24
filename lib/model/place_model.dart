// place_model.dart
class PlaceData {
  final String name;
  final String address;
  final double latitude;
  final double longitude;

  PlaceData({
    required this.name,
    required this.address,
    required this.latitude,
    required this.longitude,
  });

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'address': address,
      'latitude': latitude,
      'longitude': longitude,
    };
  }
}