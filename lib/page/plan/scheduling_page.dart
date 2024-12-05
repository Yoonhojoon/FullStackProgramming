import 'package:flutter/material.dart';
import 'package:calendar_date_picker2/calendar_date_picker2.dart';
import 'package:provider/provider.dart';
import 'package:mytour/destination_provider.dart';

class SchedulingPage extends StatefulWidget {
  @override
  _SchedulingPageState createState() => _SchedulingPageState();
}

class _SchedulingPageState extends State<SchedulingPage> {
  List<DateTime?> _selectedDates = [];
  bool _isExpanded = true;
  Map<int, Map<String, dynamic>> _selectedAccommodations = {};

  List<Map<String, dynamic>> _getAccommodations(DestinationProvider provider) {
    return provider.destinations
        .where((dest) => dest['type'] == 'ACCOMMODATION')
        .toList();
  }

  void _showDestinationList(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('여행 계획 리스트'),
        content: SizedBox(
          width: double.maxFinite,
          height: 400,
          child: Consumer<DestinationProvider>(
            builder: (context, provider, child) {
              return ListView.builder(
                itemCount: provider.destinations.length,
                itemBuilder: (context, index) {
                  final destination = provider.destinations[index];
                  return ListTile(
                    title: Text(destination['name'] ?? ''),
                    subtitle: Text(destination['address'] ?? ''),
                  );
                },
              );
            },
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('닫기'),
          ),
        ],
      ),
    );
  }

  Widget _buildAccommodationSelection(BuildContext context) {
    if (_selectedDates.isEmpty) return SizedBox();

    return Consumer<DestinationProvider>(
      builder: (context, provider, child) {
        final accommodations = _getAccommodations(provider);

        return Container(
          margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          height: 200,
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(12),
            boxShadow: [
              BoxShadow(
                color: Colors.grey.withOpacity(0.2),
                spreadRadius: 1,
                blurRadius: 6,
                offset: Offset(0, 3),
              ),
            ],
          ),
          child: SingleChildScrollView(
            child: Column(
              children: List.generate(
                _selectedDates.length - 1,
                    (index) => Container(
                  decoration: BoxDecoration(
                    border: Border(
                      bottom: BorderSide(
                        color: Colors.grey.withOpacity(0.2),
                        width: 1,
                      ),
                    ),
                  ),
                  child: ListTile(
                    contentPadding: EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                    leading: CircleAvatar(
                      backgroundColor: Color(0xFFFF7800).withOpacity(0.2),
                      child: Text(
                        '${index + 1}일',
                        style: TextStyle(
                          color: Color(0xFFFF7800),
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    title: Text(
                      _selectedAccommodations[index]?['name'] ?? '숙박지를 선택하세요',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    subtitle: _selectedAccommodations[index] != null
                        ? Text(
                      _selectedAccommodations[index]!['address'],
                      style: TextStyle(
                        fontSize: 14,
                        color: Colors.grey[600],
                      ),
                    )
                        : null,
                    trailing: Icon(
                      Icons.chevron_right,
                      color: Color(0xFFFF7800),
                    ),
                    onTap: () {
                      showDialog(
                        context: context,
                        builder: (context) => AlertDialog(
                          title: Text('${index + 1}일차 숙박지 선택'),
                          content: SizedBox(
                            width: double.maxFinite,
                            height: 400,
                            child: ListView.builder(
                              shrinkWrap: true,
                              itemCount: accommodations.length,
                              itemBuilder: (context, accIndex) {
                                final accommodation = accommodations[accIndex];
                                return Container(
                                  decoration: BoxDecoration(
                                    border: Border(
                                      bottom: BorderSide(
                                        color: Colors.grey.withOpacity(0.1),
                                      ),
                                    ),
                                  ),
                                  child: ListTile(
                                    contentPadding: EdgeInsets.symmetric(
                                      horizontal: 16,
                                      vertical: 8,
                                    ),
                                    title: Text(
                                      accommodation['name'],
                                      style: TextStyle(fontWeight: FontWeight.w500),
                                    ),
                                    subtitle: Text(accommodation['address']),
                                    onTap: () {
                                      setState(() {
                                        _selectedAccommodations[index] = accommodation;
                                      });
                                      Navigator.pop(context);
                                    },
                                  ),
                                );
                              },
                            ),
                          ),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(15),
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ),
            ),
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('계획 짜기'),
        actions: [
          IconButton(
            icon: Icon(Icons.settings),
            onPressed: () {},
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: SingleChildScrollView(
              child: Column(
                children: [
                  CalendarDatePicker2(
                    config: CalendarDatePicker2Config(
                      calendarType: CalendarDatePicker2Type.multi,
                      selectedDayHighlightColor: Colors.orange,
                    ),
                    value: _selectedDates,
                    onValueChanged: (dates) {
                      setState(() {
                        _selectedDates = dates..sort();
                        _selectedAccommodations.clear();
                      });
                    },
                  ),
                  _buildAccommodationSelection(context),
                ],
              ),
            ),
          ),
          Container(
            padding: EdgeInsets.all(20),
            child: Column(
              children: [
                ElevatedButton(
                  onPressed: () => _showDestinationList(context),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.white,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15),
                    ),
                    padding: EdgeInsets.symmetric(horizontal: 40, vertical: 15),
                  ),
                  child: Text(
                    '여행 계획 리스트 보기',
                    style: TextStyle(
                      color: Color(0xFFFF7800),
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                SizedBox(height: 10),
                ElevatedButton(
                  onPressed: () {
                    // 여행 계획 생성 로직
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Color(0xFFFF7800).withOpacity(0.6),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15),
                    ),
                    padding: EdgeInsets.symmetric(horizontal: 40, vertical: 15),
                  ),
                  child: Text(
                    '여행 계획 만들기',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}