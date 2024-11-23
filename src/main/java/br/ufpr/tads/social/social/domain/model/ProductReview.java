package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "PRODUCT_REVIEW")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID storeId;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_PROFILE_ID", nullable = false)
    private CustomerProfile customerProfile;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

