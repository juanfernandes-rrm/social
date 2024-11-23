package br.ufpr.tads.social.social.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateReviewRequestDTO {

    private int rating;
    private String comment;
    private UUID productId;
    private UUID storeId;

}
