package com.sparta.shoppingmall.domain.like.service;

import com.sparta.shoppingmall.common.exception.customexception.UserMismatchException;
import com.sparta.shoppingmall.common.util.PageUtil;
import com.sparta.shoppingmall.domain.comment.dto.CommentPageResponse;
import com.sparta.shoppingmall.domain.comment.dto.CommentResponse;
import com.sparta.shoppingmall.domain.comment.entity.Comment;
import com.sparta.shoppingmall.domain.comment.service.CommentService;
import com.sparta.shoppingmall.domain.like.dto.LikesResponse;
import com.sparta.shoppingmall.domain.like.entity.ContentType;
import com.sparta.shoppingmall.domain.like.entity.LikeStatus;
import com.sparta.shoppingmall.domain.like.entity.Likes;
import com.sparta.shoppingmall.domain.like.repository.LikesRepository;
import com.sparta.shoppingmall.domain.like.dto.LikesRequest;
import com.sparta.shoppingmall.domain.like.repository.LikesRepositoryImpl;
import com.sparta.shoppingmall.domain.product.dto.ProductPageResponse;
import com.sparta.shoppingmall.domain.product.dto.ProductResponse;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.product.service.ProductService;
import com.sparta.shoppingmall.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikesService {

    private final ProductService productService;
    private final CommentService commentService;
    private final LikesRepository likesRepository;

    /**
     * 좋아요 토글
     */
    @Transactional
    public LikesResponse toggleLike(LikesRequest request, User user) {
        //자신의 게시물 또는 댓글인지 확인
        if(ContentType.PRODUCT.equals(request.getContentType())) {
            Product product = productService.findByProductId(request.getContentId());
            if(user.equals(product.getUser())){
                throw new UserMismatchException("자신의 게시물에 좋아요를 누를 수 없습니다.");
            }
        }else if(ContentType.COMMENT.equals(request.getContentType())) {
            Comment comment = commentService.getComment(request.getContentId());
            if(user.equals(comment.getUser())){
                throw new UserMismatchException("자신의 댓글에 좋아요를 누를 수 없습니다.");
            }
        }

        Optional<Likes> existLike = likesRepository.findByUserIdAndContentTypeAndContentId(user.getId(), request.getContentType(), request.getContentId());

        Likes likes = existLike.orElseGet(() -> createLikes(request, user));

        if (likes.getStatus().equals(LikeStatus.CANCELED)) {
            doLike(likes, user.getId());// 취소된 좋아요 이거나 신규 좋아요인 경우 좋아요
        }else {
            cancelLike(likes, user.getId());
        }

        return LikesResponse.of(likes);

    }

    /**
     * 좋아요 생성 (해당 content에 최초 좋아요 실행일 때)
     */
    private Likes createLikes(LikesRequest request, User user) {
        Likes likes = Likes.createLike(request, user);
        likesRepository.save(likes);

        return likes;
    }

    /**
     * 좋아요
     */
    private void doLike(Likes likes, Long userId) {
        likes.doLike(userId);

        Long contentId = likes.getContentId();
        switch (likes.getContentType()){
            case PRODUCT -> productService.findByProductId(contentId).increaseLikeCount();
            case COMMENT -> commentService.getComment(contentId).increaseLikeCount();
        }
    }

    /**
     * 좋아요 취소
     */
    private void cancelLike(Likes likes, Long userId) {
        likes.cancelLike(userId);

        Long contentId = likes.getContentId();
        switch (likes.getContentType()){
            case PRODUCT -> productService.findByProductId(contentId).decreaseLikeCount();
            case COMMENT -> commentService.getComment(contentId).decreaseLikeCount();
        }
    }

    /**
     * 내가 좋아요 한 상품들 보기
     */
    @Transactional(readOnly = true)
    public ProductPageResponse getProductLikedList(Integer pageNum, Boolean isDesc, User user) {
        Pageable pageable = PageUtil.createPageable(pageNum, PageUtil.PAGE_SIZE_FIVE, isDesc);
        Page<Product> productList = likesRepository.productLiked(user.getId(), pageable);
        String totalProduct = PageUtil.validateAndSummarizePage(pageNum, productList);

        return ProductPageResponse.of(pageNum, totalProduct, productList);
    }

    /**
     * 내가 좋아요한 댓글 보기
     */
    public CommentPageResponse getCommentLikesList(Integer pageNum, Boolean isDesc, User user) {
        Pageable pageable = PageUtil.createPageable(pageNum, PageUtil.PAGE_SIZE_FIVE, isDesc);
        Page<Comment> commentList = likesRepository.commentLiked(user.getId(), pageable);
        String totalComment = PageUtil.validateAndSummarizePage(pageNum, commentList);

        return CommentPageResponse.of(pageNum, totalComment, commentList);
    }
}
