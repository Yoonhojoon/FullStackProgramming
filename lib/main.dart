import 'package:flutter/material.dart';
import 'package:flutter_naver_map/flutter_naver_map.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:mytour/page/splash_screen.dart';
import 'package:mytour/model/place_data.dart';  // PlaceData 클래스를 임포트
import 'package:provider/provider.dart';
import 'place_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: ".env");
  String clientId = dotenv.get("CLIENT_ID");
  await NaverMapSdk.instance.initialize(
      clientId: clientId,
      onAuthFailed: (ex) {
        print("********* 네이버맵 인증오류 : $ex *********");
      });
  runApp(        ChangeNotifierProvider(
    create: (_) => PlaceProvider(),
    child: MyApp(),
  ),
  );
}
class MyApp extends StatelessWidget {
  MyApp({super.key});
  final RouteObserver<PageRoute> routeObserver = RouteObserver<PageRoute>();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorObservers: [routeObserver],
      home:  SplashScreen(), //TODO: 마지막에는 LOGIN_PAGE로

    );
  }
}



