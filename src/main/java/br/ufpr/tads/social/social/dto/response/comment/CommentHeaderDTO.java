package br.ufpr.tads.social.social.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentHeaderDTO {
    private UUID productId;
    private UUID storeId;
    private UUID reviewId;
}
