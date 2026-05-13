package com.gamehub.category.services;

import com.gamehub.category.models.Category;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    List<Category> findAll();
    Category findById(Long id);
    Category save(Category category);
    void deleteById(Long id);
    Category updateById(Long id, Category category);
}
