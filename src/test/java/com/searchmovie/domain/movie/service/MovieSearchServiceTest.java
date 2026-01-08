package com.searchmovie.domain.movie.service;

import com.searchmovie.domain.movie.entity.Movie;
import com.searchmovie.domain.movie.model.response.MovieSearchResponse;
import com.searchmovie.domain.movie.model.response.MovieSelectCreateResponse;
import com.searchmovie.domain.movie.model.response.SimplePageResponse;
import com.searchmovie.domain.movie.repository.MovieRepository;
import com.searchmovie.domain.search.entity.HotKeyword;
import com.searchmovie.domain.search.repository.HotKeywordRepository;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MovieSearchServiceTest {

    @Autowired
    private MovieSearchService movieSearchService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private HotKeywordRepository hotKeywordRepository;


    @Test
    @DisplayName("영화_전체검색_정상작동_테스트")
    void search_movie_test() {

        // Given
        // 영화 제목으로 검색하는 경우를 가정하여 제작하였습니다.
        String title = "남산의 부장들";
        int page = 0;
        int size = 10;

        // When
        SimplePageResponse<MovieSearchResponse> result = movieSearchService.searchMovie1(title, null, null, null, null, page, size);


        // Then
        assertThat(result).isNotNull();

        MovieSearchResponse searchedMovie = result.getContent().get(0);

        assertThat(result.getContent().get(0).getTitle()).isEqualTo(title);
        assertThat(searchedMovie.getDirector()).isEqualTo("우민호");
        assertThat(searchedMovie.getGenres()).contains("드라마");

    }


    @Test
    @DisplayName("영화_전체검색_정상작동_테스트_Redis")
    void search_movie_redis_test() {
        // Given
        String title = "남산의 부장들";
        int page = 0;
        int size = 10;

        // When
        // 첫 번째 호출 (Cache Miss)
        SimplePageResponse<MovieSearchResponse> firstResult = movieSearchService.searchMovie3(title, null, null, null, null, page, size);

        // 두 번째 호출 (Cache Hit)
        SimplePageResponse<MovieSearchResponse> secondResult = movieSearchService.searchMovie3(title, null, null, null, null, page, size);

        // Then
        assertThat(firstResult).isNotNull();
        assertThat(secondResult).isNotNull();
        assertThat(firstResult.getContent().get(0).getTitle()).isEqualTo(secondResult.getContent().get(0).getTitle());  // first와 second 제목 일치하는지 확인

        String cacheKey = "search:title=남산의 부장들:director=null:genre=null:start=null:end=null:page=0:size=10";  // 캐시 키가 맞는 지 확인하기
    }


    @Test
    @DisplayName("영화_검색로그_생성_정상작동_테스트")
    void add_keyword_log_test() {

        // Given
        User testUser = new User("홍길동", "userId1", "Password!", "email1@naver.com");
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        Movie movie = movieRepository.findById(1L).orElseThrow(
                () -> new IllegalArgumentException("없는 영화입니다."));
        Long movieId = movie.getId();

        String keyword = "남산의 부장들";

        // When
        MovieSelectCreateResponse response = movieSearchService.createSelect(keyword, userId, movieId);

        // Then
        assertThat(response).isNotNull();

        // 로그 데이터 저장 되었는지 확인하기
        HotKeyword saved = hotKeywordRepository.findById(response.getId()).orElseThrow(
                () -> new IllegalArgumentException("검색 기록이 저장되지 않았습니다."));
        assertThat(saved.getKeyword()).isEqualTo(keyword);

    }
}
