import 'package:flutter/material.dart';
import 'package:mytour/page/plan/schedule_list_page.dart';
import 'package:mytour/page/plan/scheduling_page.dart';
import 'package:provider/provider.dart';
import '../dailyplan_provider.dart';
import '../entity/DailyPlan.dart';
import '../entity/Destination.dart';
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
      GoogleMapScreen(),
      Consumer<DailyPlanProvider>(
        builder: (context, provider, child) => ScheduleListPage(
          tripItems: [],
          dailyPlans: provider.plans,
        ),
      ),
      SchedulingPage(
        onPlansCreated: (List<TripItem> tripItems) {
          int dayNumber = 1;
          double totalDist = 0;
          int totalTime = 0;
          List<Destination> destinations = [];
          Destination? accommodation;

          for (var item in tripItems) {
            final destination = Destination(
              id: destinations.length + 1,
              type: item.type.toUpperCase() == 'ACCOMMODATION'
                  ? DestinationType.ACCOMMODATION
                  : DestinationType.SPOT,
              name: item.name,
              address: item.address,
              latitude: 0.0,
              longitude: 0.0,
              orderInDay: destinations.length + 1,
              distanceToNext: item.distanceToNext,
              timeToNext: item.travelTimeMinutes,
              transitDetails: null,
              lastTransitDetails: null,
              drivingDetails: null,
              lastDrivingDetails: null,
            );

            if (item.type.toUpperCase() == 'ACCOMMODATION') {
              accommodation = destination;
            } else {
              destinations.add(destination);
              totalDist += item.distanceToNext;
              totalTime += item.travelTimeMinutes;
            }
          }

          final dailyPlans = [
            DailyPlan(
              id: dayNumber,
              dayNumber: dayNumber,
              accommodation: accommodation,
              destinations: destinations,
              totalDistance: totalDist,
              totalTravelTime: totalTime,
            )
          ];

          Provider.of<DailyPlanProvider>(context, listen: false).updatePlans(dailyPlans);
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