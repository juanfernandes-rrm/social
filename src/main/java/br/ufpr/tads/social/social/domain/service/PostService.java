package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.domain.dto.product.commons.ProductDTO;
import br.ufpr.tads.social.social.domain.port.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private ProductService productService;

    public ResponseEntity<Page<ProductDTO>> getCards(Pageable pageable) {
        return null;
    }

}
