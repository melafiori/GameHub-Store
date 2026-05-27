package com.gamehub.product.services;

import com.gamehub.product.models.Product;
import com.gamehub.product.models.dtos.ProductDetalleDto;

import java.util.List;

public interface ProductService {
    List<ProductDetalleDto> findAll();
    Product findById(Long id);
    Product findBySku(String sku);
    Product save(Product product);
    void deleteById(Long id);
    Product updateById(Long id, Product product);

}
