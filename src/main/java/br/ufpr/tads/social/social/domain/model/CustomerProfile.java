package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "CUSTOMER_PROFILE")
public class CustomerProfile extends UserProfile{

    @ElementCollection
    @CollectionTable(name = "USER_REGISTERED_RECEIPTS", joinColumns = @JoinColumn(name = "USER_PROFILE_ID"))
    @Column(name = "RECEIPT_ID")
    private List<UUID> registeredReceipts;

    @Column
    private BigDecimal points;

    @OneToMany(mappedBy = "customerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingList> shoppingLists;

    @OneToMany(mappedBy = "customerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

}
