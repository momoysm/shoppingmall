package com.sparta.shoppingmall.domain.user.dto;


import com.sparta.shoppingmall.domain.like.entity.ContentType;
import com.sparta.shoppingmall.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class  ProfileResponse {

    private final Long id;

    private final String username;

    private final String email;

    private final String address;

    private final Long productLikedCount;

    private final Long commentLikedCount;

    private final LocalDateTime createAt;

    private final LocalDateTime updateAt;


    public static ProfileResponse of(User user, Map<ContentType, Long> likeCountMap) {
        return ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .productLikedCount(likeCountMap.get(ContentType.PRODUCT))
                .commentLikedCount(likeCountMap.get(ContentType.COMMENT))
                .createAt(user.getCreateAt())
                .updateAt(user.getUpdateAt())
                .build();
    }
}
