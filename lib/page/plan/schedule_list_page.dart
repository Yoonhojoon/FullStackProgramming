import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../entity/DailyPlan.dart';

class ScheduleListPage extends StatelessWidget {
  final List<DailyPlan> dailyPlans;

  const ScheduleListPage({Key? key, required this.dailyPlans}) : super(key: key);

  Widget _buildDaySchedule(String day, List<Map<String, dynamic>> schedules) {
    return Column(
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          decoration: BoxDecoration(
            color: Color(0xFFFBE5D6),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Row(
            children: [
              Text(
                day,
                style: TextStyle(fontWeight: FontWeight.w600),
              ),
              Icon(Icons.keyboard_arrow_down),
            ],
          ),
        ),
        ...schedules.map((schedule) => Padding(
          padding: const EdgeInsets.symmetric(vertical: 8),
          child: Row(
            children: [
              Container(
                width: 4,
                height: 50,
                decoration: BoxDecoration(
                  color: schedule['isAccommodation']
                      ? Colors.orange
                      : Colors.yellow,
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
              SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      schedule['name'],
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    if (schedule['duration'] != null) ...[
                      SizedBox(height: 4),
                      Row(
                        children: [
                          Icon(Icons.directions_car, size: 16),
                          SizedBox(width: 4),
                          Text(schedule['duration']),
                        ],
                      ),
                    ],
                  ],
                ),
              ),
              Text(schedule['time'] ?? ''),
            ],
          ),
        )).toList(),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: BackButton(),
        title: Text('계획 짜기'),
        actions: [
          IconButton(
            icon: Icon(Icons.delete_outline),
            onPressed: () {},
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          _buildDaySchedule('1일차 계획', [
            {
              'name': '숙터티비닷컴',
              'time': '11:00',
              'duration': '20분',
              'isAccommodation': false,
            },
            {
              'name': '성찬산',
              'time': '11:20 - 12:30',
              'duration': '15분',
              'isAccommodation': false,
            },
            {
              'name': '김밥천국',
              'time': '12:45 - 13:30',
              'isAccommodation': false,
            },
          ]),
          SizedBox(height: 16),
          _buildDaySchedule('2일차 계획', [
            {
              'name': '숙소 체크인',
              'time': '10:00',
              'duration': '20분',
              'isAccommodation': true,
            },
            {
              'name': '숙소 중앙시장',
              'time': '10:20 - 13:00',
              'duration': '15분',
              'isAccommodation': false,
            },
            {
              'name': '김밥천국',
              'time': '12:45 - 13:30',
              'isAccommodation': false,
            },
          ]),
          SizedBox(height: 16),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: Color(0xFFFBE5D6),
              minimumSize: Size(double.infinity, 48),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8),
              ),
            ),
            onPressed: () {},
            child: Text('여행계획 공유하기'),
          ),
        ],
      ),
    );
  }
}