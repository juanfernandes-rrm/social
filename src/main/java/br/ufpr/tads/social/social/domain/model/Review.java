package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "REVIEW")
public class Review extends Feedback{

    @Column(nullable = false)
    private Double rating;

    @ManyToOne
    @JoinColumn(name = "COSTUMER_PROFILE_ID", nullable = false)
    private CustomerProfile customerProfile;

}
