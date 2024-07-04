package com.sparta.shoppingmall.domain.follows.controller;

import static com.sparta.shoppingmall.common.util.ControllerUtil.getResponseEntity;

import com.sparta.shoppingmall.common.base.dto.CommonResponse;
import com.sparta.shoppingmall.common.security.UserDetailsImpl;
import com.sparta.shoppingmall.domain.follows.dto.FollowsResponse;
import com.sparta.shoppingmall.domain.follows.dto.FollowsTopTenResponse;
import com.sparta.shoppingmall.domain.follows.service.FollowsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowsController {

    private final FollowsService followsService;

    /**
     * 팔로우
     */
    @PostMapping("/{followingId}")
    public ResponseEntity<CommonResponse<FollowsResponse>> followUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        FollowsResponse response = followsService.followUser(followingId, userDetails.getUser());
        return getResponseEntity(response, "팔로우 성공");
    }

    /**
     * 팔로우 취소
     */
    @DeleteMapping("/{followingId}")
    public ResponseEntity<CommonResponse<FollowsResponse>> followCancel(
            @PathVariable Long followingId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        FollowsResponse response = followsService.followCancel(followingId, userDetails.getUser());
        return getResponseEntity(response, "팔로우 취소 성공");
    }

    /**
     * 팔로잉 목록 조회
     */
    @GetMapping("/followings")
    public ResponseEntity<CommonResponse<List<FollowsResponse>>> getFollowings(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<FollowsResponse> response = followsService.getFollowings(userDetails.getUser());
        return getResponseEntity(response, "팔로잉 목록 조회 성공");
    }

    /**
     * 팔로워 목록 조회
     */
    @GetMapping("/followers")
    public ResponseEntity<CommonResponse<List<FollowsResponse>>> getFollowers(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<FollowsResponse> response = followsService.getFollowers(userDetails.getUser());
        return getResponseEntity(response, "팔로워 목록 조회 성공");
    }

    /**
     * 관리자 - 팔로우 취소
     */
    @Secured("ADMIN")
    @DeleteMapping("/{followerId}/{followingId}")
    public ResponseEntity<CommonResponse<FollowsResponse>> followCancelAdmin(
            @PathVariable(name = "followerId") Long followerId,
            @PathVariable(name = "followingId") Long followingId
    ) {
        FollowsResponse response = followsService.followCancelAdmin(followerId, followingId);
        return getResponseEntity(response, "해당 사용자의 팔로우 취소 성공");
    }

    /**
     * 관리자 - 해당 사용자의 팔로잉 목록 조회
     */
    @Secured("ADMIN")
    @GetMapping("/{followerId}/follower")
    public ResponseEntity<CommonResponse<List<FollowsResponse>>> getFollowingsAdmin(
            @PathVariable Long followerId
    ) {
        List<FollowsResponse> response = followsService.getFollowingsAdmin(followerId);
        return getResponseEntity(response, "해당 사용자의 팔로잉 목록 조회 성공");
    }

    /**
     * 관리자 - 해당 사용자의 팔로워 목록 조회
     */
    @Secured("ADMIN")
    @GetMapping("/{followingId}/following")
    public ResponseEntity<CommonResponse<List<FollowsResponse>>> getFollowersAdmin(
            @PathVariable Long followingId
    ) {
        List<FollowsResponse> response = followsService.getFollowersAdmin(followingId);
        return getResponseEntity(response, "해당 사용자의 팔로워 목록 조회 성공");
    }

    /**
     * 팔로워 TOP10 조회
     */
    @GetMapping("/top-ten")
    public ResponseEntity<CommonResponse<List<FollowsTopTenResponse>>> getFollowsTopTen() {
        List<FollowsTopTenResponse> response = followsService.getFollowsTopTen();
        return getResponseEntity(response, "팔로워수 TOP10 조회 성공");
    }

}
