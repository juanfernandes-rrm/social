package br.ufpr.tads.social.social.application.service;

import br.ufpr.tads.social.social.domain.service.FavoriteProductService;
import br.ufpr.tads.social.social.domain.service.ProfileService;
import br.ufpr.tads.social.social.domain.service.ReviewService;
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

    @Autowired
    private ReviewService reviewService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public GetUserProfileDTO createProfile(@RequestBody UUID userKeycloakId) {
        return profileService.createProfile(userKeycloakId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<GetUserProfileDTO> getProfile() {
        try {
            return ResponseEntity.ok(profileService.getProfile(getUser()));
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{keycloakId}")
    public ResponseEntity<GetUserProfileDTO> getProfile(@PathVariable UUID keycloakId) {
        try {
            return ResponseEntity.ok(profileService.getProfile(keycloakId));
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{keycloakId}/favorites")
    public ResponseEntity<Slice<ProductDTO>> getFavorites(@PathVariable UUID keycloakId, @RequestParam("page") int page, @RequestParam("size") int size,
                                                          @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(favoriteProductService.getFavorites(keycloakId, pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{keycloakId}/receipts")
    public ResponseEntity<Slice<ReceiptSummaryResponseDTO>> getReceipts(@PathVariable UUID keycloakId, @RequestParam("page") int page, @RequestParam("size") int size,
                                                                        @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(profileService.getReceipts(keycloakId, pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/follow/{keycloakId}")
    public ResponseEntity<?> followUser(@PathVariable UUID keycloakId) {
        try {
            profileService.followUser(getUser(), keycloakId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/following")
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
    public ResponseEntity<?> followingUsersByUser(@PathVariable UUID keycloakId,
                                                  @RequestParam("page") int page, @RequestParam("size") int size,
                                                  @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(profileService.getFollowingUsers(keycloakId, pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/followers")
    public ResponseEntity<?> followerUsers(@RequestParam("page") int page, @RequestParam("size") int size,
                                           @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(profileService.getFollowerUsers(getUser(), pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{keycloakId}/followers")
    public ResponseEntity<?> followerUsersByUser(@PathVariable("keycloakId") UUID keycloakId,
                                                 @RequestParam("page") int page, @RequestParam("size") int size,
                                                 @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(profileService.getFollowerUsers(keycloakId, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{keycloakId}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable UUID keycloakId,
                                        @RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(reviewService.getReviews(keycloakId, pageable));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    private UUID getUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("User: {}", jwt.getClaimAsString("preferred_username"));
        return UUID.fromString(jwt.getSubject());
    }

}
