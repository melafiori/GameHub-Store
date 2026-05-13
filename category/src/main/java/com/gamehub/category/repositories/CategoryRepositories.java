package com.gamehub.category.repositories;

import com.gamehub.category.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepositories extends JpaRepository<Category, Long> {
}
