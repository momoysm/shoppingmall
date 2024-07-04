package com.sparta.shoppingmall.domain.like.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.shoppingmall.domain.comment.entity.Comment;
import com.sparta.shoppingmall.domain.comment.entity.QComment;
import com.sparta.shoppingmall.domain.like.entity.ContentType;
import com.sparta.shoppingmall.domain.like.entity.QLikes;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.product.entity.QProduct;
import com.sparta.shoppingmall.domain.user.dto.ProfileResponse;
import com.sparta.shoppingmall.domain.user.entity.User;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikesRepositoryCustomImpl implements LikesRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 내가 좋아요한 상품 조회(페이징)
     */
    @Override
    public Page<Product> productLiked(Long userId, Pageable pageable){
        QLikes likes = QLikes.likes;
        QProduct product = QProduct.product;
        
        List<Product> productList = jpaQueryFactory
                .selectFrom(product)
                .join(likes).on(product.id.eq(likes.contentId)).fetchJoin()
                .where(likes.user.id.eq(userId), likes.contentType.eq(ContentType.PRODUCT))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(likes.count())
                .from(likes)
                .where(likes.user.id.eq(userId), likes.contentType.eq(ContentType.PRODUCT))
                .fetch().get(0);

        return PageableExecutionUtils.getPage(productList, pageable, () -> total);
    }

    /**
     * 내가 좋아요한 댓글 조회 (페이징)
     */
    @Override
    public Page<Comment> commentLiked(Long userId, Pageable pageable){
        QLikes likes = QLikes.likes;
        QComment comment = QComment.comment;

        List<Comment> commentList = jpaQueryFactory
                .selectFrom(comment)
                .join(likes).on(comment.id.eq(likes.contentId)).fetchJoin()
                .where(likes.user.id.eq(userId), likes.contentType.eq(ContentType.COMMENT))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(likes.count())
                .from(likes)
                .where(likes.user.id.eq(userId), likes.contentType.eq(ContentType.COMMENT))
                .fetch().get(0);

        return PageableExecutionUtils.getPage(commentList, pageable, () -> total);
    }

    /**
     * 사용자 프로필 조회(좋아요한 상품/댓글 수 포함)
     */
    @Override
    public ProfileResponse getUserLikedProductComment(User loginUser) {
        QLikes likes = QLikes.likes;

        List<Tuple> result = jpaQueryFactory
                .select(
                        likes.contentType,
                        likes.count().coalesce(0L)
                )
                .from(likes)
                .where(likes.user.id.eq(loginUser.getId()))
                .groupBy(likes.contentType)
                .fetch();

        Map<ContentType, Long> test = result.stream()
                .collect(Collectors.toMap(tuple -> tuple.get(likes.contentType)
                        , tuple -> tuple.get(likes.count())));

        return ProfileResponse.of(loginUser, test);
    }

}
