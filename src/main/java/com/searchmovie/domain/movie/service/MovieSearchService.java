package com.searchmovie.domain.movie.service;

import com.searchmovie.domain.movie.model.response.MovieSearchResponse;
import com.searchmovie.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MovieSearchService {

    private final MovieRepository movieRepository;

    @Transactional(readOnly = true)
    public Page<MovieSearchResponse> searchMovie(String title, String director, String genreKeyword, LocalDate releaseDateStart, LocalDate releaseDateEnd, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        return movieRepository.search(title, director, genreKeyword, releaseDateStart, releaseDateEnd, pageable);
    }

}
