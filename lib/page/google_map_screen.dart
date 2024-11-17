import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geocoder/geocoder.dart'; // Import for geocoding functionality

class GoogleMapScreen extends StatefulWidget {
  @override
  _MapScreenState createState() => _MapScreenState();
}

class _MapScreenState extends State<GoogleMapScreen> {
  GoogleMapController? mapController;
  final TextEditingController _addressController = TextEditingController();
  String _selectedAddress = '';

  Future<void> _searchAddress() async {
    try {
      final coordinates = await Geocoder.local.findPlacesByName(_addressController.text);
      if (coordinates.isEmpty) {
        throw Exception('주소를 찾을 수 없습니다.');
      }
      final placemark = coordinates.first;
      final position = LatLng(placemark.latitude!, placemark.longitude!);
      mapController?.animateCamera(CameraUpdate.newLatLngZoom(position, 15));
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('주소를 찾을 수 없습니다: $e')),
      );
    }
  }

  Future<String> getAddressFromCoordinates(double latitude, double longitude) async {
    try {
      final coordinates = Coordinates(latitude, longitude);
      final addresses = await Geocoder.local.findAddressesFromCoordinates(coordinates);
      if (addresses.isEmpty) {
        throw Exception('주소를 가져올 수 없습니다.');
      }
      final address = addresses.first.thoroughfare + ', ' + addresses.first.locality;
      return address;
    } catch (e) {
      throw Exception('주소를 가져올 수 없습니다: $e');
    }
  }

  void _handleTap(LatLng point) async {
    try {
      final address = await getAddressFromCoordinates(point.latitude, point.longitude);
      setState(() {
        _selectedAddress = address;
      });
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('주소를 가져올 수 없습니다: $e')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('지도 예제')),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: _addressController,
              decoration: InputDecoration(
                labelText: '주소 입력',
                suffixIcon: IconButton(
                  icon: Icon(Icons.search),
                  onPressed: _searchAddress,
                ),
              ),
            ),
          ),
          Expanded(
            child: GoogleMap(
              onMapCreated: (GoogleMapController controller) {
                mapController = controller;
              },
              initialCameraPosition: CameraPosition(
                target: LatLng(37.5665, 126.9780), // Seoul (default)
                zoom: 11.0,
              ),
              onTap: _handleTap,
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text('선택된 주소: $_selectedAddress'),
          ),
        ],
      ),
    );
  }
}