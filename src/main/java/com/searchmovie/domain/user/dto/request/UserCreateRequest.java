package com.searchmovie.domain.user.dto.request;

import com.searchmovie.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserCreateRequest {
    @NotBlank(message = "name은 필수 입력 요소입니다.")
    private String name;

    @NotBlank(message = "username은 필수 입력 요소입니다.")
    private String username;

    @NotBlank(message = "password는 필수 입력 요소입니다.")
    private String password;

    @Email
    @NotBlank(message = "email은 필수 입력 요소입니다.")
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
