package com.searchmovie.domain.user.service;

import com.searchmovie.domain.user.dto.request.CreateUserRequest;
import com.searchmovie.domain.user.dto.request.UpdateUserRequest;
import com.searchmovie.domain.user.dto.response.CreateUserResponse;
import com.searchmovie.domain.user.dto.response.GetUserResponse;
import com.searchmovie.domain.user.dto.response.UpdateUserResponse;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public CreateUserResponse signup(CreateUserRequest request) {

        User user = CreateUserRequest.of(request);
        User savedUser = userRepository.save(user);
        return CreateUserResponse.from(savedUser);
    }

    public GetUserResponse getUserOwn(Long userId) {

        User user = userRepository.findById(userId).orElseThrow();
        return GetUserResponse.from(user);
    }

    public UpdateUserResponse updateUser(Long userId, UpdateUserRequest request) {

        User user = userRepository.findById(userId).orElseThrow();
        User updatedUser = user.update(user);
        return UpdateUserResponse.from(updatedUser);
    }

    public void deleteUser(Long userId) {

        userRepository.deleteById(userId);
    }
}
