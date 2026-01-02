package com.searchmovie.domain.user.service;

import com.searchmovie.domain.user.dto.request.UserCreateRequest;
import com.searchmovie.domain.user.dto.request.UserUpdateRequest;
import com.searchmovie.domain.user.dto.response.UserCreateResponse;
import com.searchmovie.domain.user.dto.response.UserGetResponse;
import com.searchmovie.domain.user.dto.response.UserUpdateResponse;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserCreateResponse signup(UserCreateRequest request) {

        User user = UserCreateRequest.of(request);
        User savedUser = userRepository.save(user);
        return UserCreateResponse.from(savedUser);
    }

    public UserGetResponse getUserOwn(Long userId) {

        User user = userRepository.findById(userId).orElseThrow();
        return UserGetResponse.from(user);
    }

    public UserUpdateResponse updateUser(Long userId, UserUpdateRequest request) {

        User user = userRepository.findById(userId).orElseThrow();
        User updatedUser = user.update(request);
        return UserUpdateResponse.from(updatedUser);
    }

    public void deleteUser(Long userId) {

        userRepository.deleteById(userId);
    }
}
