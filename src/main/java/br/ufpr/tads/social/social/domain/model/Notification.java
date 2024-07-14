package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminator;
import org.hibernate.annotations.AnyDiscriminatorValue;
import org.hibernate.annotations.AnyKeyJavaClass;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "NOTIFICATION")
public class Notification{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private NotificationType notificationType;
    private String title;
    private String message;
    private LocalDateTime createdAt;

    @Any
    @AnyDiscriminator(DiscriminatorType.STRING)
    @AnyDiscriminatorValue(discriminator = "POST", entity = Post.class)
    @AnyDiscriminatorValue(discriminator = "USER_PROFILE", entity = UserProfile.class)
    @AnyKeyJavaClass(UUID.class)
    @Column(name = "ENTITY_TYPE")
    @JoinColumn(name = "ENTITY_ID")
    private Notifiable notifiable;

}
