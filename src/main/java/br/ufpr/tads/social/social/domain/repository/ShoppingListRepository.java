package br.ufpr.tads.social.social.domain.repository;

import br.ufpr.tads.social.social.domain.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, UUID> {

    List<ShoppingList> findByCustomerProfileKeycloakId(UUID userId);
}
