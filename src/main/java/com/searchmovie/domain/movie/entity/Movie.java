package com.searchmovie.domain.movie.entity;

import com.searchmovie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "movies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = true)
    private String director;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    public Movie(String title, String director, LocalDate releaseDate) {
        this.title = title;
        this.director = director;
        this.releaseDate = releaseDate;
    }

    public void update(String title, String director, LocalDate releaseDate) {
        if (title != null) {
            this.title = title;
        }
        if (director != null) {
            this.director = director;
        }
        if (releaseDate != null) {
            this.releaseDate = releaseDate;
        }
    }
}
