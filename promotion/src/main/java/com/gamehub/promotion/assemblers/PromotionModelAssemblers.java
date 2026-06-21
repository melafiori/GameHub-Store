package com.gamehub.promotion.assemblers;

import com.gamehub.promotion.controllers.PromotionController;
import com.gamehub.promotion.models.Promotion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PromotionModelAssemblers implements RepresentationModelAssembler<Promotion, EntityModel<Promotion>>{

    @Override
    public EntityModel<Promotion> toModel(Promotion promotion) {
        return EntityModel.of(
                promotion,
                linkTo(methodOn(PromotionController.class).findById(promotion.getPromotionId())).withSelfRel(),
                linkTo(methodOn(PromotionController.class).findAll()).withRel("promotions")
        );
    }
}
