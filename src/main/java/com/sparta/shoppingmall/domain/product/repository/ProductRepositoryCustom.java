package com.sparta.shoppingmall.domain.product.repository;

import com.sparta.shoppingmall.domain.product.dto.ProductSearchCond;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> getProductsFollow(Pageable pageable, User loginUser, ProductSearchCond searchCond);
}
