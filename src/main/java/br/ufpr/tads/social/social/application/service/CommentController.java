package br.ufpr.tads.social.social.application.service;

import br.ufpr.tads.social.social.domain.service.CommentService;
import br.ufpr.tads.social.social.dto.request.CreateCommentRequestDTO;
import br.ufpr.tads.social.social.dto.response.comment.CommentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CreateCommentRequestDTO request) {
        try {
            UUID user = getUser();
            log.info("Creating comment {} for user with keycloakId {}", request, user);
            return ResponseEntity.ok(commentService.createComment(user, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getCommentsByProduct(@PathVariable UUID productId,
                                                                              @RequestParam(value = "storeId", required = false) UUID storeId,
                                                                              @RequestParam(value = "maxDepth", required = false, defaultValue = "2") int maxDepth,
                                                                              @RequestParam(value = "maxRepliesPerLevel", required = false, defaultValue = "3") int maxRepliesPerLevel,
                                                                              @RequestParam("page") int page, @RequestParam("size") int size,
                                                                              @RequestParam("sortDirection") Sort.Direction sortDirection, @RequestParam("sortBy") String sortBy) {
        log.info("Getting comments for product with id {}", productId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(commentService.getComments(productId, storeId, maxRepliesPerLevel, maxDepth, pageable));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private UUID getUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("User: {}", jwt.getClaimAsString("preferred_username"));
        return UUID.fromString(jwt.getSubject());
    }
}

