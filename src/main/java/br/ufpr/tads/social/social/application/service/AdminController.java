package br.ufpr.tads.social.social.application.service;

import br.ufpr.tads.social.social.domain.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ReviewService reviewService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/total-reviews")
    public ResponseEntity<?> getTotalReviews() {
        try {
            return ResponseEntity.ok(reviewService.getTotalReviews());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

}
