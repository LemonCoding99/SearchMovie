package com.searchmovie.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UpdateUserRequest {
    private String username;
    private String name;
    private String email;
    private String password;
}
