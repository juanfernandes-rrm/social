package br.ufpr.tads.social.social.application.service;

import br.ufpr.tads.social.social.domain.exception.BusinessException;
import br.ufpr.tads.social.social.domain.service.FavoriteProductService;
import br.ufpr.tads.social.social.domain.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/product")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private FavoriteProductService favoriteProductService;

    //TODO: Ajustar para adicionar o parâmetro de favorito
    @GetMapping
    public ResponseEntity<?> getProductCards(@RequestParam("page") int page, @RequestParam("size") int size,
                                         @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            log.info("Getting product cards for page {}", pageable);
            return ResponseEntity.ok(postService.getCards(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    //Talvez mover para favorite controller
    @PostMapping("/favorite/{productId}")
    public ResponseEntity<?> favoriteProduct(@PathVariable("productId") UUID productId) {
        try {
            log.info("Favorite product {}", productId);
            favoriteProductService.addFavorite(getUser(), productId);
            return ResponseEntity.ok().build();
        }catch (BusinessException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PostMapping("/favorite/remove/{productId}")
    public ResponseEntity<?> removeFavoriteProduct(@PathVariable("productId") UUID productId) {
        try {
            log.info("Favorite product {}", productId);
            favoriteProductService.removeFavorite(getUser(), productId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites() {
        return null;
    }

    //endpoint para fazer review de um produto
    @PostMapping("/review/{productId}")
    public ResponseEntity<?> reviewProduct(@PathVariable("productId") UUID productId) {
        return null;
    }

    //endpoint para pegar os reviews de um produto
    @GetMapping("/reviews/{productId}")
    public ResponseEntity<?> getProductReview(@PathVariable("productId") UUID id) {
        return null;
    }

    @GetMapping("/comments/{productId}")
    public ResponseEntity<?> getProductComments(@PathVariable("productId") UUID id) {
        return null;
    }

    private UUID getUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("User: {}", jwt.getClaimAsString("preferred_username"));
        return UUID.fromString(jwt.getSubject());
    }

}
