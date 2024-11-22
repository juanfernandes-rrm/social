package br.ufpr.tads.social.social.dto.request;

import br.ufpr.tads.social.social.dto.commons.ProductItemRequestDTO;
import lombok.Data;

import java.util.List;

@Data
public class ShoppingListRequestDTO {

    private List<ProductItemRequestDTO> products;

}


