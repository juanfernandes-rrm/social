package br.ufpr.tads.social.social.application.service;

import br.ufpr.tads.social.social.domain.service.ProfileService;
import br.ufpr.tads.social.social.domain.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProfileService profileService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/total-reviews")
    public ResponseEntity<?> getTotalReviews() {
        try {
            return ResponseEntity.ok(reviewService.getTotalReviews());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/profile/delete/{keycloakId}")
    public ResponseEntity<?> deleteCustomerProfile(@PathVariable("keycloakId") UUID keycloakId) {
        try {
            log.info("Deleting profile of user with keycloakId {}", keycloakId);
            profileService.deleteProfile(keycloakId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("review/pending-approval")
    public ResponseEntity<?> getPendingApprovalReviews(@RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(reviewService.getReviewsWithDetails(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/review/{reviewId}/approve")
    public ResponseEntity<?> approveReview(@PathVariable("reviewId") UUID reviewId) {
        try {
            log.info("Approving review with id {}", reviewId);
            reviewService.approveReview(reviewId, true);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/review/{reviewId}/reject")
    public ResponseEntity<?> rejectReview(@PathVariable("reviewId") UUID reviewId) {
        try {
            log.info("Rejecting review with id {}", reviewId);
            reviewService.approveReview(reviewId, false);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

}
