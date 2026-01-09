package com.searchmovie.domain.user.service;

import com.searchmovie.common.enums.ExceptionCode;
import com.searchmovie.common.exception.CustomException;
import com.searchmovie.domain.user.entity.User;
import com.searchmovie.domain.user.entity.UserRole;
import com.searchmovie.domain.user.model.request.UserCreateRequest;
import com.searchmovie.domain.user.model.request.UserUpdateRequest;
import com.searchmovie.domain.user.model.response.UserCreateResponse;
import com.searchmovie.domain.user.model.response.UserGetResponse;
import com.searchmovie.domain.user.model.response.UserUpdateResponse;
import com.searchmovie.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testAdminUser;
    private String encodedPassword = "{bcrypt}$2b$12$mvnmEHGcl2KqJg/8udGbjek/katAUSctc5gxuMpBL6105tkrCrwHG";

    @BeforeEach
    void setup() {
        // raw password = Password123!
        testUser = new User("testname", "testUserName", encodedPassword, "test@example.com");
        testAdminUser = new User("adminTestname", "testAdminName", encodedPassword, "adminTest@example.com");

        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testAdminUser, "id", 2L);
        ReflectionTestUtils.setField(testAdminUser, "role", UserRole.ADMIN);
    }

    @Test
    void signup_success() {
        // given
        UserCreateRequest request = mock(UserCreateRequest.class);
        when(request.getName()).thenReturn("test name");
        when(request.getUsername()).thenReturn("test username");
        when(request.getPassword()).thenReturn("Password123!");
        when(request.getEmail()).thenReturn("email@test.com");
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        UserCreateResponse response = userService.signup(request);

        // then
        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserOwn_success() {

        // given
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));

        Long tokenUserId = 1L;

        // when
        UserGetResponse response =
                userService.getUserOwn(1L, tokenUserId);

        // then
        assertNotNull(response);
        verify(userRepository).findById(1L);
    }


    @Test
    @DisplayName("유저 조회 실패 - 조회하고자 하는 유저가 존재하지 않을 때")
    void getUserOwn_failed_v1() {
        // given
        Long userId = 1L;
        Long tokenUserId = 1L;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getUserOwn(userId, tokenUserId);
        });

        // then
        assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 조회 실패 - 타인의 계정 정보를 조회하고자할때 (권한 없음)")
    void getUserOwn_failed_v2() {
        // given
        Long userId = 1L;
        Long tokenUserId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testAdminUser));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getUserOwn(userId, tokenUserId);
        });

        // then
        assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.NOT_HAVE_PERMISSION);
    }

    @Test
    void updateUser_success() {

        // given
        Long userId = 1L;
        Long tokenUserId = 1L;

        UserUpdateRequest request = mock(UserUpdateRequest.class);
        when(request.getName()).thenReturn("test_updated_name");
        when(request.getEmail()).thenReturn("test@updated.com");
        when(request.getPassword()).thenReturn("testPassword123!");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // when
        UserUpdateResponse response = userService.updateUser(userId, request, tokenUserId);

        // then
        assertThat(response.getName()).isEqualTo("test_updated_name");
        assertThat(response.getEmail()).isEqualTo("test@updated.com");
    }

    @Test
    @DisplayName("유저 수정 실패 - 수정하고자 하는 유저가 존재하지 않을 때")
    void updateUser_failed_v1() {
        // given
        Long userId = 1L;
        Long tokenUserId = 1L;
        UserUpdateRequest request = mock(UserUpdateRequest.class);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.updateUser(userId, request, tokenUserId);
        });

        // then
        assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 수정 실패 - 타인의 계정 정보를 수정하고자할때 (권한 없음)")
    void updateUser_failed_v2() {
        // given
        Long userId = 1L;
        Long tokenUserId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testAdminUser));

        UserUpdateRequest request = mock(UserUpdateRequest.class);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.updateUser(userId, request, tokenUserId);
        });

        // then
        assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.NOT_HAVE_PERMISSION);
    }

    @Test
    void deleteUser_success() {

        // given
        Long userId = 1L;
        Long tokenUserId = 1L;
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.deleteUser(userId, tokenUserId);

        // then
        assertThat(user.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("유저 삭제 실패 - 삭제하고자 하는 유저가 이미 존재하지 않을 때")
    void deleteUsers_failed_v1() {

        // given
        Long userId = 1L;
        Long tokenUserId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> userService.deleteUser(userId, tokenUserId));

        // then
        assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("유저 삭제 실패 - 타인의 계정을 삭제하려고하는 상황일 때(권한이 없을 때)")
    void deleteUsers_failed_v2() {
        // given
        Long userId = 1L;
        Long tokenUserId = 2L;

        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> userService.deleteUser(userId, tokenUserId));

        // then
        assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.NOT_HAVE_PERMISSION);
    }
}