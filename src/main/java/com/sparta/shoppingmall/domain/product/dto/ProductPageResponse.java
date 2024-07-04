package com.sparta.shoppingmall.domain.product.dto;

import com.sparta.shoppingmall.domain.product.entity.Product;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class ProductPageResponse {
    private final Integer currentPage;
    private final String totalProducts;
    private final List<ProductResponse> productList;

    public static ProductPageResponse of(Integer currentPage, String totalProducts, Page<Product> products) {
        return ProductPageResponse.builder()
                .currentPage(currentPage)
                .totalProducts(totalProducts)
                .productList(products.getContent().stream().map(ProductResponse::of).toList())
                .build();
    }
}
