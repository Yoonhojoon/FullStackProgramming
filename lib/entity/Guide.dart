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
      distance: json['distance'],
      duration: json['duration'],
      instructions: json['instructions'],
      type: json['type'],
      pointIndex: json['pointIndex'],
    );
  }
}