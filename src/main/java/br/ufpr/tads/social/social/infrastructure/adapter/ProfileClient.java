package br.ufpr.tads.social.social.infrastructure.adapter;

import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProfileClient {

    private static String GET_PROFILE = "/account/user/%s";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${register.service.url}")
    private String registerServiceUrl;

    public GetUserProfileDTO getProfile(String userKeycloakId) {
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
}
