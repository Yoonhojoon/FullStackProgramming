import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:mytour/entity/TripItem.dart';
import 'package:provider/provider.dart';

import '../../dailyplan_provider.dart';
import '../../entity/DailyPlan.dart';
import '../../entity/DailySchedule.dart';
import '../../entity/Destination.dart';
import '../../entity/ScheduleItem.dart';
import '../../entity/TimeSelectionDialog.dart';

class ScheduleListPage extends StatelessWidget {
  final List<DailySchedule> dailySchedules;

  const ScheduleListPage({
    Key? key,
    required this.dailySchedules,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pop(context),
        ),
        title: const Text('계획 결과'),
        actions: [
          IconButton(
            icon: const Icon(Icons.delete),
            onPressed: () {
              // TODO: 삭제 기능 구현
            },
          ),
        ],
      ),
      body: ListView.builder(
        itemCount: dailySchedules.length,
        itemBuilder: (context, index) {
          final daily = dailySchedules[index];
          return DailyScheduleCard(schedule: daily);
        },
      ),
      bottomNavigationBar: Container(
        padding: const EdgeInsets.all(16),
        child: ElevatedButton(
          child: const Text('여행계획 공유하기'),
          onPressed: () {
            // TODO: 공유 기능 구현
          },
        ),
      ),
    );
  }
}

class DailyScheduleCard extends StatelessWidget {
  final DailySchedule schedule;

  const DailyScheduleCard({
    Key? key,
    required this.schedule,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.all(8),
      child: ExpansionTile(
        title: Text(schedule.title),
        children: schedule.items.map((item) => ScheduleItemTile(item: item)).toList(),
      ),
    );
  }
}

class ScheduleItemTile extends StatelessWidget {
  final ScheduleItem item;

  const ScheduleItemTile({
    Key? key,
    required this.item,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: Container(
        width: 4,
        height: 40,
        color: _getColorFromType(item.tripItem.type),
      ),
      title: Text(item.tripItem.name),
      subtitle: Text(item.timeRangeString),
      trailing: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(Icons.directions_car),
          Text(item.travelTimeString),
        ],
      ),
      onTap: () {
        showDialog(
          context: context,
          builder: (context) => TimeSelectionDialog(
            item: item,
            onTimeUpdated: (startTime, duration) {
              Provider.of<DailyPlanProvider>(context, listen: false)
                  .updateItemTime(item, startTime, duration);
            },
          ),
        );
      },
    );
  }

  Color _getColorFromType(String type) {
    switch (type.toUpperCase()) {
      case 'ACCOMMODATION':
        return Colors.red;  // 숙소는 빨간색
      case 'SPOT':
        return Colors.orange;  // 관광지는 주황색
      case 'RESTAURANT':
        return Colors.yellow;  // 음식점은 노란색
      default:
        return Colors.blue;  // 기본값은 파란색
    }
  }
  }
