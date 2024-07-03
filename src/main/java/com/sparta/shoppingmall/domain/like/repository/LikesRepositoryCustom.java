package com.sparta.shoppingmall.domain.like.repository;

import com.sparta.shoppingmall.domain.comment.entity.Comment;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.user.dto.ProfileResponse;
import com.sparta.shoppingmall.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikesRepositoryCustom {
    Page<Product> productLiked(Long userId, Pageable pageable);
    Page<Comment> commentLiked(Long userId, Pageable pageable);
    ProfileResponse getUserLikedProductComment(User user);
}
