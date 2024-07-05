package com.sparta.shoppingmall.unittest.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.shoppingmall.common.exception.customexception.PasswordMismatchException;
import com.sparta.shoppingmall.common.exception.customexception.UserMismatchException;
import com.sparta.shoppingmall.common.jwt.RefreshTokenRepository;
import com.sparta.shoppingmall.domain.like.repository.LikesRepository;
import com.sparta.shoppingmall.domain.user.dto.EditPasswordRequest;
import com.sparta.shoppingmall.domain.user.dto.ProfileRequest;
import com.sparta.shoppingmall.domain.user.entity.User;
import com.sparta.shoppingmall.domain.user.repository.UserRepository;
import com.sparta.shoppingmall.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    User user;

    @Mock
    private UserRepository userRepository;
    @Mock
    private LikesRepository likesRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("프로필 수정 성공 테스트")
    void updateProfileTest_success() {
        Long userId = 1L;

        ProfileRequest request = mock(ProfileRequest.class);
        given(request.getName()).willReturn("홍길동2");
        given(request.getEmail()).willReturn("test2@gamil.com");
        given(request.getAddress()).willReturn("대전");

        when(user.getId()).thenReturn(userId);

        userService.editProfile(userId, request, user);

        verify(user).editProfile(request.getName(), request.getEmail(), request.getAddress());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("프로필 수정 실패 테스트")
    void updateProfileTest_fail_1() {
        Long userId = 1L;
        ProfileRequest request = mock(ProfileRequest.class);

        when(user.getId()).thenReturn(2L);

        assertThrows(UserMismatchException.class, () -> {
            userService.editProfile(userId, request, user);
        });
    }

    @Test
    @DisplayName("비밀번호 변경 성공 테스트")
    void updatePasswordTest_success() {
        // given
        given(user.getPassword()).willReturn("encodedOldPassword");
        given(user.getRecentPassword()).willReturn("encodedRecentPassword1");
        given(user.getRecentPassword2()).willReturn("encodedRecentPassword2");
        given(user.getRecentPassword3()).willReturn("encodedRecentPassword3");

        EditPasswordRequest request = mock(EditPasswordRequest.class);
        given(request.getPassword()).willReturn("Abcdefg123!");
        given(request.getNewPassword()).willReturn("ABcdefg123!");

        String encodedNewPassword = "encodedNewPassword";

        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(true);
        given(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).willReturn(false);
        given(passwordEncoder.matches(request.getNewPassword(), user.getRecentPassword())).willReturn(false);
        given(passwordEncoder.matches(request.getNewPassword(), user.getRecentPassword2())).willReturn(false);
        given(passwordEncoder.matches(request.getNewPassword(), user.getRecentPassword3())).willReturn(false);
        given(passwordEncoder.encode(request.getNewPassword())).willReturn(encodedNewPassword);

        // when
        userService.editPassword(request, user);

        verify(user).changePassword(encodedNewPassword);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트1")
    void updatePasswordTest_fail1() {
        // given
        given(user.getPassword()).willReturn("encodedOldPassword");

        EditPasswordRequest request = mock(EditPasswordRequest.class);

        // when
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // then
        assertThrows(PasswordMismatchException.class, () -> {
            userService.editPassword(request, user);
        });
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트2")
    void updatePasswordTest_fail2() {
        // given
        given(user.getPassword()).willReturn("Abcdefg123!");

        EditPasswordRequest request = mock(EditPasswordRequest.class);
        given(request.getPassword()).willReturn("Abcdefg123!");
        given(request.getNewPassword()).willReturn("ABcdefg123!");

        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(true);

        assertThrows(PasswordMismatchException.class, () -> {
            userService.editPassword(request, user);
        });
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트3")
    void updatePasswordTest_fail3() {
        // given
        given(user.getPassword()).willReturn("encodedOldPassword");
        given(user.getRecentPassword()).willReturn("encodedRecentPassword1");

        EditPasswordRequest request = mock(EditPasswordRequest.class);
        given(request.getPassword()).willReturn("Abcdefg123!");
        given(request.getNewPassword()).willReturn("ABcdefg123!");

        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.matches(request.getNewPassword(), user.getRecentPassword())).thenReturn(true);

        assertThrows(PasswordMismatchException.class, () -> {
            userService.editPassword(request, user);
        });
    }

}
