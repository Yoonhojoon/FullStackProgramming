import 'package:flutter/material.dart';
import 'package:flutter_naver_map/flutter_naver_map.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: ".env");
  String clientId = dotenv.get("CLIENT_ID");
  await NaverMapSdk.instance.initialize(
      clientId: clientId,
      onAuthFailed: (ex) {
        print("********* 네이버맵 인증오류 : $ex *********");
      });
  runApp(MyApp());
}
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(

      home: First(),

    );
  }
}

  class First extends StatelessWidget{
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



