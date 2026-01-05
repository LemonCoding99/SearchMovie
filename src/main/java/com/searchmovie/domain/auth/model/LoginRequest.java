package com.searchmovie.domain.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginRequest {

    @Pattern(regexp = "^([a-zA-Z0-9]{4,20})$",
            message = "아이디는 4~20자의 영문 또는 숫자만 가능합니다.")
    @NotBlank(message = "username과 password는 필수입니다.")
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*#?&])([a-zA-Z0-9@$!%*#?&]{8,})$",
            message = "비밀번호는 대소문자 포함 영문, 숫자, 특수문자(@$!%*#?&)를 최소 1글자씩 포함하여 8자이상 입력해야 합니다.")
    @NotBlank(message = "username과 password는 필수입니다.")
    private String password;


}
