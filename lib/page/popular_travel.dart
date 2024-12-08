import 'package:flutter/material.dart';
import 'package:mytour/entity/Category.dart';
import 'package:mytour/page/post/create_post_screen.dart';

import '../entity/Post.dart';
import '../service/post_service.dart';

class PopularTravel extends StatefulWidget {
  @override
  _PopularTravelState createState() => _PopularTravelState();
}

class _PopularTravelState extends State<PopularTravel> {
  final PostService _postService = PostService();
  List<Post> posts = [];
  List<Category> categories = [];
  String? selectedCategory;

  @override
  void initState() {
    super.initState();
    _loadInitialData();
  }

  Future<void> _loadInitialData() async {
    await Future.wait([
      _loadPosts(),
      _loadCategories(),
    ]);
  }

  Future<void> _loadPosts() async {
    try {
      final loadedPosts = await _postService.getPosts();
      setState(() {
        posts = loadedPosts;
      });
    } catch (e) {
      // 에러 처리
    }
  }

  Future<void> _loadCategories() async {
    try {
      final loadedCategories = await _postService.getCategories();
      setState(() {
        categories = loadedCategories;
      });
    } catch (e) {
      // 에러 처리
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: RefreshIndicator(
        onRefresh: _loadInitialData,
        child: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      '요즘 핫한 여행루트 🔥',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Row(
                      children: [
                        ElevatedButton.icon(
                          onPressed: () => _showCreatePostDialog(context),
                          icon: const Icon(Icons.add),
                          label: const Text('글작성'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.blue,
                            foregroundColor: Colors.white,
                          ),
                        ),
                        const SizedBox(width: 8),
                        TextButton(
                          onPressed: () {},
                          child: const Text('See all'),
                        ),
                      ],
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                ...posts.map((post) => _buildRouteCard(
                  post.postTitle,
                  post.thumbnailImageUrl ?? 'assets/images/placeholder.jpg',
                  post.postContent,
                  'Author',  // 사용자 정보 추가 필요
                  post.createdAt.toString(),
                )),
              ],
            ),
          ),
        ),
      ),
    );
  }
  void _showCreatePostDialog(BuildContext context) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => CreatePostScreen(
          onPostCreated: () {
            _loadPosts(); // 글작성 완료 후 목록 새로고침
          },
        ),
      ),
    );
  }

  Widget _buildRouteCard(String title, String imagePath, String description,
      String author, String date) {
    print(imagePath);
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        children: [
          // 게시글 헤더
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                CircleAvatar(
                  radius: 16,
                  child: Text(author[0]),
                ),
                const SizedBox(width: 8),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      author,
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    Text(
                      date,
                      style: TextStyle(
                        color: Colors.grey[600],
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),

          // 메인 컨텐츠
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(8),
                  child: Image.network(  // Image.asset을 Image.network로 변경
                    imagePath,
                    width: 80,
                    height: 80,
                    fit: BoxFit.cover,
                    // 로딩 중 표시할 위젯
                    loadingBuilder: (context, child, loadingProgress) {
                      if (loadingProgress == null) return child;
                      return SizedBox(
                        width: 80,
                        height: 80,
                        child: Center(
                          child: CircularProgressIndicator(),
                        ),
                      );
                    },
                    // 에러 발생 시 표시할 위젯
                    errorBuilder: (context, error, stackTrace) {
                      return SizedBox(
                        width: 80,
                        height: 80,
                        child: Icon(Icons.error),
                      );
                    },
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        title,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        description,
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),

          // 상호작용 버튼
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8.0),
            child: Row(
              children: [
                IconButton(
                  icon: const Icon(Icons.favorite_border),
                  onPressed: () async {
                    // 좋아요 기능 구현
                  },
                ),
                const Text('0'),
                const SizedBox(width: 16),
                const Icon(Icons.remove_red_eye_outlined),
                const Text('0'),
              ],
            ),
          ),
        ],
      ),
    );
  }
}