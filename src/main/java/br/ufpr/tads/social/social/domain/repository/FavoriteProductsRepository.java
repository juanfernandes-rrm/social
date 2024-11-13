package br.ufpr.tads.social.social.domain.repository;

import br.ufpr.tads.social.social.domain.model.FavoriteProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteProductsRepository extends JpaRepository<FavoriteProducts, UUID> {

    Optional<FavoriteProducts> findByCustomerProfileKeycloakId(UUID keycloakId);
}