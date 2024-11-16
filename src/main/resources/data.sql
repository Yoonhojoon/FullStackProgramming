INSERT INTO users (
    username,
    google_id,
    email,
    role,
    created_at,
    updated_at
) VALUES
      ('홍길동', 'google_123456789', 'hong.gildong@gmail.com', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('김여행', 'google_987654321', 'kim.travel@gmail.com', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('이관리', 'google_456789123', 'lee.admin@gmail.com', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('박여행', 'google_789123456', 'park.travel@gmail.com', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('정모험', 'google_321654987', 'jung.adventure@gmail.com', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Board 테이블 데이터 (Board 엔티티의 구조에 따라 수정이 필요할 수 있습니다)
INSERT INTO board (board_name, created_at, updated_at)
VALUES
    ('여행 후기',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('여행 계획',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('추천 여행지', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Category 테이블 데이터
INSERT INTO category (category_name, created_at, updated_at)
VALUES
    ('국내여행', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('해외여행', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('도시여행', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('자연여행', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('맛집투어', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('문화탐방', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Post 테이블 데이터
INSERT INTO post (
    post_title,
    post_content,
    thumbnail_image_url,
    content_image_url,
    start_date,
    end_date,
    created_at,
    updated_at,
    view_count,
    like_count,
    post_category,
    board_id
) VALUES
      ('제주도 3박 4일 여행기',
       '제주도의 아름다운 해변과 맛있는 음식들을 소개합니다...',
       'https://example.com/jeju-thumbnail.jpg',
       'https://example.com/jeju-content.jpg',
       '2024-03-01',
       '2024-03-04',
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP,
       120,
       15,
       '국내여행',
       1),

      ('방콕 5일 꿀팁 공유',
       '방콕에서 꼭 가봐야 할 곳들과 맛집 리스트입니다...',
       'https://example.com/bangkok-thumbnail.jpg',
       'https://example.com/bangkok-content.jpg',
       '2024-02-01',
       '2024-02-05',
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP,
       85,
       10,
       '해외여행',
       1),

      ('부산 여행 계획 공유',
       '부산 2박 3일 일정 계획입니다...',
       'https://example.com/busan-thumbnail.jpg',
       'https://example.com/busan-content.jpg',
       '2024-04-01',
       '2024-04-03',
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP,
       45,
       5,
       '국내여행',
       2);


INSERT INTO itinerary (
    title,
    description,
    start_date,
    end_date,
    user_id,
    created_at,
    updated_at
) VALUES
      ('제주도 여행 계획', '제주도 3박 4일 여행 일정', '2024-04-01', '2024-04-04', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('방콕 여행 계획', '방콕 5박 6일 여행 일정', '2024-05-01', '2024-05-06', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('부산 여행 계획', '부산 2박 3일 여행 일정', '2024-06-01', '2024-06-03', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
