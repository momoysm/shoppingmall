package com.sparta.shoppingmall.domain.product.dto;

import com.sparta.shoppingmall.domain.product.entity.ProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchCond {

    // 페이지 정보
    private Integer pageNum = 1; // 기본값 1
    private Integer pageSize = 5; // 기본값 5

    // where조건
    private ProductStatus status = ProductStatus.ONSALE; // 기본값 : 판매 중
    private String username = null; // 상품 등록자 아이디
    private Long productPrice = 0L; // 상품 가격

}
