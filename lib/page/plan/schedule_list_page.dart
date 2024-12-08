import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:mytour/entity/TripItem.dart';
import 'package:provider/provider.dart';

import '../../dailyplan_provider.dart';
import '../../entity/DailyPlan.dart';
import '../../entity/DailySchedule.dart';
import '../../entity/Destination.dart';
import '../../entity/Guide.dart';
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
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<DailyPlanProvider>(context, listen: false)
          .setSchedules(dailySchedules);
    });
    return Consumer<DailyPlanProvider>( // 추가된 부분
        builder: (context, provider, child) {
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
                    showDialog(
                      context: context,
                      builder: (context) =>
                          AlertDialog(
                            title: const Text('일정 삭제'),
                            content: const Text('모든 일정을 삭제하시겠습니까?'),
                            actions: [
                              TextButton(
                                onPressed: () => Navigator.pop(context),
                                child: const Text('취소'),
                              ),
                              TextButton(
                                onPressed: () {
                                  Provider.of<DailyPlanProvider>(
                                      context, listen: false)
                                      .deleteAllSchedules();
                                  Navigator.pop(context); // 다이얼로그 닫기
                                  Navigator.pop(context); // 이전 화면으로 돌아가기
                                },
                                child: const Text('삭제'),
                              ),
                            ],
                          ),
                    );
                  },
                ),
              ],
            ),
            body: ListView.builder(
              itemCount: provider.schedules.length,  // provider.schedules 사용
              itemBuilder: (context, index) {
                final daily = provider.schedules[index];  // provider.schedules 사용
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
    return Consumer<DailyPlanProvider>(  // 추가된 부분
        builder: (context, provider, child) {
          return ListTile(
                leading: Container(
                  width: 4,
                  height: 40,
                  color: _getColorFromType(item.tripItem.type),
                ),
                title: Text(item.tripItem.name),
                subtitle: Text(item.timeRangeString),
                trailing: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    if (item.tripItem.guides.isNotEmpty)  // guides가 비어있지 않을 때만 보여줌
                    IconButton(
                      icon: const Icon(Icons.directions_car),
                      onPressed: () {
                        _showGuidesDialog(context, item.tripItem.guides);  // 복수의 guides를 전달
                      },
                ),
                Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.access_time),
                    Text(item.travelTimeString),
                  ],
                ),
            ],
          ),

      onTap: () {
        showDialog(
          context: context,
          builder: (context) => TimeSelectionDialog(
            item: item,
            onTimeUpdated: (startTime, duration) {
              provider.updateItemTimeWithCascade(item, startTime, duration);  // Provider.of 대신 provider 직접 사용
            },
          ),
        );
      },
    );
  }
  );
  }

  void _showGuidesDialog(BuildContext context, List<Guide> guides) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('길 안내'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              for (var guide in guides)
                Padding(
                  padding: const EdgeInsets.symmetric(vertical: 8.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('${guide.distance}m 후에 ${guide.instructions}'),
                      Text('예상 소요시간: ${guide.duration}분'),
                      const Divider(),  // 각 안내 사이에 구분선 추가
                    ],
                  ),
                ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('확인'),
          ),
        ],
      ),
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
