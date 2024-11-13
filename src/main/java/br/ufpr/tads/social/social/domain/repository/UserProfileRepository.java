package br.ufpr.tads.social.social.domain.repository;

import br.ufpr.tads.social.social.domain.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByKeycloakId(UUID keycloakId);

    Page<UserProfile> findFollowersByKeycloakId(UUID user, Pageable pageable);
}
