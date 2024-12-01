package br.ufpr.tads.social.social.infrastructure.adapter;

import br.ufpr.tads.social.social.dto.commons.CustomSliceDTO;
import br.ufpr.tads.social.social.dto.response.profile.ReceiptSummaryResponseDTO;
import br.ufpr.tads.social.social.dto.response.profile.UserStatistics;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Service
public class ReceiptClient {
    private static String GET_SCANNED_RECEIPTS = "/scan/receipts/%s";
    private static String GET_USER_STATISTICS = "/scan/users-statistics";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${receipt-scan.service.url}")
    private String receiptScanServiceUrl;

    public Slice<ReceiptSummaryResponseDTO> getScannedReceipts(UUID userKeycloakId, Pageable pageable) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CustomSliceDTO<ReceiptSummaryResponseDTO>> responseEntity = restTemplate.exchange(
                constructUrlWithPaginationParams(pageable, String.format(GET_SCANNED_RECEIPTS, userKeycloakId)),
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            CustomSliceDTO<ReceiptSummaryResponseDTO> customSlice = responseEntity.getBody();

            List<ReceiptSummaryResponseDTO> content = new ArrayList<>();
            boolean hasNext = false;
            if(nonNull(customSlice)) {
                content = customSlice.getContent();
                hasNext = customSlice.isHasNext();
            }

            return new SliceImpl<>(content, pageable, hasNext);
        }

        return null;
    }

    public Slice<UserStatistics> getUserStatistics(Pageable pageable) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CustomSliceDTO<UserStatistics>> responseEntity = restTemplate.exchange(
                constructUrlWithPaginationParams(pageable, String.format(GET_USER_STATISTICS)),
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            CustomSliceDTO<UserStatistics> customSlice = responseEntity.getBody();

            List<UserStatistics> content = new ArrayList<>();
            boolean hasNext = false;
            if(nonNull(customSlice)) {
                content = customSlice.getContent();
                hasNext = customSlice.isHasNext();
            }

            return new SliceImpl<>(content, pageable, hasNext);
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
