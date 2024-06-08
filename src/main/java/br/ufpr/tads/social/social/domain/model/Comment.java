package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "COMMENT")
public class Comment extends Feedback{

    @ManyToOne
    @JoinColumn(name = "USER_PROFILE_ID", nullable = false)
    private UserProfile userProfile;

}
