package com.gamehub.product.services;

import com.gamehub.product.clients.CategoryClient;
import com.gamehub.product.exceptions.ProductException;
import com.gamehub.product.models.Product;
import com.gamehub.product.models.dtos.CategoryDto;
import com.gamehub.product.models.dtos.ProductDetalleDto;
import com.gamehub.product.repositories.ProductRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryClient categoryClient;

    @Override
    public List<ProductDetalleDto> findAll() {
        return this.productRepository.findAll().stream().map(p -> {
            ProductDetalleDto productoDetalle = new ProductDetalleDto();
            productoDetalle.setId(p.getProductId());
            productoDetalle.setNombre(p.getNombre());
            productoDetalle.setMarca(p.getMarca());
            productoDetalle.setModelo(p.getModelo());
            productoDetalle.setPrecio(p.getPrecio());
            productoDetalle.setDescripcion(p.getDescripcion());
            productoDetalle.setEstado(p.getEstado());

            try {
                // Saco la información desde category client
                CategoryDto categoryDTO = this.categoryClient.getCategoryById(p.getCategoryId());
                productoDetalle.setCategoria(categoryDTO);
            } catch (FeignException exception) {
                // Si falla la comunicación o no existe, lanza la excepción
                throw new ProductException("La categoría asociada al producto no existe");
            }
            return productoDetalle;
        }).toList();
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
        Product product = this.findById(id);

        this.productRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Product updateById(Long id, Product product) {
        return this.productRepository.findById(id).map(element -> {
            element.setNombre(product.getNombre());
            element.setEstado(product.getEstado());
            element.setPrecio(product.getPrecio());
            element.setDescripcion(product.getDescripcion());

            try {
                this.categoryClient.getCategoryById(product.getCategoryId());
                element.setCategoryId(product.getCategoryId());
            } catch (FeignException exception) {
                throw new ProductException("La categoría con Id " + product.getProductId() + " no existe.");
            }

            return this.productRepository.save(element);
        }).orElseThrow(
                () -> new ProductException("El producto con Id " + id + " no existe.")
        );
    }
}
