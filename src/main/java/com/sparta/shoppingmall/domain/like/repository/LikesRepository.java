package com.sparta.shoppingmall.domain.like.repository;

import com.sparta.shoppingmall.domain.like.entity.ContentType;
import com.sparta.shoppingmall.domain.like.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long>, LikesRepositoryCustom {

    Optional<Likes> findByUserIdAndContentTypeAndContentId(Long id, ContentType contentType, Long contentId);

}
