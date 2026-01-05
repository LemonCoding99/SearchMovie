package com.searchmovie.domain.user.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.model.request.UserCreateRequest;
import com.searchmovie.domain.user.model.request.UserUpdateRequest;
import com.searchmovie.domain.user.model.response.UserCreateResponse;
import com.searchmovie.domain.user.model.response.UserGetResponse;
import com.searchmovie.domain.user.model.response.UserUpdateResponse;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public UserCreateResponse signup(UserCreateRequest request) {

        // password 암호화
        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(
                request.getName(),
                request.getUsername(),
                encodedPassword,
                request.getEmail()
        );
        User savedUser = userRepository.save(user);
        return UserCreateResponse.from(savedUser);
    }

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserGetResponse getUserOwn(Long userId, Long tokenUserId) {

        // 유저 존재 여부 검사
        User user = getUserOrThrow(userId);

        // 권한 검사
        if (!userId.equals(tokenUserId)) {
            throw new CustomException(ExceptionCode.NOT_HAVE_PERMISSION);
        }

        // deletedAt null여부로 조회처리
        return UserGetResponse.from(user);
    }

    // 내 회원 정보 수정
    @Transactional
    public UserUpdateResponse updateUser(Long userId, UserUpdateRequest request, Long tokenUserId) {

        // 유저 존재 여부 검사
        User user = getUserOrThrow(userId);

        // 권한 검사
        if (!userId.equals(tokenUserId)) {
            throw new CustomException(ExceptionCode.NOT_HAVE_PERMISSION);
        }

        String updatedName = request.getName();
        String updatedEmail = request.getEmail();
        String updatedPassword = request.getPassword();

        // 수정하지 않는 컬럼 값이 있을 경우 기존의 값으로 세팅해준 뒤 전달
        if (updatedName == null) {
            updatedName = user.getName();
        }

        if (updatedEmail == null) {
            updatedEmail = user.getEmail();
        }

        if (updatedPassword == null) {
            updatedPassword = user.getPassword();
        }

        // 세팅 완료된 request로 user 업데이트
        User updatedUser = user.update(updatedName, updatedEmail, updatedPassword);
        return UserUpdateResponse.from(updatedUser);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId, Long tokenUserId) {

        // 유저 존재 여부 검사
        User user = getUserOrThrow(userId);

        // 권한 검사
        if (!userId.equals(tokenUserId)) {
            throw new CustomException(ExceptionCode.NOT_HAVE_PERMISSION);
        }

        user.softDelete();
    }

    // 유저 존재 여부 검사
    private User getUserOrThrow(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ExceptionCode.USER_NOT_FOUND)
        );

        if (user.getDeletedAt() != null) {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }
        return user;
    }

}
