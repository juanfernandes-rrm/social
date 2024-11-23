package br.ufpr.tads.social.social.application.service;

import br.ufpr.tads.social.social.domain.exception.BusinessException;
import br.ufpr.tads.social.social.domain.service.FavoriteProductService;
import br.ufpr.tads.social.social.domain.service.ReviewService;
import br.ufpr.tads.social.social.dto.request.CreateReviewRequestDTO;
import br.ufpr.tads.social.social.dto.request.UpdateReviewRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductPostController {

    @Autowired
    private FavoriteProductService favoriteProductService;

    @Autowired
    private ReviewService reviewService;

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping("/favorite/{productId}")
    public ResponseEntity<?> favoriteProduct(@PathVariable("productId") UUID productId) {
        try {
            log.info("Favorite product {}", productId);
            favoriteProductService.addFavorite(getUser(), productId);
            return ResponseEntity.ok().build();
        }catch (BusinessException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PostMapping("/favorite/remove/{productId}")
    public ResponseEntity<?> removeFavoriteProduct(@PathVariable("productId") UUID productId) {
        try {
            log.info("Favorite product {}", productId);
            favoriteProductService.removeFavorite(getUser(), productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @PostMapping("/reviews/{productId}")
    public ResponseEntity<?> reviewProduct(@PathVariable("productId") UUID productId, @RequestBody CreateReviewRequestDTO reviewRequestDTO) {
        try {
            UUID user = getUser();
            log.info("Review product {} from user with keycloakId {}", productId, user);
            return ResponseEntity.ok(reviewService.createReview(user, reviewRequestDTO));
        } catch (Exception e) {
            log.error("Erro ao criar review", e);
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/reviews/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable("productId") UUID productId,
                                               @RequestParam(value = "storeId", required = false) UUID storeId,
                                               @RequestParam("page") int page, @RequestParam("size") int size,
                                               @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            log.info("Get reviews for product {}", productId);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(reviewService.getReviews(productId, Optional.ofNullable(storeId), pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable("reviewId") UUID reviewId) {
        try {
            reviewService.deleteReview(getUser(), reviewId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable("reviewId") UUID reviewId,
                                          @RequestBody UpdateReviewRequestDTO reviewRequestDTO) {
        try {
            return ResponseEntity.ok(reviewService.updateReview(getUser(), reviewId, reviewRequestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/comments/{productId}")
    public ResponseEntity<?> getProductComments(@PathVariable("productId") UUID id) {
        return null;
    }

    private UUID getUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("User: {}", jwt.getClaimAsString("preferred_username"));
        return UUID.fromString(jwt.getSubject());
    }

}
