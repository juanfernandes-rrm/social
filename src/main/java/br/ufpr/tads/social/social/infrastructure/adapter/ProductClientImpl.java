package br.ufpr.tads.social.social.infrastructure.adapter;

import br.ufpr.tads.social.social.domain.port.product.ProductClient;
import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import br.ufpr.tads.social.social.dto.response.PageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Component
public class ProductClientImpl implements ProductClient {

    public static final String GET_PRODUCT_BY_ID = "/product/post?id=";
    public static final String GET_PRODUCTS_WITH_LOWEST_PRICE = "/product/lowest-price";
    public static final String GET_PRODUCTS_DETAILS = "/product/products-details";
    private final RestTemplate restTemplate;
    private final String catalogServiceUrl;

    @Autowired
    private PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler;

    public ProductClientImpl(RestTemplate restTemplate, @Value("${catalog.service.url}") String catalogServiceUrl) {
        this.restTemplate = restTemplate;
        this.catalogServiceUrl = catalogServiceUrl;
    }

    @Override
    public Page<ProductDTO> fetchProducts(Pageable pageable) {
        ResponseEntity<PageWrapper<ProductDTO>> responseEntity = restTemplate.exchange(
                constructUrlWithPaginationParams(pageable, GET_PRODUCTS_WITH_LOWEST_PRICE),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        return requireNonNull(responseEntity.getBody()).toPage();
    }

    @Override
    public PagedModel<ProductDTO> fetchProductsDetails(List<UUID> productIdList, Pageable pageable) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<UUID>> requestEntity = new HttpEntity<>(productIdList, headers);

        ResponseEntity<PageWrapper<ProductDTO>> responseEntity = restTemplate.exchange(
                constructUrlWithPaginationParams(pageable, GET_PRODUCTS_DETAILS),
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        Page<ProductDTO> productDTOPage = requireNonNull(responseEntity.getBody()).toPage();
        PagedModel<EntityModel<ProductDTO>> entityModelPagedModel = pagedResourcesAssembler.toModel(productDTOPage);

        List<ProductDTO> productDTOs = entityModelPagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .collect(Collectors.toList());

        return PagedModel.of(productDTOs, entityModelPagedModel.getMetadata());
    }

    private URI constructUrlWithPaginationParams(Pageable pageable, String path) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(catalogServiceUrl)
                .path(path)
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
