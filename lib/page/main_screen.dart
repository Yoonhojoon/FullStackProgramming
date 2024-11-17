import 'package:flutter/material.dart';
import 'google_map_screen.dart';
import 'home_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0; // 현재 선택된 인덱스

  // 각 탭에 표시할 화면 목록
  final List<Widget> _pages = [
    const HomeScreen(),
    const GoogleMapScreen(), // 네이버 맵 화면 추가
    const ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        // title: const Text('My Tour App'),
      ),
      body: _pages[_currentIndex], // 현재 선택된 페이지 표시
      bottomNavigationBar: BottomNavigationBar(
        backgroundColor: Colors.white, // 네비게이션 바 색상을 하얀색으로 설정
        selectedItemColor: Colors.black, // 선택된 아이콘 색상을 검은색으로 설정
        unselectedItemColor: Colors.black, // 선택되지 않은 아이콘 색상을 검은색으로 설정
        currentIndex: _currentIndex, // 현재 선택된 인덱스
        onTap: (index) {
          setState(() {
            _currentIndex = index; // 인덱스 업데이트
          });
        },
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.view_cozy),
            label: 'List',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.map),
            label: 'Map',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.calendar_month),
            label: 'Cal',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}


// Profile 화면 예제
class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Text('Profile Screen'),
    );
  }
}
