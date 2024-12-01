package br.ufpr.tads.social.social.dto.response.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRanking {

    private UUID userId;
    private String name;
    private String urlImage;
    private Long totalReceipts;
    private Long totalProducts;

}
