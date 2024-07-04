package com.sparta.shoppingmall.domain.product.dto;

import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.product.entity.ProductStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

    private final Long id;
    private final Long userId;
    private final String username;
    private final String name;
    private final Long price;
    private final ProductStatus status;

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .userId(product.getUser().getId())
                .username(product.getUser().getUsername())
                .name(product.getName())
                .price(product.getPrice())
                .status(product.getStatus())
                .build();
    }
}