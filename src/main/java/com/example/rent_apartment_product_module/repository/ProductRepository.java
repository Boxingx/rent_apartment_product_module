package com.example.rent_apartment_product_module.repository;


import com.example.rent_apartment_product_module.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    ProductEntity getProductEntityByProductName(String name);
}
