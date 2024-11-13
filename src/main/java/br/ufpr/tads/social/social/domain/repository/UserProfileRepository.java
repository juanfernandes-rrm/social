package br.ufpr.tads.social.social.domain.repository;

import br.ufpr.tads.social.social.domain.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByKeycloakId(UUID keycloakId);

    @Query("SELECT u FROM UserProfile u " +
            "JOIN u.usersFollowing f " +
            "WHERE f.keycloakId = :keycloakId")
    Page<UserProfile> findFollowersByKeycloakId(@Param("keycloakId") UUID keycloakId, Pageable pageable);

    @Query("SELECT f FROM UserProfile u JOIN u.usersFollowing f WHERE u.keycloakId = :userId")
    Page<UserProfile> findFollowingByKeycloakId(@Param("userId") UUID userId, Pageable pageable);

}
