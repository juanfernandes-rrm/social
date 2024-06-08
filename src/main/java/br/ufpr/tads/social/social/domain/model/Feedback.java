package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Feedback parentFeedback;

    @OneToMany(mappedBy = "parentFeedback", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> replies;

    @ManyToOne
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;

}
