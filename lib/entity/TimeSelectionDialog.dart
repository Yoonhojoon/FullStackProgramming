import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'ScheduleItem.dart';

class TimeSelectionDialog extends StatefulWidget {
  final ScheduleItem item;
  final Function(DateTime startTime, Duration duration) onTimeUpdated;

  const TimeSelectionDialog({
    Key? key,
    required this.item,
    required this.onTimeUpdated,
  }) : super(key: key);

  @override
  State<TimeSelectionDialog> createState() => _TimeSelectionDialogState();
}

class _TimeSelectionDialogState extends State<TimeSelectionDialog> {
  late TimeOfDay selectedStartTime;
  late int durationHours;
  late int durationMinutes;

  @override
  void initState() {
    super.initState();
    selectedStartTime = TimeOfDay.fromDateTime(widget.item.startTime);
    final totalMinutes = widget.item.endTime.difference(widget.item.startTime).inMinutes;
    durationHours = totalMinutes ~/ 60;
    durationMinutes = totalMinutes % 60;
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text('${widget.item.tripItem.name} 시간 설정'),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ListTile(
            title: const Text('시작 시간'),
            trailing: TextButton(
              child: Text(selectedStartTime.format(context)),
              onPressed: () async {
                final TimeOfDay? picked = await showTimePicker(
                  context: context,
                  initialTime: selectedStartTime,
                );
                if (picked != null) {
                  setState(() {
                    selectedStartTime = picked;
                  });
                }
              },
            ),
          ),
          ListTile(
            title: const Text('체류 시간'),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                DropdownButton<int>(
                  value: durationHours,
                  items: List.generate(24, (index) {
                    return DropdownMenuItem(
                      value: index,
                      child: Text('$index시간'),
                    );
                  }),
                  onChanged: (value) {
                    setState(() {
                      durationHours = value!;
                    });
                  },
                ),
                const SizedBox(width: 8),
                DropdownButton<int>(
                  value: durationMinutes,
                  items: List.generate(12, (index) {
                    return DropdownMenuItem(
                      value: index * 5,
                      child: Text('${index * 5}분'),
                    );
                  }),
                  onChanged: (value) {
                    setState(() {
                      durationMinutes = value!;
                    });
                  },
                ),
              ],
            ),
          ),
        ],
      ),
      actions: [
        TextButton(
          child: const Text('취소'),
          onPressed: () => Navigator.pop(context),
        ),
        TextButton(
          child: const Text('확인'),
          onPressed: () {
            final now = DateTime.now();
            final startTime = DateTime(
              now.year,
              now.month,
              now.day,
              selectedStartTime.hour,
              selectedStartTime.minute,
            );
            final duration = Duration(
              hours: durationHours,
              minutes: durationMinutes,
            );
            widget.onTimeUpdated(startTime, duration);
            Navigator.pop(context);
          },
        ),
      ],
    );
  }
}