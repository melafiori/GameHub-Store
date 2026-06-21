package com.gamehub.warranty.assemblers;

import com.gamehub.warranty.controllers.WarrantyController;
import com.gamehub.warranty.models.Warranty;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class WarrantyModelAssemblers implements RepresentationModelAssembler <Warranty, EntityModel<Warranty>> {
    @Override
    public EntityModel<Warranty> toModel(Warranty warranty) {
        return EntityModel.of(
                warranty,
                linkTo(methodOn(WarrantyController.class).findById(warranty.getWarrantyId())).withSelfRel(),
                linkTo(methodOn(WarrantyController.class).findAll()).withRel("warranties"));
    }
}
