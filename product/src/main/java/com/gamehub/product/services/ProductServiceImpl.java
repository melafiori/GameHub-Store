package com.gamehub.product.services;

import com.gamehub.product.models.Product;
import com.gamehub.product.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return List.of();
    }

    @Override
    public Product findById(Long id) {
        return null;
    }

    @Override
    public Product findByMarca(String marca) {
        return null;
    }

    @Override
    public Product findByModelo(String modelo) {
        return null;
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Product updateById(Long id, Product product) {
        return null;
    }
}
