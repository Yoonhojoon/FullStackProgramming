import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AuthService {
  // 싱글톤 패턴 구현
  static final AuthService _instance = AuthService._internal();
  factory AuthService() => _instance;
  AuthService._internal();

  // 현재 로그인된 사용자 정보 저장
  String? _currentUserName;
  String? get currentUserName => _currentUserName;

  final GoogleSignIn _googleSignIn = GoogleSignIn(
    scopes: [
      'email',
      'profile',
    ],
  );

  final String _baseUrl = 'http://10.0.2.2:8080'; // 스프링 서버 주소

  Future<bool> signInWithGoogle() async {
    try {
      final GoogleSignInAccount? account = await _googleSignIn.signIn();
      if (account != null) {
        // 구글 로그인 정보를 스프링 서버로 전송
        final response = await http.post(
          Uri.parse('$_baseUrl/api/auth/google'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({
            'googleId': account.id,
            'email': account.email,
            'username': account.displayName,
          }),
        );

        if (response.statusCode == 200) {
          print('Server Login Success: ${account.displayName}');
          return true;
        }
      }
      return false;
    } catch (e) {
      print('Error: $e');
      return false;
    }
  }

  Future<void> signOut() async {
    await _googleSignIn.signOut();
  }
}