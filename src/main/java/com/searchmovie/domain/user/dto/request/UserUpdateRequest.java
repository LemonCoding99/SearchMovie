package com.searchmovie.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private String username;
    private String name;
    private String email;
    private String password;
}
