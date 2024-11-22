package br.ufpr.tads.social.social.infrastructure.adapter;

import br.ufpr.tads.social.social.dto.response.PageWrapper;
import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class ProfileClient {

    private static String GET_PROFILE = "/account/user/%s";
    private static String GET_PROFILES = "/account/user/details";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${register.service.url}")
    private String registerServiceUrl;

    public GetUserProfileDTO getProfile(UUID userKeycloakId) {
        ResponseEntity<GetUserProfileDTO> responseEntity = restTemplate.exchange(
                registerServiceUrl + String.format(GET_PROFILE, userKeycloakId),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }

        return null;
    }

    public SliceImpl<GetUserProfileDTO> getProfiles(List<UUID> idUsersFollowing, Pageable pageable) {
        HttpEntity<List<UUID>> bodyRequest = new HttpEntity<>(idUsersFollowing);

        ResponseEntity<PageWrapper<GetUserProfileDTO>> responseEntity = restTemplate.exchange(
                constructUrlWithPaginationParams(pageable, GET_PROFILES),
                HttpMethod.POST,
                bodyRequest,
                new ParameterizedTypeReference<>() {}
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Page<GetUserProfileDTO> page = responseEntity.getBody().toPage();
            return new SliceImpl<>(page.getContent(), pageable, page.hasNext());
        }

        return null;
    }

    private URI constructUrlWithPaginationParams(Pageable pageable, String path) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(registerServiceUrl)
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
