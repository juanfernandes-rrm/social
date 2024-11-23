package br.ufpr.tads.social.social.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequestDTO {

    private UUID productId;
    private UUID storeId;
    private UUID reviewId;
    private UUID parentCommentId;
    private String text;

}
