package br.ufpr.tads.social.social.application.service;

import br.ufpr.tads.social.social.domain.service.ShoppingListService;
import br.ufpr.tads.social.social.dto.commons.ProductDTO;
import br.ufpr.tads.social.social.dto.request.ShoppingListRequestDTO;
import br.ufpr.tads.social.social.dto.response.ProductsPriceResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/shopping-list")
public class ShoppingListController {

    @Autowired
    private ShoppingListService shoppingListService;

    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @GetMapping
    public ResponseEntity<SliceImpl<ProductDTO>> getOrCreateList() {
        try {
            log.info("Getting shopping list");
            return ResponseEntity.ok(shoppingListService.getOrCreateShoppingList(getUser()));
        } catch (Exception e) {
            log.error("Error getting shopping list", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @PostMapping("/add-products")
    public void addProductsToList(@RequestBody ShoppingListRequestDTO shoppingListRequestDTO) {
        try {
            UUID user = getUser();
            log.info("Add products {} to shopping list of user with keycloakId {}: ", shoppingListRequestDTO.getProducts(), user);
            shoppingListService.addProductsInShoppingList(user, shoppingListRequestDTO);
        } catch (Exception e) {
            log.error("Error add products to shopping list", e);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @PostMapping("/remove-products")
    public void removeProductsFromList(@RequestBody ShoppingListRequestDTO shoppingListRequestDTO) {
        try {
            UUID user = getUser();
            log.info("Remove products {} from shopping list of user with keycloakId {}: ", shoppingListRequestDTO.getProducts(), user);
            shoppingListService.removeProductsInShoppingList(user, shoppingListRequestDTO);
        } catch (Exception e) {
            log.error("Error add products to shopping list", e);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @GetMapping("/calculate")
    public ResponseEntity<SliceImpl<ProductsPriceResponseDTO>> calculateList(@RequestParam("cep") String cep, @RequestParam("distance") double distance) {
        try {
            UUID user = getUser();
            log.info("Calculate shopping list of user with keycloakId{}", user);
            return ResponseEntity.ok(shoppingListService.calculateShoppingList(user, cep, distance));
        } catch (Exception e) {
            log.error("Error calculate shopping list", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    private UUID getUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("User: {}", jwt.getClaimAsString("preferred_username"));
        return UUID.fromString(jwt.getSubject());
    }

}
