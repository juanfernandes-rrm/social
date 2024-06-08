package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

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

    @ElementCollection
    @CollectionTable(name = "PRODUCTS_LIST", joinColumns = @JoinColumn(name = "SHOPPING_LIST_ID"))
    @Column(name = "PRODUCT_ID")
    private List<UUID> productList;

}
