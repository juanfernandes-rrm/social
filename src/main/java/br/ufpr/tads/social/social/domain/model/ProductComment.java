package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "PRODUCT_COMMENT")
public class ProductComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID storeId;

    @ManyToOne
    @JoinColumn(name = "REVIEW_ID")
    private ProductReview review;

    @ManyToOne
    @JoinColumn(name = "USER_PROFILE_ID", nullable = false)
    private UserProfile userProfile;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "PARENT_COMMENT_ID")
    private ProductComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductComment> replies;
}

