package com.searchmovie.domain.user.entity;

import com.searchmovie.common.entity.BaseEntity;
import com.searchmovie.domain.user.dto.request.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    public User(String name, String username, String password, String email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User update(UserUpdateRequest request) {
        this.username = request.getUsername();
        this.name = request.getName();
        this.email = request.getEmail();
        this.password = request.getPassword();
        return this;
    }
}
