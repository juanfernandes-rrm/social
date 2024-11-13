package br.ufpr.tads.social.social.infrastructure.adapter;

import br.ufpr.tads.social.social.dto.commons.CustomSliceDTO;
import br.ufpr.tads.social.social.dto.response.profile.ReceiptSummaryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class ReceiptClient {
    private static String GET_SCANNED_RECEIPTS = "/scan/receipts/%s";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${receipt-scan.service.url}")
    private String receiptScanServiceUrl;

    public Slice<ReceiptSummaryResponseDTO> getScannedReceipts(UUID userKeycloakId, String token, Pageable pageable) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CustomSliceDTO<ReceiptSummaryResponseDTO>> responseEntity = restTemplate.exchange(
                constructUrlWithPaginationParams(pageable, String.format(GET_SCANNED_RECEIPTS, userKeycloakId)),
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            CustomSliceDTO<ReceiptSummaryResponseDTO> customSlice = responseEntity.getBody();
            List<ReceiptSummaryResponseDTO> content = customSlice.getContent();
            return new SliceImpl<>(content, pageable, customSlice.isHasNext());
        }

        return null;
    }

    private URI constructUrlWithPaginationParams(Pageable pageable, String path) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(receiptScanServiceUrl)
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
