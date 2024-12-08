import 'package:flutter/material.dart';
import 'package:mytour/page/plan/schedule_list_page.dart';
import 'package:mytour/page/plan/scheduling_page.dart';
import 'package:mytour/page/popular_travel.dart';
import 'package:provider/provider.dart';
import '../dailyplan_provider.dart';
import '../entity/DailyPlan.dart';
import '../entity/DailySchedule.dart';
import '../entity/Destination.dart';
import '../entity/ScheduleItem.dart';
import '../entity/TripItem.dart';
import 'google_map_screen.dart';
import 'home_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0;

  @override
  Widget build(BuildContext context) {
    final List<Widget> _pages = [
      const HomeScreen(),
      PopularTravel(),
      // Consumer<DailyPlanProvider>(
      //   builder: (context, provider, child) => ScheduleListPage(
      //     dailySchedules: provider.schedules,
      //   ),
      // ),
      GoogleMapScreen(),
      SchedulingPage(
        onPlansCreated: (List<TripItem> tripItems) {
          // 현재 시간을 기준으로 시작
          DateTime currentTime = DateTime.now();
          List<ScheduleItem> scheduleItems = tripItems.map((item) {
            return ScheduleItem(
              tripItem: item,
              startTime: currentTime,
              duration: const Duration(hours: 1),  // 기본값으로 설정, 나중에 사용자가 수정
              travelTime: Duration(minutes: item.travelTimeMinutes),
            );
          }).toList();

          // 모든 항목을 하나의 DailySchedule로 묶기
          final dailySchedule = DailySchedule(
            day: 1,
            items: scheduleItems,
          );

          Provider.of<DailyPlanProvider>(context, listen: false)
              .setSchedules([dailySchedule]);
        },
      ),
      const ProfileScreen(),
    ];


    return Scaffold(
      appBar: AppBar(),
      body: _pages[_currentIndex],
      bottomNavigationBar: BottomNavigationBar(
        backgroundColor: Colors.white,
        selectedItemColor: Colors.black,
        unselectedItemColor: Colors.black,
        currentIndex: _currentIndex,
        type: BottomNavigationBarType.fixed, // 5개 이상의 아이템을 위해 추가
        onTap: (index) {
          setState(() {
            _currentIndex = index;
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

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Text('Profile Screen'),
    );
  }
}