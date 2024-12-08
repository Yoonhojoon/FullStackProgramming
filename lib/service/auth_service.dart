import 'package:google_sign_in/google_sign_in.dart';

class AuthService {
  final GoogleSignIn _googleSignIn = GoogleSignIn(
    scopes: [
      'email',
      'profile',
    ],
  );

  Future<GoogleSignInAccount?> signInWithGoogle() async {
    try {
      final GoogleSignInAccount? account = await _googleSignIn.signIn();
      if (account != null) {
        // 필요한 경우 사용자 정보 저장
        final userEmail = account.email;
        final userName = account.displayName;
        final userPhoto = account.photoUrl;

        print('Login Success: $userName ($userEmail)');
        return account;
      }
      return null;
    } catch (e) {
      print('Google Sign In Error: $e');
      return null;
    }
  }

  Future<void> signOut() async {
    await _googleSignIn.signOut();
  }
}