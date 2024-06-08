package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
@Entity
@Table(name = "FAVORITE_PRODUCTS")
public class FavoriteProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_PROFILE_ID", nullable = false)
    private CustomerProfile customerProfile;

    @ElementCollection
    @CollectionTable(name = "FAVORITE_PRODUCT_IDS", joinColumns = @JoinColumn(name = "FAVORITE_PRODUCTS_ID"))
    @Column(name = "PRODUCT_ID")
    private List<UUID> productIds;
}
