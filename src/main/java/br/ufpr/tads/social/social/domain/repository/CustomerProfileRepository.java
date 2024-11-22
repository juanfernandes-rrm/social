package br.ufpr.tads.social.social.domain.repository;

import br.ufpr.tads.social.social.domain.model.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {
    Optional<CustomerProfile> findByKeycloakId(UUID keycloakId);
}
