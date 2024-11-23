package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.domain.model.CustomerProfile;
import br.ufpr.tads.social.social.domain.model.UserProfile;
import br.ufpr.tads.social.social.domain.repository.CustomerProfileRepository;
import br.ufpr.tads.social.social.domain.repository.UserProfileRepository;
import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import br.ufpr.tads.social.social.dto.response.profile.ReceiptSummaryResponseDTO;
import br.ufpr.tads.social.social.infrastructure.adapter.ProfileClient;
import br.ufpr.tads.social.social.infrastructure.adapter.ReceiptClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ProfileService {

    @Autowired
    private ProfileClient profileClient;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ReceiptClient receiptClient;

    public GetUserProfileDTO getProfile(UUID userKeycloakId) {
        return profileClient.getProfile(userKeycloakId);
    }

    public Slice<ReceiptSummaryResponseDTO> getReceipts(UUID keycloakId, String token, Pageable pageable) {
        return receiptClient.getScannedReceipts(keycloakId, token, pageable);
    }

    @Transactional
    public void followUser(UUID user, UUID keycloakId) {
        Optional<UserProfile> userToFollowOptional = userProfileRepository.findByKeycloakId(keycloakId);
        Optional<UserProfile> userFollowerOptional = userProfileRepository.findByKeycloakId(user);

        if (userToFollowOptional.isEmpty()) {
            throw new RuntimeException("User to follow not found with Keycloak ID: " + keycloakId);
        }

        if (userFollowerOptional.isEmpty()) {
            throw new RuntimeException("User follower not found with Keycloak ID: " + user);
        }

        UserProfile userToFollow = userToFollowOptional.get();
        UserProfile userFollower = userFollowerOptional.get();

        if (!userFollower.getUsersFollowing().contains(userToFollow)) {
            userFollower.getUsersFollowing().add(userToFollow);
            userToFollow.getUsersFollowers().add(userFollower);

            userProfileRepository.save(userFollower);
            userProfileRepository.save(userToFollow);
        } else {
            log.info("User {} already follow user {}.", user, keycloakId);
        }
    }

    public GetUserProfileDTO createProfile(UUID userKeycloakId) {
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setKeycloakId(userKeycloakId);
        return mapToDTO(customerProfileRepository.save(customerProfile));
    }

    public SliceImpl<GetUserProfileDTO> getFollowerUsers(UUID user, Pageable pageable) {
        Pageable pageableForLocalQuery = Pageable.ofSize(pageable.getPageSize()).withPage(pageable.getPageNumber());
        Page<UserProfile> followersPage = userProfileRepository.findFollowersByKeycloakId(user, pageableForLocalQuery);

        if (followersPage.hasContent()) {
            List<UUID> followerIds = followersPage.stream()
                    .map(UserProfile::getKeycloakId)
                    .toList();

            return profileClient.getProfiles(followerIds, pageable);
        }

        return new SliceImpl<>(Collections.emptyList(), pageable, false);
    }

    public SliceImpl<GetUserProfileDTO> getFollowingUsers(UUID userId, Pageable pageable) {
        Pageable pageableForLocalQuery = Pageable.ofSize(pageable.getPageSize()).withPage(pageable.getPageNumber());
        Page<UserProfile> followingIdsPage = userProfileRepository.findFollowingByKeycloakId(userId, pageableForLocalQuery);

        if (followingIdsPage.hasContent()) {
            List<UUID> followingIds = followingIdsPage.getContent().stream().map(UserProfile::getKeycloakId).toList();

            Slice<GetUserProfileDTO> profilesSlice = profileClient.getProfiles(followingIds, pageable);

            return new SliceImpl<>(profilesSlice.getContent(), pageable, followingIdsPage.hasNext());
        }

        return new SliceImpl<>(Collections.emptyList(), pageable, false);
    }

    @Transactional
    public void deleteProfile(UUID keycloakId) {
        CustomerProfile profile = customerProfileRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
        customerProfileRepository.delete(profile);
    }

    private GetUserProfileDTO mapToDTO(CustomerProfile customerProfile) {
        GetUserProfileDTO getUserProfileDTO = new GetUserProfileDTO();
        getUserProfileDTO.setId(customerProfile.getId());
        getUserProfileDTO.setKeycloakId(customerProfile.getKeycloakId());
        return getUserProfileDTO;
    }

}
