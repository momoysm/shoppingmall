package com.sparta.shoppingmall.domain.product.repository;

import static com.sparta.shoppingmall.domain.product.entity.QProduct.product;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.shoppingmall.domain.cart.entity.QCart;
import com.sparta.shoppingmall.domain.follows.entity.QFollows;
import com.sparta.shoppingmall.domain.product.dto.ProductSearchCond;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.product.entity.ProductStatus;
import com.sparta.shoppingmall.domain.product.entity.QProduct;
import com.sparta.shoppingmall.domain.user.entity.QUser;
import com.sparta.shoppingmall.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Product> getProductsFollow(Pageable pageable, User loginUser, ProductSearchCond searchCond) {
        QUser user = QUser.user;
        QProduct product = QProduct.product;
        QFollows follows = QFollows.follows;
        QCart cart = QCart.cart;

        List<Product> productList = jpaQueryFactory
                .selectFrom(product)
                .leftJoin(follows).on(product.user.id.eq(follows.following.id))
                .join(product.user, user).fetchJoin()
                .join(user.cart, cart).fetchJoin()
                .where(
                        follows.follower.id.eq(loginUser.getId()),
                        productStatusEq(searchCond.getStatus()),
                        productPriceEq(searchCond.getProductPrice()),
                        usernameLike(searchCond.getUsername())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(product.count())
                .from(product)
                .leftJoin(follows).on(product.user.id.eq(follows.following.id))
                .where(
                        follows.follower.id.eq(loginUser.getId()),
                        productStatusEq(searchCond.getStatus()),
                        productPriceEq(searchCond.getProductPrice()),
                        usernameLike(searchCond.getUsername())
                )
                .fetch().get(0);

        return PageableExecutionUtils.getPage(productList, pageable, () -> total);
    }

    private BooleanExpression productStatusEq(final ProductStatus productStatus){
        return product.status.eq(productStatus);
    }

    private BooleanExpression usernameLike(final String username){
        if(!StringUtils.hasText(username)) return null;
        return product.user.username.like(username);
    }

    private BooleanExpression productPriceEq(final Long productPrice){
        if(productPrice < 100L) return null;
        return product.price.eq(productPrice);
    }

}
