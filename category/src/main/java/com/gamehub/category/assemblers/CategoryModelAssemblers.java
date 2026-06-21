package com.gamehub.category.assemblers;

import com.gamehub.category.controllers.CategoryController;
import com.gamehub.category.models.Category;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssemblers implements RepresentationModelAssembler <Category, EntityModel<Category>> {
    @Override
    public EntityModel<Category> toModel(Category category) {
        return EntityModel.of(
                category,
                linkTo(methodOn(CategoryController.class).getById(category.getCategoryId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAll()).withRel("categories"));
    }
}
