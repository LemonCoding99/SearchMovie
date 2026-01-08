CREATE INDEX idx_search_logs_keyword_searched_at
    ON search_logs (keyword, searched_at);

CREATE INDEX idx_search_logs_movie_id
    ON search_logs (movie_id);