package com.gamehub.product.services;

import com.gamehub.product.exceptions.ProductException;
import com.gamehub.product.models.Product;
import com.gamehub.product.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Product> findAll() {
        return this.productRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Product findById(Long id) {
        return this.productRepository.findById(id).orElseThrow(
        () -> new ProductException("Producto con id: " + id + " no encontrado.")
        );
    }

    @Override
    public Product findBySku(String sku) {
        return this.productRepository.findBySku(sku).orElseThrow(
                ()-> new ProductException("Producto con sku: " + sku + " no encontrado.")
        );
    }


    @Transactional
    @Override
    public Product save(Product product) {
        if (this.productRepository.findBySku(product.getSku()).isPresent()){
            throw new ProductException("Producto con SKU proporcionado ya existe.");
        }

        return this.productRepository.save(product);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {

    }

    @Transactional
    @Override
    public Product updateById(Long id, Product product) {
        return this.productRepository.findById(id).map(element -> {
            element.setEstado(product.getEstado());
            element.setPrecio(product.getPrecio());
            element.setDescripcion(product.getDescripcion());
            return this.productRepository.save(element);
        }).orElseThrow(
                () -> new ProductException("El producto con Id " + id + " no existe.")
        );
    }
}
