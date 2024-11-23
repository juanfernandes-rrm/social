package br.ufpr.tads.social.social.dto.request;

import lombok.Data;

@Data
public class UpdateReviewRequestDTO {

    private int rating;
    private String comment;

}
