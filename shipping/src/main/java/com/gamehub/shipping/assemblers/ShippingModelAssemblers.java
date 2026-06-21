package com.gamehub.shipping.assemblers;

import com.gamehub.shipping.controllers.ShippingController;
import com.gamehub.shipping.models.Shipping;
import org.hibernate.sql.results.graph.entity.internal.EntityAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ShippingModelAssemblers implements RepresentationModelAssembler <Shipping, EntityModel<Shipping>> {
    @Override
    public EntityModel<Shipping> toModel(Shipping shipping) {
        return EntityModel.of(
                shipping,
                linkTo(methodOn(ShippingController.class).findById(shipping.getShippingId())).withSelfRel(),
                linkTo(methodOn(ShippingController.class).findAll()).withRel("shippings"));
    }
}
