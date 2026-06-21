package com.gamehub.review.assemblers;

import com.gamehub.review.controllers.ReviewController;
import com.gamehub.review.models.Review;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ReviewModelAssemblers implements RepresentationModelAssembler <Review, EntityModel<Review>> {
    @Override
    public EntityModel<Review> toModel(Review review) {
        return EntityModel.of(
                review,
                linkTo(methodOn(ReviewController.class).findById(review.getResenaId())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).findAll()).withRel("reviews"));
    }
}
