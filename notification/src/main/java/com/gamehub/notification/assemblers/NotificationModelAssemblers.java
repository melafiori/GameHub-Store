package com.gamehub.notification.assemblers;

import com.gamehub.notification.controllers.NotificationController;
import com.gamehub.notification.models.Notification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificationModelAssemblers implements RepresentationModelAssembler<Notification, EntityModel<Notification>>{
    @Override
    public EntityModel<Notification>toModel(Notification notification){
        return EntityModel.of(
                notification,
                linkTo(methodOn(NotificationController.class).findById(notification.getNotificationID())).withSelfRel(),
                linkTo(methodOn(NotificationController.class).findAll()).withRel("notification"));

    }

}
