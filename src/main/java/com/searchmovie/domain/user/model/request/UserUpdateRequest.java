package com.searchmovie.domain.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserUpdateRequest {

    private String name;

    @Email
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*#?&])([a-zA-Z0-9@$!%*#?&]{8,})$",
            message = "비밀번호는 대소문자 포함 영문, 숫자, 특수문자(@$!%*#?&)를 최소 1글자씩 포함하여 8자이상 입력해야 합니다.")
    private String password;
}