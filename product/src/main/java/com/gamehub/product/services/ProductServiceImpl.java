package com.gamehub.product.services;

import com.gamehub.product.clients.CategoryClient;
import com.gamehub.product.clients.InventoryClient;
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

    @Autowired
    private InventoryClient inventoryClient;

    @Override
    public List<ProductDetalleDto> findAll() {
        return this.productRepository.findAll().stream().map(p -> {
            ProductDetalleDto productoDetalle = new ProductDetalleDto();
            productoDetalle.setId(p.getProductId());
            productoDetalle.setSku(p.getSku());
            productoDetalle.setNombre(p.getNombre());
            productoDetalle.setMarca(p.getMarca());
            productoDetalle.setModelo(p.getModelo());
            productoDetalle.setPrecio(p.getPrecio());
            productoDetalle.setPrecioVigente(p.getPrecioVigente());
            productoDetalle.setDescripcion(p.getDescripcion());
            productoDetalle.setImagen(p.getImagen());
            productoDetalle.setEspecificaciones(p.getEspecificaciones());
            productoDetalle.setEstado(p.getEstado());
            try {
                productoDetalle.setStock(inventoryClient.getStock(p.getProductId()));
            } catch (FeignException exception) {
                productoDetalle.setStock(null);
            }

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

        checkCategory(product.getCategoryId());
        return this.productRepository.save(product);
    }

    private void checkCategory(Long id) {
        if (id == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Categoria obligatoria");
        try {
            var category = categoryClient.getCategoryById(id);
            if (category == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Categoria inexistente");
            if (!"ACTIVO".equalsIgnoreCase(category.getEstado())) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "La categoria esta desactivada");
        }
        catch (FeignException.NotFound e) { throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Categoria inexistente"); }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Product product = this.findById(id);

        product.setEstado("INACTIVO");
        this.productRepository.save(product);
    }

    @Transactional
    @Override
    public Product updateById(Long id, Product product) {
        return this.productRepository.findById(id).map(element -> {
            element.setNombre(product.getNombre());
            element.setEstado(product.getEstado());
            element.setPrecio(product.getPrecio());
            element.setDescripcion(product.getDescripcion());
            element.setImagen(product.getImagen());
            element.setEspecificaciones(product.getEspecificaciones());

            checkCategory(product.getCategoryId());
            element.setCategoryId(product.getCategoryId());

            return this.productRepository.save(element);
        }).orElseThrow(
                () -> new ProductException("El producto con Id " + id + " no existe.")
        );
    }
}
