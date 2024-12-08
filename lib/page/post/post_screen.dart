import 'package:flutter/material.dart';
import 'package:mytour/service/api_service.dart';

class PostScreen extends StatelessWidget {
  final ApiService apiService = ApiService();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Posts"),
      ),
      body: FutureBuilder<List<dynamic>>(
        future: apiService.getAllPosts(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text("Error: ${snapshot.error}"));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return Center(child: Text("No posts available"));
          }

          final posts = snapshot.data!;
          return ListView.builder(
            itemCount: posts.length,
            itemBuilder: (context, index) {
              final post = posts[index];
              return ListTile(
                leading: Image.network(
                  post['thumbnailImageUrl'],
                  width: 50,
                  height: 50,
                  fit: BoxFit.cover,
                  // 로딩 중 표시할 위젯
                  loadingBuilder: (context, child, loadingProgress) {
                    if (loadingProgress == null) return child;
                    return SizedBox(
                      width: 50,
                      height: 50,
                      child: Center(
                        child: CircularProgressIndicator(
                          value: loadingProgress.expectedTotalBytes != null
                              ? loadingProgress.cumulativeBytesLoaded /
                              loadingProgress.expectedTotalBytes!
                              : null,
                        ),
                      ),
                    );
                  },
                  // 에러 발생 시 표시할 위젯
                  errorBuilder: (context, error, stackTrace) {
                    return SizedBox(
                      width: 50,
                      height: 50,
                      child: Icon(Icons.error),
                    );
                  },
                ),
                title: Text(post['postTitle']),
                subtitle: Text(post['postContent']),
              );
            },
          );
        },
      ),
    );
  }
}
