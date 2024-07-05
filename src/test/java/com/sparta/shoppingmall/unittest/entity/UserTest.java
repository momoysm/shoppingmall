package com.sparta.shoppingmall.unittest.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sparta.shoppingmall.domain.user.dto.AdminUpdateUserRequest;
import com.sparta.shoppingmall.domain.user.entity.User;
import com.sparta.shoppingmall.domain.user.entity.UserStatus;
import com.sparta.shoppingmall.domain.user.entity.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

    User user;

    @BeforeEach
    void initUser() {
        user = User.builder()
                .username("gildong")
                .password("Abcdefg123!")
                .name("홍길동")
                .email("test@gmail.com")
                .address("서울")
                .userStatus(UserStatus.JOIN)
                .userType(UserType.USER)
                .build();
    }

    @Test
    @DisplayName("프로필 수정")
    void updateProfileTest() {
        // when
        user.editProfile("길동홍", "test2@gmail.com", "대전");

        // then
        assertThat(user.getName()).isEqualTo("길동홍");
        assertThat(user.getEmail()).isEqualTo("test2@gmail.com");
        assertThat(user.getAddress()).isEqualTo("대전");
    }

    @Test
    @DisplayName("비밀번호 수정")
    void updatePassword() {
        // when
        user.changePassword("Abcdefg1234!");

        // then
        assertThat(user.getPassword()).isEqualTo("Abcdefg1234!");
        assertThat(user.getRecentPassword()).isEqualTo("Abcdefg123!");
        assertThat(user.getRecentPassword2()).isEqualTo(null);
        assertThat(user.getRecentPassword3()).isEqualTo(null);
    }

    @Test
    @DisplayName("회원 상태 변경")
    void updateStatus() {
        // when
        user.changeStatus(UserStatus.BLOCK);

        // then
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.BLOCK);
    }

    @Test
    @DisplayName("관리자 - 회원정보수정")
    void adminUpdateUser() {
        // given
        AdminUpdateUserRequest request = mock(AdminUpdateUserRequest.class);
        given(request.getUsername()).willReturn("gildong2");
        given(request.getPassword()).willReturn("Abcdefg1234!");
        given(request.getRecentPassword()).willReturn("Abcdefg123!");
        given(request.getRecentPassword2()).willReturn(null);
        given(request.getRecentPassword3()).willReturn(null);
        given(request.getName()).willReturn("길동홍2");
        given(request.getEmail()).willReturn("test2@gmail.com");
        given(request.getAddress()).willReturn("대전");
        given(request.getUserType()).willReturn(UserType.ADMIN);
        given(request.getUserStatus()).willReturn(UserStatus.JOIN);

        // when
        user.adminUpdateUser(request);

        //then
        assertThat(user.getUsername()).isEqualTo("gildong2");
        assertThat(user.getPassword()).isEqualTo("Abcdefg1234!");
        assertThat(user.getRecentPassword()).isEqualTo("Abcdefg123!");
        assertThat(user.getRecentPassword2()).isEqualTo(null);
        assertThat(user.getRecentPassword3()).isEqualTo(null);
        assertThat(user.getName()).isEqualTo("길동홍2");
        assertThat(user.getEmail()).isEqualTo("test2@gmail.com");
        assertThat(user.getAddress()).isEqualTo("대전");
        assertThat(user.getUserType()).isEqualTo(UserType.ADMIN);
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.JOIN);
    }

}
