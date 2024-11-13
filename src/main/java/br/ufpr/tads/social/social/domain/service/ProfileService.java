package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.domain.model.CustomerProfile;
import br.ufpr.tads.social.social.domain.repository.CustomerProfileRepository;
import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import br.ufpr.tads.social.social.dto.response.profile.ReceiptSummaryResponseDTO;
import br.ufpr.tads.social.social.infrastructure.adapter.ProfileClient;
import br.ufpr.tads.social.social.infrastructure.adapter.ReceiptClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private ProfileClient profileClient;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private ReceiptClient receiptClient;

    public GetUserProfileDTO getProfile(String userKeycloakId) {
        return profileClient.getProfile(userKeycloakId);
    }

    public Slice<ReceiptSummaryResponseDTO> getReceipts(UUID keycloakId, String token, Pageable pageable) {
        return receiptClient.getScannedReceipts(keycloakId, token, pageable);
    }

    public GetUserProfileDTO createProfile(UUID userKeycloakId) {
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setKeycloakId(userKeycloakId);
        return mapToDTO(customerProfileRepository.save(customerProfile));
    }

    private GetUserProfileDTO mapToDTO(CustomerProfile customerProfile) {
        GetUserProfileDTO getUserProfileDTO = new GetUserProfileDTO();
        getUserProfileDTO.setId(customerProfile.getId());
        getUserProfileDTO.setKeycloakId(customerProfile.getKeycloakId());
        return getUserProfileDTO;
    }

}
