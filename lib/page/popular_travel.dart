import 'package:flutter/material.dart';

class PopularTravel extends StatefulWidget {
  @override
  _PopularTravelState createState() => _PopularTravelState();
}

class _PopularTravelState extends State<PopularTravel> {
  // 예시 데이터
  final Map<String, bool> likedPosts = {};
  final Map<String, List<String>> comments = {
    '남원 여행 테크트리': [
      '좋은 정보 감사합니다!',
      '광한루원이 정말 좋았어요',
    ],
  };

  @override
  Widget build(BuildContext context) {
    return Column(
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
            TextButton(
              onPressed: () {},
              child: const Text('See all'),
            ),
          ],
        ),
        const SizedBox(height: 8),
        _buildRouteCard(
          '남원 여행 테크트리',
          'assets/images/namwon.jpg',
          '남원의 대표적인 여행지와 명소들을 소개하는 루트입니다.',
          '김여행',
          '2024.12.08',
        ),
      ],
    );
  }

  Widget _buildRouteCard(String title, String imagePath, String description,
      String author, String date) {
    bool isLiked = likedPosts[title] ?? false;
    List<String> postComments = comments[title] ?? [];

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
                  child: Image.asset(
                    imagePath,
                    width: 80,
                    height: 80,
                    fit: BoxFit.cover,
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
                  icon: Icon(
                    isLiked ? Icons.favorite : Icons.favorite_border,
                    color: isLiked ? Colors.red : null,
                  ),
                  onPressed: () {
                    setState(() {
                      likedPosts[title] = !isLiked;
                    });
                  },
                ),
                Text('${isLiked ? 1 : 0}'),
                const SizedBox(width: 16),
                IconButton(
                  icon: const Icon(Icons.comment_outlined),
                  onPressed: () {
                    _showCommentDialog(context, title);
                  },
                ),
                Text('${postComments.length}'),
              ],
            ),
          ),

          // 댓글 섹션
          if (postComments.isNotEmpty)
            Container(
              padding: const EdgeInsets.all(8.0),
              decoration: BoxDecoration(
                color: Colors.grey[50],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: postComments.map((comment) => Padding(
                  padding: const EdgeInsets.symmetric(vertical: 4.0),
                  child: Text(comment),
                )).toList(),
              ),
            ),
        ],
      ),
    );
  }

  void _showCommentDialog(BuildContext context, String postTitle) {
    final TextEditingController commentController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('댓글 작성'),
        content: TextField(
          controller: commentController,
          decoration: const InputDecoration(
            hintText: '댓글을 입력하세요',
          ),
          maxLines: 3,
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('취소'),
          ),
          TextButton(
            onPressed: () {
              if (commentController.text.isNotEmpty) {
                setState(() {
                  comments[postTitle] = [
                    ...comments[postTitle] ?? [],
                    commentController.text,
                  ];
                });
                Navigator.pop(context);
              }
            },
            child: const Text('등록'),
          ),
        ],
      ),
    );
  }
}