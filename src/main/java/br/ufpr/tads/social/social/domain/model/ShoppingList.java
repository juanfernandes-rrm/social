package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "SHOPPING_LIST")
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_PROFILE_ID", nullable = false)
    private CustomerProfile customerProfile;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "PRODUCTS_LIST", joinColumns = @JoinColumn(name = "SHOPPING_LIST_ID"))
    private List<ProductItem> productList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class ProductItem {
        @Column(name = "PRODUCT_ID", nullable = false)
        private UUID productId;

        @Column(name = "QUANTITY", nullable = false)
        private Integer quantity;
    }

}

