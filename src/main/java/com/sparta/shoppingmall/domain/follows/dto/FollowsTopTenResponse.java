package com.sparta.shoppingmall.domain.follows.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FollowsTopTenResponse {

    private final Long userId;
    private final String username;
    private final String email;
    private final String address;
    private final LocalDateTime createAt;
    private final LocalDateTime updateAt;
    private final Long followerCount;

    public static FollowsTopTenResponse of(Long userId, String username, String email,
            String address, LocalDateTime createAt, LocalDateTime updateAt, Long followerCount) {
        return FollowsTopTenResponse.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .address(address)
                .createAt(createAt)
                .updateAt(updateAt)
                .followerCount(followerCount)
                .build();
    }

}
