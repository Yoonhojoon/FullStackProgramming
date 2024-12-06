import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:google_place/google_place.dart';
import 'package:dio/dio.dart';

// API 서비스 클래스
class ApiService {
  final Dio _dio = Dio(
    BaseOptions(
      baseUrl: "http://10.0.2.2:8080/api",
      connectTimeout: Duration(seconds: 5),
      receiveTimeout: Duration(seconds: 5),
    ),
  );

  // 장소 추가하기
  Future<bool> addDestination(Map<String, dynamic> placeData) async {
    try {
      final response = await _dio.post("/destinations" ,data: placeData);

      // HTTP 상태 코드가 201이면 성공
      if (response.statusCode == 201) {
        return true;
      } else {
        print('Failed to add destination: ${response.data}');
        return false;
      }
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
}