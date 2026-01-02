package com.searchmovie.domain.user.dto.request;

import com.searchmovie.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserCreateRequest {
    private String name;
    private String username;
    private String password;
    private String email;

    public static User of(UserCreateRequest request) {
        return new User(
                request.name,
                request.username,
                request.password,
                request.email
        );
    }
}
