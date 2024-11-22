package br.ufpr.tads.social.social.dto.response;

import br.ufpr.tads.social.social.dto.commons.BranchDTO;
import br.ufpr.tads.social.social.dto.commons.ProductItemResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class ProductsPriceResponseDTO {

    private BigDecimal totalPrice;
    private int productQuantity;
    private BranchDTO branch;
    private List<ProductItemResponseDTO> products;

}
