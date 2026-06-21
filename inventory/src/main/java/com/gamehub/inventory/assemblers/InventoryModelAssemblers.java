package com.gamehub.inventory.assemblers;

import com.gamehub.inventory.controllers.InventarioController;
import com.gamehub.inventory.models.Inventario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class InventoryModelAssemblers implements RepresentationModelAssembler <Inventario, EntityModel<Inventario>> {
    @Override
    public EntityModel<Inventario> toModel(Inventario inventario) {
        return EntityModel.of(
                inventario,
                linkTo(methodOn(InventarioController.class).findById(inventario.getInventoryId())).withSelfRel(),
                linkTo(methodOn(InventarioController.class).getAll()).withRel("inventory"));
    }

}
