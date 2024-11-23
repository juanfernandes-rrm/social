package br.ufpr.tads.social.social.dto.response;

import br.ufpr.tads.social.social.dto.commons.BranchDTO;
import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductReviewResponseDTO {

    private UUID id;
    private ProductDTO product;
    private BranchDTO store;
    private GetUserProfileDTO user;
    private String review;
    private Integer rating;
    private Boolean approved;
    private String createdAt;

}
