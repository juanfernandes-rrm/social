package br.ufpr.tads.social.social.dto.request;

import br.ufpr.tads.social.social.dto.commons.ProductItemRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductsPriceRequestDTO {

    private List<ProductItemRequestDTO> products;
    private String cep;
    private double distance;
}
