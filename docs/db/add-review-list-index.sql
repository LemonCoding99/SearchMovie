-- 영화별 리뷰 목록 조회 성능 개선을 위한 복합 인덱스
CREATE INDEX idx_reviews_movie_created
    ON reviews (movie_id, deleted_at, created_at);