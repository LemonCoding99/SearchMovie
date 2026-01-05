package com.searchmovie.domain.movie.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class SimplePageResponse<T> {

    private List<T> content;

    private long totalElements;
    private int totalPages;
    private int size;
    private int number;

    public static <T> SimplePageResponse<T> from(Page<T> page) {
        return new SimplePageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }
}