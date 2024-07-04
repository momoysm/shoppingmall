package com.sparta.shoppingmall.domain.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.shoppingmall.domain.cart.entity.QCart;
import com.sparta.shoppingmall.domain.follows.entity.QFollows;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.product.entity.QProduct;
import com.sparta.shoppingmall.domain.user.entity.QUser;
import com.sparta.shoppingmall.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Product> getProductsFollow(Pageable pageable, User loginUser) {
        QUser user = QUser.user;
        QProduct product = QProduct.product;
        QFollows follows = QFollows.follows;
        QCart cart = QCart.cart;

        List<Product> productList = jpaQueryFactory
                .selectFrom(product)
                .leftJoin(follows).on(product.user.id.eq(follows.following.id))
                .join(product.user, user).fetchJoin()
                .join(user.cart, cart).fetchJoin()
                .where(follows.follower.id.eq(loginUser.getId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(product.count())
                .from(product)
                .leftJoin(follows).on(product.user.id.eq(follows.following.id))
                .where(follows.follower.id.eq(loginUser.getId()))
                .fetchOne();

        return PageableExecutionUtils.getPage(productList, pageable, () -> total);
    }

}
