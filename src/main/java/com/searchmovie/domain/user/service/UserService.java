package com.searchmovie.domain.user.service;

import com.searchmovie.domain.user.dto.request.UserCreateRequest;
import com.searchmovie.domain.user.dto.request.UserUpdateRequest;
import com.searchmovie.domain.user.dto.response.UserCreateResponse;
import com.searchmovie.domain.user.dto.response.UserGetResponse;
import com.searchmovie.domain.user.dto.response.UserUpdateResponse;
import com.searchmovie.domain.user.entity.User;
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

    @Transactional
    public UserCreateResponse signup(UserCreateRequest request) {

        // password 암호화
        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        request.setPassword(encodedPassword);

        User user = UserCreateRequest.of(request);
        User savedUser = userRepository.save(user);
        return UserCreateResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    public UserGetResponse getUserOwn(Long userId) {

        User user = userRepository.findById(userId).orElseThrow();
        // deletedAt null여부로 조회처리
        return UserGetResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserUpdateResponse updateUser(Long userId, UserUpdateRequest request) {

        User user = userRepository.findById(userId).orElseThrow();
        User updatedUser = user.update(request);
        return UserUpdateResponse.from(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId).orElseThrow();
        user.softDelete();
    }
}
