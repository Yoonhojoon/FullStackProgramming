import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:google_place/google_place.dart';
import 'package:mytour/destination_provider.dart';
import 'package:mytour/service/api_service.dart';
import 'package:provider/provider.dart';
import 'package:mytour/destination_provider.dart';  // PlaceProvider import
import 'package:flutter_dotenv/flutter_dotenv.dart';

class GoogleMapScreen extends StatefulWidget {
  @override
  _GoogleMapScreenState createState() => _GoogleMapScreenState();
}

class _GoogleMapScreenState extends State<GoogleMapScreen> {
  final apiService = ApiService();
  GoogleMapController? mapController;
  final TextEditingController _addressController = TextEditingController();
  Set<Marker> _markers = {};

  final googlePlace = GooglePlace(dotenv.env['GOOGLE_MAP_API']!);

  Future<void> _searchAddress() async {
    try {
      var result = await googlePlace.search.getTextSearch(_addressController.text);

      if (result != null && result.results != null && result.results!.isNotEmpty) {
        setState(() {
          _markers.clear();
        });

        for (var place in result.results!) {
          if (place.geometry?.location?.lat != null &&
              place.geometry?.location?.lng != null) {
            final marker = Marker(
              markerId: MarkerId(place.placeId ?? DateTime.now().toString()),
              position: LatLng(
                place.geometry!.location!.lat!,
                place.geometry!.location!.lng!,
              ),
              infoWindow: InfoWindow(
                title: place.name,
                snippet: place.formattedAddress,
              ),
              onTap: () => _showPlaceDetails(place),
            );

            setState(() {
              _markers.add(marker);
            });

            // 카메라를 해당 위치로 이동
            if (mapController != null && place.geometry?.location?.lat != null && place.geometry?.location?.lng != null) {
              final position = LatLng(
                  place.geometry!.location!.lat!,
                  place.geometry!.location!.lng!
              );
              mapController?.animateCamera(CameraUpdate.newLatLngZoom(position, 15));
            }
          }
        }
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('검색 중 오류가 발생했습니다. ($e)')),
      );
    }
  }

  void _showPlaceDetails(SearchResult place) {
    showModalBottomSheet(
      context: context,
      builder: (context) {
        String _selectedType = '숙박'; // 기본값 설정

        return StatefulBuilder(
          builder: (BuildContext context, StateSetter setModalState) {
            return Container(
              padding: EdgeInsets.all(16),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    place.name ?? '이름 없음',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  SizedBox(height: 8),
                  Text(place.formattedAddress ?? '주소 없음'),
                  SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: RadioListTile<String>(
                          title: Text('숙박'),
                          value: 'ACCOMMODATION',
                          groupValue: _selectedType,
                          onChanged: (value) {
                            if (value != null) {
                              setModalState(() {
                                _selectedType = value;
                              });
                            }
                          },
                        ),
                      ),
                      Expanded(
                        child: RadioListTile<String>(
                          title: Text('장소'),
                          value: 'SPOT',
                          groupValue: _selectedType,
                          onChanged: (value) {
                            if (value != null) {
                              setModalState(() {
                                _selectedType = value;
                              });
                            }
                          },
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: () async {
                      // Destination 데이터를 위한 placeData 생성
                      final placeData = {
                        'name': place.name,
                        'address': place.formattedAddress,
                        'latitude': place.geometry?.location?.lat,
                        'longitude': place.geometry?.location?.lng,
                        'type': _selectedType.toUpperCase(), // 숙박/장소 값 전달
                        'transitDetails': null,
                        'lastTransitDetails': null,
                        'drivingDetails': null,
                        'lastDrivingDetails': null,
                        'orderInDay': null,
                        'distanceToNext': null,
                        'timeToNext': null,
                      };

                      final success = await apiService.addDestination(placeData);
                      // DestinationProvider를 통해 destination에 추가
                      final placeProvider = Provider.of<DestinationProvider>(context, listen: false);
                      placeProvider.addDestination(placeData);

                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('여행 리스트에 추가되었습니다!')),
                      );

                      Navigator.pop(context);
                    },
                    child: Text('여행 리스트에 추가'),
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('장소 검색')),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: _addressController,
              decoration: InputDecoration(
                labelText: '검색어 입력',
                hintText: '장소, 주소, 키워드 등 검색',
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
                target: LatLng(37.5665, 126.9780),
                zoom: 11.0,
              ),
              markers: _markers,
            ),
          ),
        ],
      ),
    );
  }
}
