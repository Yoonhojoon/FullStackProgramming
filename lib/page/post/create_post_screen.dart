import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mytour/service/post_service.dart';

import '../../entity/Category.dart';

class CreatePostScreen extends StatefulWidget {
  final VoidCallback onPostCreated;

  const CreatePostScreen({Key? key, required this.onPostCreated}) : super(key: key);

  @override
  _CreatePostScreenState createState() => _CreatePostScreenState();
}

class _CreatePostScreenState extends State<CreatePostScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _contentController = TextEditingController();
  final _postService = PostService();

  List<Category> categories = [];
  Set<Category> selectedCategories = {};
  File? _image;
  bool isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadCategories();
  }

  Future<void> _loadCategories() async {
    try {
      final loadedCategories = await _postService.getCategories();
      setState(() {
        categories = loadedCategories;
      });
    } catch (e) {
      // 에러 처리
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('카테고리를 불러오는데 실패했습니다')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('새 글 작성')),
      body: Form(
        key: _formKey,
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              TextFormField(
                controller: _titleController,
                decoration: const InputDecoration(
                  labelText: '제목',
                  border: OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value?.isEmpty ?? true) {
                    return '제목을 입력해주세요';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _contentController,
                decoration: const InputDecoration(
                  labelText: '내용',
                  border: OutlineInputBorder(),
                ),
                maxLines: 5,
                validator: (value) {
                  if (value?.isEmpty ?? true) {
                    return '내용을 입력해주세요';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text(
                    '카테고리 선택',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  TextButton.icon(
                    onPressed: _showAddCategoryDialog,
                    icon: const Icon(Icons.add),
                    label: const Text('카테고리 추가'),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8,
                children: categories.map((category) {
                  return FilterChip(
                    label: Text(category.categoryName),
                    selected: selectedCategories.contains(category),
                    onSelected: (selected) {
                      setState(() {
                        if (selected) {
                          selectedCategories.add(category);
                        } else {
                          selectedCategories.remove(category);
                        }
                      });
                    },
                  );
                }).toList(),
              ),
              const SizedBox(height: 16),
              ElevatedButton.icon(
                onPressed: _pickImage,
                icon: const Icon(Icons.image),
                label: const Text('이미지 선택'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.grey[200],
                  foregroundColor: Colors.black87,
                ),
              ),
              if (_image != null) ...[
                const SizedBox(height: 8),
                Image.file(
                  _image!,
                  height: 200,
                  width: double.infinity,
                  fit: BoxFit.cover,
                ),
              ],
              const SizedBox(height: 24),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: isLoading ? null : _submitPost,
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                  ),
                  child: isLoading
                      ? const CircularProgressIndicator()
                      : const Text('작성완료'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _pickImage() async {
    final picker = ImagePicker();
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);

    if (pickedFile != null) {
      setState(() {
        _image = File(pickedFile.path);
      });
    }
  }

  Future<void> _submitPost() async {
    if (!_formKey.currentState!.validate()) {  // 폼 검증
      return;
    }

    setState(() {
      isLoading = true;  // 로딩 시작
    });

    try {
      // createPost 호출
      await _postService.createPost({
        'postTitle': _titleController.text,
        'postContent': _contentController.text,
        'categoryIds': selectedCategories.map((category) => category.categoryId).toList(),
      }, _image);

      if (mounted) {
        Navigator.pop(context);  // 작성 화면 닫기
        widget.onPostCreated(); // 이전 화면 새로고침
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('게시글이 작성되었습니다')),
        );
      }
    } catch (e) {
      print('게시글 작성 에러: $e');  // 에러 로그 추가
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('게시글 작성에 실패했습니다')),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          isLoading = false;  // 로딩 종료
        });
      }
    }
  }

  @override
  void dispose() {
    _titleController.dispose();
    _contentController.dispose();
    super.dispose();
  }

  void _showAddCategoryDialog() {
    final TextEditingController categoryController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('새 카테고리 추가'),
        content: TextField(
          controller: categoryController,
          decoration: const InputDecoration(
            labelText: '카테고리 이름',
            border: OutlineInputBorder(),
          ),
          maxLength: 50,
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('취소'),
          ),
          FilledButton(
            onPressed: () async {
              if (categoryController.text.isNotEmpty) {
                try {
                  await _postService.createCategory({
                    'categoryName': categoryController.text,
                  });
                  Navigator.pop(context);
                  _loadCategories(); // 카테고리 목록 새로고침
                } catch (e) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('카테고리 추가에 실패했습니다')),
                  );
                }
              }
            },
            child: const Text('추가'),
          ),
        ],
      ),
    );
  }
}