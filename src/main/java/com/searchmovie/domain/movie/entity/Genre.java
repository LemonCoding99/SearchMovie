package com.searchmovie.domain.movie.entity;

import com.searchmovie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table( // 같은 장르가 여러개 들어가는 것을 방지 (중복데이터방지)
        name = "genres",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_genres_name", columnNames = "name")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Genre extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    public Genre(String name) {
        this.name = name;
    }
}
