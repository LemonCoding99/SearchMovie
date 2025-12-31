package com.searchmovie.domain.user.dto.request;

import com.searchmovie.domain.user.entity.User;
import lombok.Getter;

@Getter
public class CreateUserRequest {
    private String name;
    private String username;
    private String password;
    private String email;

    public static User of(CreateUserRequest request) {
        return new User(
                request.name,
                request.username,
                request.password,
                request.email
        );
    }
}
