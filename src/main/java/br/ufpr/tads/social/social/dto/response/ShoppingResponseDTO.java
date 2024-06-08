package br.ufpr.tads.social.social.dto.response;


import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import br.ufpr.tads.social.social.dto.commons.StoreDTO;

import java.math.BigDecimal;
import java.util.List;

public class ShoppingResponseDTO {

    private StoreDTO store;
    private List<ProductDTO> productList;
    private BigDecimal totalPrice;

}
