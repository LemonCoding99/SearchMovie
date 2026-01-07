package com.searchmovie.domain.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "name은 필수 입력 요소입니다.")
    private String name;

    @Pattern(regexp = "^([a-zA-Z0-9]{4,20})$",
            message = "아이디는 4~20자의 영문 또는 숫자만 가능합니다.")
    @NotBlank(message = "username은 필수 입력 요소입니다.")
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*#?&])([a-zA-Z0-9@$!%*#?&]{8,})$",
            message = "비밀번호는 대소문자 포함 영문, 숫자, 특수문자(@$!%*#?&)를 최소 1글자씩 포함하여 8자이상 입력해야 합니다.")
    @NotBlank(message = "password는 필수 입력 요소입니다.")
    private String password;

    @Email
    @NotBlank(message = "email은 필수 입력 요소입니다.")
    private String email;
}
