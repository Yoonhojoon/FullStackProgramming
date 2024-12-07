import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:google_place/google_place.dart';
import 'package:dio/dio.dart';
import '../entity/DailySchedule.dart';
import '../entity/TripItem.dart';

import '../entity/DailyPlan.dart';
import '../entity/Destination.dart';

// API 서비스 클래스
class ApiService {
  final Dio _dio = Dio(
    BaseOptions(
      baseUrl: "http://10.0.2.2:8080/api",
      connectTimeout: Duration(seconds: 5),
      receiveTimeout: Duration(seconds: 5),
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      }
    ),
  );

  // 장소 추가하기
  Future<bool> addDestination(Map<String, dynamic> destination) async {
    try {
      final response = await _dio.post("/destination", data: destination);
      return response.statusCode == 200 || response.statusCode == 201;
    } catch (e) {
      print("Error adding destination: $e");
      return false;
    }
  }

  // 기존 API 메서드들
  Future<List<dynamic>> getAllPosts() async {
    try {
      final response = await _dio.get("/posts");
      return response.data;
    } catch (e) {
      print("Error getting posts: $e");
      return [];
    }
  }

  Future<Map<String, dynamic>?> getPostById(int id) async {
    try {
      final response = await _dio.get("/posts/$id");
      return response.data;
    } catch (e) {
      print("Error getting post by ID: $e");
      return null;
    }
  }

  Future<void> createPost(Map<String, dynamic> post) async {
    try {
      await _dio.post("/posts", data: post);
    } catch (e) {
      print("Error creating post: $e");
    }
  }

  Future<void> deletePost(int id) async {
    try {
      await _dio.delete("/posts/$id");
    } catch (e) {
      print("Error deleting post: $e");
    }
  }

  Future<List<DailySchedule>> optimizeTrip({
    required Map<int, Destination> accommodations,
    required List<Destination> spots,
    String travelMode = 'DRIVING',
  }) async {
    try {
      final requestData = {
        'accommodationsByDay': accommodations.map(
                (key, value) => MapEntry(key.toString(), value.toJson())
        ),
        'spots': spots.map((spot) => spot.toJson()).toList()
      };

      final response = await _dio.post('/trips/optimize', data: requestData);
      print('API Response: ${response.data}');

      return (response.data as List)
          .map((x) => DailySchedule.fromJson(x))
          .toList();
    } catch (e) {
      throw Exception('Failed to optimize trip: ${e.toString()}');
    }
  }
}


