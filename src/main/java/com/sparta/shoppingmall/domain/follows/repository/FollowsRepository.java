package com.sparta.shoppingmall.domain.follows.repository;

import com.sparta.shoppingmall.domain.follows.entity.Follows;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowsRepository extends JpaRepository<Follows, Long>, FollowsRepositoryCustom {

    Optional<Follows> findByFollowingIdAndFollowerId(Long followingId, Long id);

}
