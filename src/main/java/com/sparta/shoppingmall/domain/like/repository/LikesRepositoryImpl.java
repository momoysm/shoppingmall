package com.sparta.shoppingmall.domain.like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.shoppingmall.domain.comment.entity.Comment;
import com.sparta.shoppingmall.domain.comment.entity.QComment;
import com.sparta.shoppingmall.domain.like.entity.ContentType;
import com.sparta.shoppingmall.domain.like.entity.QLikes;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.product.entity.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikesRepositoryImpl implements LikesRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

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

//    private List<OrderSpecifier> getOrderSpecifier(Sort sort) {
//        List<OrderSpecifier> orders = new ArrayList<>();
//
//        sort.stream().forEach(order -> {
//            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
//            String prop = order.getProperty();
//            PathBuilder orderByExpression = new PathBuilder(Product.class, "product");
//            orders.add(new OrderSpecifier(direction, orderByExpression.get(prop)));
//        });
//
//        return orders;
//    }

}
