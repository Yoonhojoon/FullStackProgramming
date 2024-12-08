import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geocoding/geocoding.dart';
import 'package:mytour/entity/TripItem.dart';

import '../../entity/DailySchedule.dart';

class ScheduleMapScreen extends StatefulWidget {
  final List<DailySchedule> dailySchedules;  // TripItems 대신 DailySchedule 리스트를 받도록 수정

  const ScheduleMapScreen({Key? key, required this.dailySchedules}) : super(key: key);

  @override
  State<ScheduleMapScreen> createState() => _ScheduleMapScreenState();
}

class _ScheduleMapScreenState extends State<ScheduleMapScreen> {
  GoogleMapController? mapController;
  Set<Marker> markers = {};
  Set<Polyline> polylines = {};  // 경로선을 위한 Set
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    _initializeMarkersAndPolylines();
  }



  // 각 일정별 색상 리스트
  final List<Color> scheduleColors = [
    Colors.blue,
    Colors.red,
    Colors.green,
    Colors.purple,
    Colors.orange,
    Colors.pink,
    Colors.teal,
  ];

  // 마커 아이콘 생성을 위한 메서드
  Future<BitmapDescriptor> _createMarkerIcon(int dayIndex, String type) async {
    // Canvas 설정
    final PictureRecorder pictureRecorder = PictureRecorder();
    final Canvas canvas = Canvas(pictureRecorder);
    final double size = 150.0; // 마커 크기

    // 배경 원 그리기
    final Paint circlePaint = Paint()
      ..color = scheduleColors[dayIndex % scheduleColors.length].withOpacity(0.9);
    canvas.drawCircle(Offset(size / 2, size / 2), size / 3, circlePaint);

    // 테두리 그리기
    final Paint borderPaint = Paint()
      ..color = Colors.white
      ..style = PaintingStyle.stroke
      ..strokeWidth = 8;
    canvas.drawCircle(Offset(size / 2, size / 2), size / 3, borderPaint);

    // 아이콘 설정
    IconData iconData;
    switch (type.toUpperCase()) {
      case 'ACCOMMODATION':
        iconData = Icons.hotel;
        break;
      case 'RESTAURANT':
        iconData = Icons.restaurant;
        break;
      case 'SPOT':
        iconData = Icons.photo_camera;
        break;
      default:
        iconData = Icons.place;
    }

    // 텍스트 스타일 설정
    TextPainter textPainter = TextPainter(textDirection: TextDirection.ltr);
    textPainter.text = TextSpan(
      text: String.fromCharCode(iconData.codePoint),
      style: TextStyle(
        fontSize: size / 2,
        fontFamily: iconData.fontFamily,
        color: Colors.white,
      ),
    );

    // 텍스트 그리기
    textPainter.layout();
    textPainter.paint(
      canvas,
      Offset(
        size / 2 - textPainter.width / 2,
        size / 2 - textPainter.height / 2,
      ),
    );

    // 숫자 추가
    textPainter.text = TextSpan(
      text: '${dayIndex + 1}',
      style: TextStyle(
        fontSize: size / 4,
        fontWeight: FontWeight.bold,
        color: Colors.white,
      ),
    );
    textPainter.layout();
    textPainter.paint(
      canvas,
      Offset(
        size / 2 - textPainter.width / 2,
        size / 2 + textPainter.height,
      ),
    );

    // 비트맵으로 변환
    final img = await pictureRecorder.endRecording().toImage(
      size.toInt(),
      size.toInt(),
    );
    final data = await img.toByteData(format: ImageByteFormat.png);

    return BitmapDescriptor.fromBytes(data!.buffer.asUint8List());
  }


  Future<void> _initializeMarkersAndPolylines() async {
    for (int i = 0; i < widget.dailySchedules.length; i++) {
      DailySchedule schedule = widget.dailySchedules[i];
      Color scheduleColor = scheduleColors[i % scheduleColors.length];
      List<LatLng> dayPoints = [];

      for (int j = 0; j < schedule.items.length; j++) {
        TripItem tripItem = schedule.items[j].tripItem;
        try {
          List<Location> locations = await locationFromAddress(tripItem.address);
          if (locations.isNotEmpty) {
            LatLng position = LatLng(
                locations.first.latitude,
                locations.first.longitude
            );
            dayPoints.add(position);

            // 커스텀 마커 아이콘 생성
            final markerIcon = await _createMarkerIcon(i, tripItem.type);

            markers.add(
              Marker(
                markerId: MarkerId('${schedule.title}_${tripItem.name}'),
                position: position,
                icon: markerIcon,
                infoWindow: InfoWindow(
                  title: tripItem.name,
                  snippet: '${schedule.title} - ${tripItem.address}',
                ),
              ),
            );
          }
        } catch (e) {
          print('Error getting location for ${tripItem.name}: $e');
        }
      }

      // 경로선 추가 (더 부드럽게)
      if (dayPoints.length >= 2) {
        polylines.add(
          Polyline(
            polylineId: PolylineId('route_${schedule.title}'),
            points: dayPoints,
            color: scheduleColor,
            width: 4,
            patterns: [
              PatternItem.dash(20.0),
              PatternItem.gap(10.0),
            ],
          ),
        );
      }
    }

    setState(() {
      isLoading = false;
    });
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('일정별 경로'),
      ),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : GoogleMap(
        onMapCreated: (controller) {
          mapController = controller;
        },
        initialCameraPosition: const CameraPosition(
          target: LatLng(37.5665, 126.9780),
          zoom: 12,
        ),
        markers: markers,
        polylines: polylines,  // 경로선 추가
      ),
    );
  }
}