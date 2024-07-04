package com.sparta.shoppingmall.domain.follows.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.shoppingmall.domain.follows.dto.FollowsTopTenResponse;
import com.sparta.shoppingmall.domain.follows.entity.QFollows;
import com.sparta.shoppingmall.domain.user.entity.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowsRepositoryCustomImpl implements FollowsRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FollowsTopTenResponse> getFollowsTopTen() {
        QUser user = QUser.user;
        QFollows follows = QFollows.follows;

        return jpaQueryFactory
                .select(Projections.constructor(FollowsTopTenResponse.class,
                        user.id,
                        user.username,
                        user.email,
                        user.address,
                        user.createAt,
                        user.updateAt,
                        follows.count()
                        )
                )
                .from(user)
                .innerJoin(follows).on(user.id.eq(follows.following.id))
                .groupBy(user.id)
                .orderBy(follows.count().desc())
                .limit(10)
                .fetch();
    }

}
