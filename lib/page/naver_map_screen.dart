import 'package:flutter/material.dart';
import 'package:flutter_naver_map/flutter_naver_map.dart';

class NaverMapScreen extends StatelessWidget {
  const NaverMapScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text('mytour'),
        ),
        body: NaverMap(
          options: const NaverMapViewOptions(locationButtonEnable: true),
          onMapReady: (controller) {
            print("네이버 맵 로딩됨!");
          },
      )
    );
  }

}

