class Post {
  final int postId;
  final String postTitle;
  final String postContent;
  final String? thumbnailImageUrl;
  final String? contentImageUrl;
  final DateTime? startDate;
  final DateTime? endDate;
  final DateTime createdAt;
  final DateTime updatedAt;
  final int viewCount;
  final int likeCount;
  final String? postCategory;

  Post({
    required this.postId,
    required this.postTitle,
    required this.postContent,
    this.thumbnailImageUrl,
    this.contentImageUrl,
    this.startDate,
    this.endDate,
    required this.createdAt,
    required this.updatedAt,
    required this.viewCount,
    required this.likeCount,
    this.postCategory,
  });

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      postId: json['postId'],
      postTitle: json['postTitle'],
      postContent: json['postContent'],
      thumbnailImageUrl: json['thumbnailImageUrl'],
      contentImageUrl: json['contentImageUrl'],
      startDate: json['startDate'] != null ? DateTime.parse(json['startDate']) : null,
      endDate: json['endDate'] != null ? DateTime.parse(json['endDate']) : null,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      viewCount: json['viewCount'] ?? 0,
      likeCount: json['likeCount'] ?? 0,
      postCategory: json['postCategory'],
    );
  }
}