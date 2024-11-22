package br.ufpr.tads.social.social.infrastructure.adapter;

import br.ufpr.tads.social.social.domain.port.product.ProductClient;
import br.ufpr.tads.social.social.domain.port.product.ProductService;
import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductClient productClient;

    @Autowired
    public ProductServiceImpl(ProductClient productClient) {
        this.productClient = productClient;
    }

    public Page<ProductDTO> getProducts(Pageable pageable) {
        return productClient.fetchProducts(pageable);
    }

    public SliceImpl<ProductDTO> getProductsDetails(List<UUID> productIdList, Pageable pageable) {
        return productClient.fetchProductsDetails(productIdList, pageable);
    }

}
