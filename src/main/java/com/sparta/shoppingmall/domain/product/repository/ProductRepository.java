package com.sparta.shoppingmall.domain.product.repository;

import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long>, ProductRepositoryCustom{

    Page<Product> findAllByStatusIn(Pageable pageable, List<ProductStatus> statuses);
}
