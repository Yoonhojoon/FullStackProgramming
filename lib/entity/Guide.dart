class Guide {
  final int distance;
  final int duration;
  final String instructions;
  final int type;
  final int pointIndex;

  Guide({
    required this.distance,
    required this.duration,
    required this.instructions,
    required this.type,
    required this.pointIndex,
  });

  factory Guide.fromJson(Map<String, dynamic> json) {
    return Guide(
      distance: json['distance'] as int,  // 정수라고 명시
      duration: json['duration'] as int,  // 정수라고 명시
      instructions: json['instructions'] as String,  // 문자열이라고 명시
      type: json['type'] as int,         // 정수라고 명시
      pointIndex: json['pointIndex'] as int,  // 정수라고 명시
    );
  }
}