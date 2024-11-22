package br.ufpr.tads.social.social.domain.port.product;

import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Page<ProductDTO> getProducts(Pageable pageable);

    SliceImpl<ProductDTO> getProductsDetails(List<UUID> productIdList, Pageable pageable);

}
