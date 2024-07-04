package com.sparta.shoppingmall.domain.product.controller;

import static com.sparta.shoppingmall.common.util.ControllerUtil.getResponseEntity;

import com.sparta.shoppingmall.common.base.dto.CommonResponse;
import com.sparta.shoppingmall.common.security.UserDetailsImpl;
import com.sparta.shoppingmall.domain.product.dto.ProductPageResponse;
import com.sparta.shoppingmall.domain.product.dto.ProductRequest;
import com.sparta.shoppingmall.domain.product.dto.ProductResponse;
import com.sparta.shoppingmall.domain.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품등록
     */
    @PostMapping
    public ResponseEntity<CommonResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest productRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ProductResponse response = productService.createProduct(productRequest, userDetails.getUser().getId());
        return getResponseEntity(response, "상품 등록 성공");
    }

    /**
     * 상품조회(전체) get api/products
     */
    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductResponse>>> getProducts(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") final Integer pageNum,
            @RequestParam(value = "isDesc", required = false, defaultValue = "true") final Boolean isDesc
    ) {
        ProductPageResponse response = productService.getProducts(pageNum, isDesc);
        return getResponseEntity(response, "상품 목록 조회 성공");
    }


    /**
     * 상품조회(단일)get api/products/{productId}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductResponse>> getProduct(
            @PathVariable Long productId
    ){
        ProductResponse response = productService.getProduct(productId);
        return getResponseEntity(response, "상품 조회 성공");
    }

    /**
     * 팔로우한 유저의 상품 조회
     */
    @GetMapping("/following")
    public ResponseEntity<CommonResponse<ProductPageResponse>> getProductsFollow(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ProductPageResponse response = productService.getProductFollow(pageNum, userDetails.getUser());
        return getResponseEntity(response, "팔로우한 유저의 상품 조회 성공");
    }


    /**
     * 상품수정 api/products/{productId}
     */
    @PatchMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest productRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ProductResponse response = productService.updateProduct(productId, productRequest, userDetails.getUser());
        return getResponseEntity(response, "상품 수정 성공");
    }

    /**
     * 상품삭제 delete  api/products
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<CommonResponse<Long>> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        Long response = productService.deleteProduct(productId, userDetails.getUser());
        return getResponseEntity(response, "상품 삭제 성공");
    }
}

