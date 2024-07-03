package com.sparta.shoppingmall.domain.comment.dto;

import com.sparta.shoppingmall.domain.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class CommentPageResponse {

    private final Integer currentPage;
    private final String totalComment;
    private final List<CommentResponse> commentList;


    public static CommentPageResponse of(Integer currentPage, String totalComment, Page<Comment> comments) {
        return CommentPageResponse.builder()
                .currentPage(currentPage)
                .totalComment(totalComment)
                .commentList(comments.getContent().stream().map(CommentResponse::of).toList())
                .build();
    }
}
