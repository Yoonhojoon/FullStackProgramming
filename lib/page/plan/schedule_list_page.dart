import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../../entity/DailyPlan.dart';
import '../../entity/Destination.dart';

class ScheduleListPage extends StatelessWidget {
  final List<DailyPlan> dailyPlans;

  const ScheduleListPage({Key? key, required this.dailyPlans}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: BackButton(),
        title: Text('계획 짜기'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: dailyPlans.length,
        itemBuilder: (context, index) {
          final plan = dailyPlans[index];
          return Padding(
            padding: const EdgeInsets.only(bottom: 16),
            child: _buildDaySchedule(plan),
          );
        },
      ),
    );
  }

  Widget _buildDaySchedule(DailyPlan plan) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Day Header with Total Distance and Travel Time
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          decoration: BoxDecoration(
            color: Color(0xFFFBE5D6),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Row(
            children: [
              Text(
                '${plan.dayNumber}일차 계획',
                style: TextStyle(fontWeight: FontWeight.w600),
              ),
              Spacer(),
              Text(
                '총 거리: ${plan.totalDistance.toStringAsFixed(1)} km, 총 시간: ${_formatMinutes(plan.totalTravelTime)}',
                style: TextStyle(fontSize: 12, color: Colors.grey[600]),
              ),
            ],
          ),
        ),
        SizedBox(height: 8),
        // Accommodation (if exists)
        if (plan.accommodation != null) _buildDestinationTile(plan.accommodation!, isAccommodation: true),
        // Other Destinations
        Column(
          children: plan.destinations.map((destination) {
            return _buildDestinationTile(destination);
          }).toList(),
        ),
      ],
    );
  }

  Widget _buildDestinationTile(Destination destination, {bool isAccommodation = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          // Indicator for accommodation or regular destination
          Container(
            width: 4,
            height: 50,
            decoration: BoxDecoration(
              color: isAccommodation ? Colors.orange : Colors.yellow,
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Destination name
                Text(
                  destination.name,
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
                ),
                SizedBox(height: 4),
                // Additional details
                if (destination.timeToNext != null && destination.distanceToNext != null)
                  Text(
                    '다음 목적지까지: ${destination.distanceToNext?.toStringAsFixed(1)} km, 약 ${_formatMinutes(destination.timeToNext!)}',
                    style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                  ),
                if (destination.transitDetails != null)
                  Row(
                    children: [
                      Icon(Icons.directions_transit, size: 16, color: Colors.grey[600]),
                      SizedBox(width: 4),
                      Text(destination.transitDetails!, style: TextStyle(fontSize: 12)),
                    ],
                  ),
              ],
            ),
          ),
          // Order in the day's plan
          if (destination.orderInDay != null)
            Text(
              '순서: ${destination.orderInDay}',
              style: TextStyle(fontSize: 12, color: Colors.grey),
            ),
        ],
      ),
    );
  }

  // Helper to format minutes into hours and minutes
  String _formatMinutes(int minutes) {
    final hours = minutes ~/ 60;
    final remainingMinutes = minutes % 60;
    if (hours > 0) {
      return '$hours시간 ${remainingMinutes}분';
    } else {
      return '${remainingMinutes}분';
    }
  }
}
