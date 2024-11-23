package br.ufpr.tads.social.social.dto.response;

import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductReviewResponseDTO {

    private UUID id;
    private UUID productId;
    private UUID storeId;
    private GetUserProfileDTO user;
    private String review;
    private Integer rating;
    private String createdAt;

}
