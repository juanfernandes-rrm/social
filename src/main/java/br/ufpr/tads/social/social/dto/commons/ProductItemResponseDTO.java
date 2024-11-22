package br.ufpr.tads.social.social.dto.commons;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductItemResponseDTO {

    private UUID productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal total;

}
