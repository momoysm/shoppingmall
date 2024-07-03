package com.sparta.shoppingmall.domain.like.dto;

import com.sparta.shoppingmall.domain.like.entity.LikeStatus;
import com.sparta.shoppingmall.domain.like.entity.Likes;
import com.sparta.shoppingmall.domain.like.entity.ContentType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikesResponse {

    private final Long id;
    private final ContentType contentType;
    private final Long contentId;
    private final LikeStatus status;

    public static LikesResponse of(Likes likes) {
        return LikesResponse.builder()
                .id(likes.getId())
                .contentType(likes.getContentType())
                .contentId(likes.getContentId())
                .status(likes.getStatus())
                .build();
    }

}
