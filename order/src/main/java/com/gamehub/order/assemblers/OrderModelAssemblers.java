package com.gamehub.order.assemblers;


import com.gamehub.order.controllers.OrderController;
import com.gamehub.order.models.OrderDetalle;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssemblers implements RepresentationModelAssembler<OrderDetalle, EntityModel<OrderDetalle>> {

    @Override
    public EntityModel<OrderDetalle>toModel(OrderDetalle orderDetalle){
        return EntityModel.of(
                orderDetalle,
                linkTo(methodOn(OrderController.class).getHistory(orderDetalle.getUserEmail())).withRel("history"));

    }


}

