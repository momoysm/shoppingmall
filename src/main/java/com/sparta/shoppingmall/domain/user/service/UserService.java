package com.sparta.shoppingmall.domain.user.service;


import com.sparta.shoppingmall.common.exception.customexception.PasswordMismatchException;
import com.sparta.shoppingmall.common.exception.customexception.UserDuplicatedException;
import com.sparta.shoppingmall.common.exception.customexception.UserMismatchException;
import com.sparta.shoppingmall.common.jwt.RefreshTokenService;
import com.sparta.shoppingmall.domain.like.entity.ContentType;
import com.sparta.shoppingmall.domain.like.repository.LikesRepository;
import com.sparta.shoppingmall.domain.user.dto.*;
import com.sparta.shoppingmall.domain.user.entity.User;
import com.sparta.shoppingmall.domain.user.entity.UserStatus;
import com.sparta.shoppingmall.domain.user.entity.UserType;
import com.sparta.shoppingmall.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    /**
     * 일반회원 - 회원 가입
     */
    @Transactional
    public SignupResponse createUser(SignupRequest request) {
        //아이디 중복 검사
        validateUsername(request.getUsername());

        //비밀번호 암호화
        String password = passwordEncoder.encode(request.getPassword());
        User user = User.createUserWithCart(request, password, UserType.USER);

        userRepository.save(user);

        return new SignupResponse(user);
    }

    /**
     * 관리자 - 회원가입
     */
    @Transactional
    public SignupResponse createAdminUser(SignupRequest request) {
        //아이디 중복 검사
        validateUsername(request.getUsername());

        //비밀번호 암호화
        String password = passwordEncoder.encode(request.getPassword());
        User user = User.createUserWithCart(request, password, UserType.ADMIN);

        userRepository.save(user);

        return new SignupResponse(user);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public Long withdrawUser(WithdrawRequest request, User user) {
        //비밀번호 일치 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        //회원 상태 변경
        user.changeStatus(UserStatus.WITHDRAW);
        userRepository.save(user);

        return user.getId();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public Long logout(Long userId) {
        //사용자 조회
        User user = findById(userId);
        refreshTokenService.deleteToken(user.getUsername());

        return user.getId();
    }


    /**
     * 회원 조회 (유저 아이디)
     */
    public ProfileResponse inquiryUser(Long userId, User user) {
        if(!Objects.equals(user.getId(), userId)) {
            throw new UserMismatchException("사용자가 일치하지 않습니다.");
        }
        return likesRepository.getUserLikedProductComment(user);
    }


    /**
     * 회원 프로필 수정
     */
    @Transactional // 변경할 필드만 수정하고 바꾸지 않은 필드는 기존 데이터를 유지하는 메서드
    public UserResponse editProfile(Long userId, ProfileRequest request, User user) {
        if(!Objects.equals(userId, user.getId())){
            throw new UserMismatchException("사용자가 일치하지 않습니다.");
        }
        user.editProfile(request.getName(), request.getEmail(), request.getAddress());
        userRepository.save(user);

        return new UserResponse(user);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public UserResponse editPassword(EditPasswordRequest request, User user) {
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new PasswordMismatchException("새로운 비밀번호와 기존 비밀번호가 동일합니다.");
        }

        // 새로운 비밀번호가 최근 비밀번호들과 일치하는지 확인 - > 유효성 검사
        if (passwordEncoder.matches(request.getNewPassword(), user.getRecentPassword())
                || passwordEncoder.matches(request.getNewPassword(), user.getRecentPassword2())
                || passwordEncoder.matches(request.getNewPassword(), user.getRecentPassword3())) {
            throw new PasswordMismatchException("새로운 비밀번호는 최근 사용한 비밀번호와 다르게 설정해야 합니다.");
        }

        String editPassword = passwordEncoder.encode(request.getNewPassword());

        // 최근3개 비밀번호 저장
        user.changePassword(editPassword);
        userRepository.save(user);

        return new UserResponse(user);
    }


    /**
     * 회원 전체 조회 - 관리자
     */
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUserList() {
        List<User> userList = userRepository.findAll();
        List<AdminUserResponse> response = new ArrayList<>();
        for (User findUser : userList) {
            response.add(new AdminUserResponse(findUser));
        }
        return response;
    }

    /**
     * 회원 정보 수정 - 관리자
     */
    @Transactional
    public AdminUserResponse updateUser(AdminUpdateUserRequest request, Long userId) {
        User findUser = findById(userId);

        findUser.adminUpdateUser(request);

        return new AdminUserResponse(findUser);
    }

    // 회원정보 수정 - 관리자 변경 -> 회원 상태 변경, 회원 타입 변경 메서드 빼기

    /**
     * 회원 상태 확인
     */
    private void checkUserStatus(UserStatus userStatus) {
        if (userStatus.equals(UserStatus.WITHDRAW)) {
            throw new UserMismatchException("이미 탈퇴한 회원입니다.");
        }
    }

    /**
     * username 유효성 검사
     */
    private void validateUsername(String username) {
        Optional<User> findUser = userRepository.findByUsername(username);
        if (findUser.isPresent()) {
            throw new UserDuplicatedException("중복된 username 입니다.");
        }
    }

    /**
     * PK로 user 찾기
     */
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserMismatchException("해당 사용자는 존재하지 않습니다.")
        );
    }
}
