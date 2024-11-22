package br.ufpr.tads.social.social.dto.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductItemRequestDTO {

    private UUID productId;
    private int quantity;

}
