package br.ufpr.tads.social.social.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCardResponseDTO(UUID id, String name, String code, BigDecimal price, UUID storeCorrelationId) {
}
