package com.gamehub.auth.assemblers;

import com.gamehub.auth.controllers.AuthControllerV2;
import com.gamehub.auth.models.Auth;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthModelAssemblers implements RepresentationModelAssembler<Auth, EntityModel<Auth>>{

    //CORREGIR, solo agregué estructura por ejemplo del profe.
    @Override
    public EntityModel<Auth> toModel(Auth auth) {
        return EntityModel.of(
                auth,
                linkTo(methodOn(AuthControllerV2.class).findById(auth.getAuthId())).withSelfRel(),
                linkTo(methodOn(AuthControllerV2.class).findAll()).withRel("auth"));
    }
}
