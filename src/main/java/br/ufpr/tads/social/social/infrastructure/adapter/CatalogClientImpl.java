package br.ufpr.tads.social.social.infrastructure.adapter;

import br.ufpr.tads.social.social.domain.port.product.ProductClient;
import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import br.ufpr.tads.social.social.dto.commons.ProductItemRequestDTO;
import br.ufpr.tads.social.social.dto.request.ProductsPriceRequestDTO;
import br.ufpr.tads.social.social.dto.response.PageWrapper;
import br.ufpr.tads.social.social.dto.response.ProductsPriceResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
public class CatalogClientImpl implements ProductClient {

    public static final String GET_PRODUCT_BY_ID = "/product/post?id=";
    public static final String GET_PRODUCTS_WITH_LOWEST_PRICE = "/product/lowest-price";
    public static final String GET_PRODUCTS_DETAILS = "/product/products-details";
    public static final String POST_LIST_PRODUCTS_PRICE = "/product/prices-by-store";
    private final RestTemplate restTemplate;
    private final String catalogServiceUrl;

    @Autowired
    private PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler;

    @Autowired
    private PagedResourcesAssembler<ProductsPriceResponseDTO> productsPriceResponseDTOPagedResourcesAssembler;

    public CatalogClientImpl(RestTemplate restTemplate, @Value("${catalog.service.url}") String catalogServiceUrl) {
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
    public SliceImpl<ProductDTO> fetchProductsDetails(List<UUID> productIdList, Pageable pageable) {
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

        PagedModel<ProductDTO> pagedModel = PagedModel.of(productDTOs, entityModelPagedModel.getMetadata());
        List<ProductDTO> products = pagedModel.getContent().stream().toList();
        boolean hasNext = pagedModel.getContent().size() == pageable.getPageSize();

        return new SliceImpl<>(products, pageable, hasNext);
    }

    public SliceImpl<ProductsPriceResponseDTO> fetchProductsPriceByStore(ProductsPriceRequestDTO productsPriceRequestDTO, Pageable pageable) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductsPriceRequestDTO> requestEntity = new HttpEntity<>(productsPriceRequestDTO, headers);

        ResponseEntity<PageWrapper<ProductsPriceResponseDTO>> responseEntity = restTemplate.exchange(
                constructUrlWithPaginationParams(pageable, POST_LIST_PRODUCTS_PRICE),
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        Page<ProductsPriceResponseDTO> productDTOPage = requireNonNull(responseEntity.getBody()).toPage();
        PagedModel<EntityModel<ProductsPriceResponseDTO>> entityModelPagedModel = productsPriceResponseDTOPagedResourcesAssembler.toModel(productDTOPage);

        List<ProductsPriceResponseDTO> productDTOs = entityModelPagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .collect(Collectors.toList());

        PagedModel<ProductsPriceResponseDTO> pagedModel = PagedModel.of(productDTOs, entityModelPagedModel.getMetadata());
        List<ProductsPriceResponseDTO> products = pagedModel.getContent().stream().toList();
        boolean hasNext = pagedModel.getContent().size() == pageable.getPageSize();

        return new SliceImpl<>(products, pageable, hasNext);
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
        }else {
            uriBuilder.queryParam("sortBy", "id");
            uriBuilder.queryParam("sortDirection", "ASC");
        }

        return uriBuilder.build().toUri();
    }

}
