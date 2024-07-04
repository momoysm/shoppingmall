package com.sparta.shoppingmall.domain.cart.service;

import com.sparta.shoppingmall.common.exception.customexception.CartRejectedException;
import com.sparta.shoppingmall.common.exception.customexception.ProductNotFoundException;
import com.sparta.shoppingmall.common.util.PageUtil;
import com.sparta.shoppingmall.domain.cart.dto.CartProductRequest;
import com.sparta.shoppingmall.domain.cart.dto.CartProductResponse;
import com.sparta.shoppingmall.domain.cart.dto.CartResponse;
import com.sparta.shoppingmall.domain.cart.entity.Cart;
import com.sparta.shoppingmall.domain.cart.entity.CartProduct;
import com.sparta.shoppingmall.domain.cart.repository.CartProductRepository;
import com.sparta.shoppingmall.domain.cart.repository.CartRepository;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.product.service.ProductService;
import com.sparta.shoppingmall.domain.user.entity.User;
import com.sparta.shoppingmall.domain.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final UserService userService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    /**
     * 장바구니에 상품 단건 담기
     */
    @Transactional
    public CartProductResponse addCartProduct(CartProductRequest request, User user) {
        User loginUser = userService.findById(user.getId());
        Product product = productService.findByProductId(request.getProductId());
        //상품 상태 체크
        if(!product.checkProductStatus()){
            throw new CartRejectedException("판매 중인 상품이 아닙니다.");
        }
        Cart cart = loginUser.getCart();

        //장바구니 안에 상품 중복 확인
        cart.getCartProducts().forEach(cartProduct -> cartProduct.verifyCartProduct(product.getId()));

        CartProduct cartProduct = CartProduct.createCartProduct(cart, product);
        cart.addCartProduct(cartProduct);
        cartRepository.save(cart);

        return CartProductResponse.of(cartProduct);
    }

    /**
     * 장바구니에 상품 리스트 조회
     */
    @Transactional
    public CartResponse getCartProducts(Integer pageNum, Boolean isDesc, User user) {
        Pageable pageable = PageUtil.createPageable(pageNum, PageUtil.PAGE_SIZE_FIVE, isDesc);
        Cart cart = user.getCart();

        //상품 상태 확인 후 삭제
        List<CartProduct> cartProductList = cart.getCartProducts();
        for (CartProduct cartProduct : cartProductList) {
            if(!cartProduct.getProduct().checkProductStatus()){
                cartProductRepository.delete(cartProduct);
            }
        }

        Page<CartProduct> cartProducts = cartProductRepository.findAllByCartId(pageable, cart.getId());
        String totalCartProduct = PageUtil.validateAndSummarizePage(pageNum, cartProducts);

        return CartResponse.of(pageNum, totalCartProduct, cartProducts);
    }

    /**
     * 장바구니에 상품 단건 삭제
     */
    @Transactional
    public Long deleteCartProduct(Long productId, User user) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new CartRejectedException("상품을 담은 후 시도해주세요.")
        );

        CartProduct cartProduct = cartProductRepository.findByProductId(productId).orElseThrow(
                () -> new ProductNotFoundException("해당 상품이 장바구니에 없습니다.")
        );
        cart.removeCartProduct(cartProduct);

        return cartProduct.getId();
    }
}
