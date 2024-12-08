import 'dart:io';

import 'package:dio/dio.dart';
import 'package:mytour/entity/Category.dart';

import '../entity/Post.dart';

class PostService {
  final String baseUrl = 'http://10.0.2.2:8080';
  final dio = Dio();

  Future<List<Post>> getPosts() async {
    try {
      final response = await dio.get('$baseUrl/api/posts');
      return (response.data as List)
          .map((json) => Post.fromJson(json))
          .toList();
    } catch (e) {
      throw Exception('Failed to load posts');
    }
  }

  Future<void> createPost(Map<String, dynamic> postData, File? image) async {
    try {
      print('게시글 생성 시작');
      print('받은 데이터: $postData');

      String? imageUrl;
      if (image != null) {
        print('이미지 업로드 시작: ${image.path}');
        imageUrl = await uploadImage(image);
        print('업로드된 이미지 URL: $imageUrl');
      }

      print('게시글 POST 요청 시작: $baseUrl/api/posts');
      final requestData = {
        ...postData,
        'thumbnailImageUrl': imageUrl,
      };
      print('요청 데이터: $requestData');

      final response = await dio.post(
        '$baseUrl/api/posts',
        data: requestData,
      );
      print('응답 데이터: ${response.data}');

      return response.data;
    } catch (e) {
      print('게시글 생성 에러: $e');
      throw Exception('Failed to create post');
    }
  }


  Future<void> incrementViewCount(int postId) async {
    try {
      await dio.post('$baseUrl/api/posts/$postId/view');
    } catch (e) {
      throw Exception('Failed to increment view count');
    }
  }

  Future<void> toggleLike(int postId) async {
    try {
      await dio.post('$baseUrl/api/posts/$postId/like');
    } catch (e) {
      throw Exception('Failed to toggle like');
    }
  }

  Future<List<Category>> getCategories() async {
    try {
      print('카테고리 요청 시작: http://10.0.2.2:8080/api/categories');
      final response = await dio.get('$baseUrl/api/categories');
      print('응답 데이터 타입: ${response.data.runtimeType}');
      print('응답 데이터 내용: ${response.data}');

      // JSON 파싱 전에 데이터 확인
      if (response.data is! List) {
        print('WARNING: 응답이 리스트 형식이 아닙니다');
        // 실제 받은 데이터 구조 출력
        print('데이터 구조: ${response.data}');
      }

      final categories = (response.data as List)
          .map((json) => Category.fromJson(json))
          .toList();
      print('변환된 카테고리 목록: $categories');
      return categories;
    } catch (e) {
      print('카테고리 로드 에러: $e');
      print('에러 스택트레이스: ${StackTrace.current}');
      throw Exception('Failed to load categories');
    }
  }
// S3 이미지 업로드
  Future<String> uploadImage(File image) async {
    try {
      String fileName = image.path.split('/').last;
      FormData formData = FormData.fromMap({
        'file': await MultipartFile.fromFile(
          image.path,
          filename: fileName,
        ),
      });

      final response = await dio.post(
        '$baseUrl/api/upload',
        data: formData,
      );

      return response.data as String;
    } catch (e) {
      throw Exception('이미지 업로드에 실패했습니다');
    }
  }

  Future<Category> createCategory(Map<String, dynamic> categoryData) async {
    try {
      final response = await dio.post(
        '$baseUrl/api/categories',
        data: categoryData,
      );
      return Category.fromJson(response.data);
    } catch (e) {
      throw Exception('카테고리 생성에 실패했습니다');
    }
  }
}