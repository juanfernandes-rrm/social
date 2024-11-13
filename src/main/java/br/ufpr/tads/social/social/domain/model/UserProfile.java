package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "USER_PROFILE")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserProfile implements Notifiable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "KEYCLOAK_ID", nullable = false)
    private UUID keycloakId;

    @ManyToMany
    @JoinTable(
            name = "USER_FOLLOW_RELATION",
            joinColumns = @JoinColumn(name = "USER_PROFILE_ID"),
            inverseJoinColumns = @JoinColumn(name = "FOLLOWING_USER_ID")
    )
    private List<UserProfile> usersFollowing;

    @ManyToMany(mappedBy = "usersFollowing")
    private List<UserProfile> usersFollowers;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}