package br.ufpr.tads.social.social.domain.port.product;

import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Page<ProductDTO> getProducts(Pageable pageable);

    PagedModel<ProductDTO> getProductsDetails(List<UUID> productIdList, Pageable pageable);

}