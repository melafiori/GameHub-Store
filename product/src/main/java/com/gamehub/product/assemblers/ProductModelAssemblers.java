package com.gamehub.product.assemblers;

import com.gamehub.product.controllers.ProductController;
import com.gamehub.product.models.Product;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ProductModelAssemblers implements RepresentationModelAssembler<Product,EntityModel<Product>> {
    @Override
    public EntityModel<Product> toModel(Product product){
        return EntityModel.of(
                product,
                linkTo(methodOn(ProductController.class).getById(product.getProductId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).getAll()).withRel("products"));

    }
}
