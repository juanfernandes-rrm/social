package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.domain.model.UserProfile;
import br.ufpr.tads.social.social.domain.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile getProfileByKeycloakId(UUID userKeycloakId) {
        return userProfileRepository.findByKeycloakId(userKeycloakId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}
