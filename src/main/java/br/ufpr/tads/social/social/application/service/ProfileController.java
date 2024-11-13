package br.ufpr.tads.social.social.application.service;

import br.ufpr.tads.social.social.domain.service.FavoriteProductService;
import br.ufpr.tads.social.social.domain.service.ProfileService;
import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import br.ufpr.tads.social.social.dto.response.profile.ReceiptSummaryResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/profile")
//TODO: Adicionar tratamento de erro para user não encontrado
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FavoriteProductService favoriteProductService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public GetUserProfileDTO createProfile(@RequestBody UUID userKeycloakId) {
        return profileService.createProfile(userKeycloakId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public GetUserProfileDTO getProfile() {
        return profileService.getProfile(getUser());
    }

    @GetMapping("/{keycloakId}")
    public GetUserProfileDTO getProfile(@PathVariable UUID keycloakId) {
        return profileService.getProfile(keycloakId);
    }

    @GetMapping("{keycloakId}/favorites")
    public ResponseEntity<Slice<ProductDTO>> getFavorites(@PathVariable UUID keycloakId, @RequestParam("page") int page, @RequestParam("size") int size,
                                                          @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(favoriteProductService.getFavorites(keycloakId, pageable));
    }

    @GetMapping("{keycloakId}/receipts")
    public ResponseEntity<Slice<ReceiptSummaryResponseDTO>> getReceipts(@PathVariable UUID keycloakId, @RequestParam("page") int page, @RequestParam("size") int size,
                                                                        @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(profileService.getReceipts(keycloakId, getToken(), pageable));
    }

    @PostMapping("/follow/{keycloakId}")
    public ResponseEntity<?> followUser(@PathVariable UUID keycloakId) {
        try {
            profileService.followUser(getUser(), keycloakId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("following")
    public ResponseEntity<?> followingUsers(@RequestParam("page") int page, @RequestParam("size") int size,
                                            @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(profileService.getFollowingUsers(getUser(), pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{keycloakId}/following")
    public ResponseEntity<?> followingUsersByUser(@PathVariable UUID keycloak,
                                                  @RequestParam("page") int page, @RequestParam("size") int size,
                                                  @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(profileService.getFollowingUsers(keycloak, pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("followers")
    public ResponseEntity<?> followerUsers(@RequestParam("page") int page, @RequestParam("size") int size,
                                           @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(profileService.getFollowerUsers(getUser(), pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{keycloak}/followers")
    public ResponseEntity<?> followerUsers(@PathVariable UUID keycloakId,
                                           @RequestParam("page") int page, @RequestParam("size") int size,
                                           @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(profileService.getFollowerUsers(keycloakId, pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    private UUID getUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("User: {}", jwt.getClaimAsString("preferred_username"));
        return UUID.fromString(jwt.getSubject());
    }

    private String getToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getTokenValue();
    }

}
