import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:mytour/page/main_screen.dart';
import 'package:mytour/service/auth_service.dart';

class LoginPage extends StatelessWidget {
  const LoginPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(  // MaterialApp 대신 Scaffold만 반환
      body: SingleChildScrollView(
        child: Container(
          height: MediaQuery.of(context).size.height,
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const _HeaderSection(),
              const SizedBox(height: 48),
              _buildLoginButtons(context),
              const SizedBox(height: 32),
              const _DividerSection(),
              const SizedBox(height: 32),
              const _SignUpSection(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildLoginButtons(BuildContext context) {
    final authService = AuthService();
    return Column(
        children: [
          _LoginButton(
            text: '구글로 시작하기',
            icon: Image.network(
              'https://120812081208.s3.us-east-1.amazonaws.com/google.png',
              height: 24,
            ),
            onPressed: () async {
              final success = await authService.signInWithGoogle();
              if (success) {
                Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(builder: (context) => const MainScreen()),
                );
              } else {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('로그인에 실패했습니다')),
                );
              }
            },
          ),
        ]
    );

  }
}

class _HeaderSection extends StatelessWidget {
  const _HeaderSection();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          height: 120,
          width: 120,
          decoration: BoxDecoration(
            color: const Color(0xFFFF7800).withOpacity(0.1),
            shape: BoxShape.circle,
          ),
          child: const Icon(
            Icons.map_outlined,
            size: 60,
            color: Color(0xFFFF7800),
          ),
        ),
        const SizedBox(height: 24),
        const Text(
          'My Tour',
          style: TextStyle(
            fontSize: 32,
            fontWeight: FontWeight.bold,
            color: Color(0xFFFF7800),
          ),
        ),
        const SizedBox(height: 12),
        const Text(
          '나만의 여행 플래너',
          style: TextStyle(
            fontSize: 16,
            color: Colors.grey,
          ),
        ),
      ],
    );
  }
}

class _LoginButton extends StatelessWidget {
  final String text;
  final Widget? icon;
  final VoidCallback? onPressed;
  final bool isOutlined;

  const _LoginButton({
    required this.text,
    this.icon,
    this.onPressed,
    this.isOutlined = false,
  });

  @override
  Widget build(BuildContext context) {
    return Material(
      color: isOutlined ? Colors.white : const Color(0xFFFF7800),
      borderRadius: BorderRadius.circular(12),
      child: InkWell(
        onTap: onPressed,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          width: double.infinity,
          height: 56,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(12),
            border: isOutlined
                ? Border.all(color: Colors.grey.shade300)
                : null,
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              if (icon != null) ...[
                icon!,
                const SizedBox(width: 12),
              ],
              Text(
                text,
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: isOutlined ? Colors.grey.shade700 : Colors.white,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _DividerSection extends StatelessWidget {
  const _DividerSection();

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(child: Divider(color: Colors.grey.shade300)),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Text(
            'or',
            style: TextStyle(
              color: Colors.grey.shade600,
              fontSize: 14,
            ),
          ),
        ),
        Expanded(child: Divider(color: Colors.grey.shade300)),
      ],
    );
  }
}

class _SignUpSection extends StatelessWidget {
  const _SignUpSection();

  @override
  Widget build(BuildContext context) {
    return RichText(
      text: TextSpan(
        style: const TextStyle(fontSize: 14),
        children: [
          TextSpan(
            text: '아직 계정이 없으신가요? ',
            style: TextStyle(color: Colors.grey.shade600),
          ),
          TextSpan(
            text: '회원가입',
            style: const TextStyle(
              color: Color(0xFFFF7800),
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }
}