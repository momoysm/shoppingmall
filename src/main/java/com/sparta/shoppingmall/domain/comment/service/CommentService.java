package com.sparta.shoppingmall.domain.comment.service;

import com.sparta.shoppingmall.common.exception.customexception.CommentNotFoundException;
import com.sparta.shoppingmall.domain.comment.dto.CommentRequest;
import com.sparta.shoppingmall.domain.comment.dto.CommentResponse;
import com.sparta.shoppingmall.domain.comment.entity.Comment;
import com.sparta.shoppingmall.domain.comment.repository.CommentRepository;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.product.service.ProductService;
import com.sparta.shoppingmall.domain.user.entity.User;
import com.sparta.shoppingmall.domain.user.entity.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final ProductService productService;
    private final CommentRepository commentRepository;

    /**
     * 댓글 등록
     */
    @Transactional
    public CommentResponse createComment(CommentRequest request, Long productId, User user) {
        Product product = productService.findByProductId(productId);
        Comment comment = Comment.createComment(request, user, product);

        commentRepository.save(comment);

        return CommentResponse.of(comment);
    }

    /**
     * 해당 상품의 댓글 전체 조회
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long productId) {
        Product product = productService.findByProductId(productId);
        List<Comment> comments = product.getComments();

        List<CommentResponse> response = new ArrayList<>();
        for(Comment comment : comments) {
            response.add(CommentResponse.of(comment));
        }

        return response;
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentResponse updateComments(CommentRequest request, Long productId, Long commentId, User user) {
        Comment comment = getComment(commentId);

        if(!user.getUserType().equals(UserType.ADMIN)){ // 관리자가 아닐 경우 댓글 작성자와 로그인 사용자를 비교
            comment.verifyCommentUser(user.getId());
        }
        comment.verifyCommentProduct(productId);
        comment.updateComment(request);

        return CommentResponse.of(comment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public Long deleteComment(Long productId, Long commentId, User user) {
        Comment comment = getComment(commentId);

        if(!user.getUserType().equals(UserType.ADMIN)){ // 관리자가 아닐 경우 댓글 작성자와 로그인 사용자를 비교
            comment.verifyCommentUser(user.getId());
        }
        comment.verifyCommentProduct(productId);
        commentRepository.delete(comment);

        return comment.getId();
    }

    /**
     * 댓글 아이디로 댓글 조회
     */
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("해당 댓글은 존재하지 않습니다.")
        );
    }
}
