package br.ufpr.tads.social.social.domain.dto.product.commons;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
public class PriceHistoryResponseDTO {

    private UUID storeId;
    private UUID branchId;
    private String storeName;
    private String neighborhood;
    private BigDecimal price;
    private LocalDateTime priceChangeDate;

}
