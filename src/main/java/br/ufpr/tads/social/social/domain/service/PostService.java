package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.dto.response.PageWrapper;
import br.ufpr.tads.social.social.dto.response.ProductCardResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static java.util.Objects.requireNonNull;

@Service
public class PostService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${catalog.service.url}")
    private String catalogServiceUrl;

    public ResponseEntity<Page<ProductCardResponseDTO>> getCards(Pageable pageable) {
        ResponseEntity<PageWrapper<ProductCardResponseDTO>> responseEntity = restTemplate.exchange(
                constructQueryParams(pageable),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        Page<ProductCardResponseDTO> page = requireNonNull(responseEntity.getBody()).toPage();
        return new ResponseEntity<>(page, responseEntity.getStatusCode());
    }

    private URI constructQueryParams(Pageable pageable) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(catalogServiceUrl)
                .path("/product/lowest-price")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                uriBuilder.queryParam("sortBy", order.getProperty());
                uriBuilder.queryParam("sortDirection", order.getDirection().name());
            });
        }

        return uriBuilder.build().toUri();
    }

}
