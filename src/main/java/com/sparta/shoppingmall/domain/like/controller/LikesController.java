package com.sparta.shoppingmall.domain.like.controller;

import com.sparta.shoppingmall.common.base.dto.CommonResponse;
import com.sparta.shoppingmall.common.exception.customexception.LikeMismatchException;
import com.sparta.shoppingmall.common.security.UserDetailsImpl;
import com.sparta.shoppingmall.domain.comment.dto.CommentPageResponse;
import com.sparta.shoppingmall.domain.like.dto.LikesRequest;
import com.sparta.shoppingmall.domain.like.dto.LikesResponse;
import com.sparta.shoppingmall.domain.like.service.LikesService;
import com.sparta.shoppingmall.domain.product.dto.ProductPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.sparta.shoppingmall.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikesController {

    private final LikesService likesService;

    /**
     * 상품 좋아요 토글
     */
    @PostMapping("/products/{productId}/like")
    public ResponseEntity<CommonResponse<LikesResponse>> toggleProductLike (
            @PathVariable Long productId,
            @Valid @RequestBody LikesRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
       verifyPathVariable(productId, request.getContentId());

       LikesResponse response = likesService.toggleLike(request, userDetails.getUser());
       return getResponseEntity(response, "상품 좋아요 토글 성공");
    }

    /**
     * 댓글 좋아요 토글
     */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<CommonResponse<LikesResponse>> toggleCommentLike (
            @PathVariable Long commentId,
            @Valid @RequestBody LikesRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        verifyPathVariable(commentId, request.getContentId());

        LikesResponse response = likesService.toggleLike(request, userDetails.getUser());
        return getResponseEntity(response, "댓글 좋아요 토글 성공");
    }

    /**
     * 내가 좋아요 한 상품들 보기
     */
    @GetMapping("/products/like")
    public ResponseEntity<CommonResponse<ProductPageResponse>> getProductLikedList(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") final Integer pageNum,
            @RequestParam(value = "isDesc", required = false, defaultValue = "true") final Boolean isDesc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ProductPageResponse response = likesService.getProductLikedList(pageNum, isDesc, userDetails.getUser());
        return getResponseEntity(response, "좋아요한 상품 조회 성공");
    }

    /**
     * 내가 좋아요 한 댓글 보기
     */
    @GetMapping("/comments/like")
    public ResponseEntity<CommonResponse<CommentPageResponse>> getCommentLikedList(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") final Integer pageNum,
            @RequestParam(value = "isDesc", required = false, defaultValue = "true") final Boolean isDesc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        CommentPageResponse response = likesService.getCommentLikesList(pageNum, isDesc, userDetails.getUser());
        return getResponseEntity(response, "좋아요한 댓글 조회 성공");
    }

    /**
     * PathVariable값과 RequestBody 값을 비교
     */
    private void verifyPathVariable(Long contentId, Long requestId) {
        if (!Objects.equals(contentId, requestId)) {
            throw new LikeMismatchException("PathVariable의 contentId와 RequestBody의 contentId가 다릅니다.");
        }
    }

}
