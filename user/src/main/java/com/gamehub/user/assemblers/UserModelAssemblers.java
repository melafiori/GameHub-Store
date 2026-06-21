package com.gamehub.user.assemblers;

import com.gamehub.user.controllers.UserController;
import com.gamehub.user.models.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssemblers implements RepresentationModelAssembler<User, EntityModel<User>> {
    @Override
    public EntityModel<User> toModel(User user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).getById(user.getUserId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"));
    }
}
