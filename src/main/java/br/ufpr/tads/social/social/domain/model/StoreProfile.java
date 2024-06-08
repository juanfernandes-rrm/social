package br.ufpr.tads.social.social.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "STORE_PROFILE")
public class StoreProfile extends UserProfile{

}
