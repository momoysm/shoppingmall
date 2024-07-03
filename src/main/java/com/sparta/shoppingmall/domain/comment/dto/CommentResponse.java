package com.sparta.shoppingmall.domain.comment.dto;

import com.sparta.shoppingmall.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private final Long id;

    private final String content;

    private final int likeCount;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    public static CommentResponse of(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreateAt())
                .updatedAt(comment.getUpdateAt())
                .build();
    }

}
