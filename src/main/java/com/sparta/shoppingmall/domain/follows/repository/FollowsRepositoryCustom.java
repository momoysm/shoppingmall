package com.sparta.shoppingmall.domain.follows.repository;

import com.sparta.shoppingmall.domain.follows.dto.FollowsTopTenResponse;
import java.util.List;

public interface FollowsRepositoryCustom {

    List<FollowsTopTenResponse> getFollowsTopTen();

}
