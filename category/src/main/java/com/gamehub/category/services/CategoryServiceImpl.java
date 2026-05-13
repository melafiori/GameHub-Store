package com.gamehub.category.services;

import com.gamehub.category.exceptions.CategoryException;
import com.gamehub.category.models.Category;
import com.gamehub.category.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.catalog.CatalogException;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Category> findAll() {
        return this.categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Category findById(Long id) {
        return this.categoryRepository.findById(id).orElseThrow(
                () -> new CategoryException("La categoría con ID " + id + " no existe.")
        );
    }

    @Transactional
    @Override
    public Category save(Category category) {
        if (this.categoryRepository.findById(category.getCategoryId()).isPresent()) {
            throw new CategoryException("Categoria con id " + category.getCategoryId() + " ya existe.");
        }

        return this.categoryRepository.save(category);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {

    }

    @Transactional
    @Override
    public Category updateById(Long id, Category category) {
        return this.categoryRepository.findById(id).map(element -> {
            element.setNombre(category.getNombre());
            element.setEstado(category.getEstado());
            element.setDescripcion(category.getDescripcion());
            return this.categoryRepository.save(element);
        }).orElseThrow(
                ()-> new CategoryException("La categoría con Id " + id + " no existe.")
        );
    }
}
