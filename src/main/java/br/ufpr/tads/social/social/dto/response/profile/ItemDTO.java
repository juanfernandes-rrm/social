package br.ufpr.tads.social.social.dto.response.profile;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemDTO {
    private String name;
    private String code;
    private BigDecimal amount;
    private String unit;
    private BigDecimal unitValue;
    private BigDecimal totalValue;
}
