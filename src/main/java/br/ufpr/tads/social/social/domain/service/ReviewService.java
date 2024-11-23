package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.domain.model.CustomerProfile;
import br.ufpr.tads.social.social.domain.model.ProductReview;
import br.ufpr.tads.social.social.domain.repository.CustomerProfileRepository;
import br.ufpr.tads.social.social.domain.repository.ProductReviewRepository;
import br.ufpr.tads.social.social.dto.request.CreateReviewRequestDTO;
import br.ufpr.tads.social.social.dto.request.UpdateReviewRequestDTO;
import br.ufpr.tads.social.social.dto.response.ProductReviewResponseDTO;
import br.ufpr.tads.social.social.dto.response.TotalReviewsResponseDTO;
import br.ufpr.tads.social.social.infrastructure.adapter.CatalogClientImpl;
import br.ufpr.tads.social.social.infrastructure.adapter.ProfileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ReviewService {

    @Autowired
    private CatalogClientImpl catalogClient;

    @Autowired
    private ProfileClient profileClient;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private ProductReviewRepository reviewRepository;

    public ProductReviewResponseDTO createReview(UUID userId, CreateReviewRequestDTO reviewRequestDTO) {
        catalogClient.fetchProductById(reviewRequestDTO.getProductId()).orElseThrow(
                () -> new RuntimeException("Produto não encontrado"));

        CustomerProfile user = customerProfileRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ProductReview review = new ProductReview();
        review.setProductId(reviewRequestDTO.getProductId());
        review.setStoreId(reviewRequestDTO.getStoreId());
        review.setCustomerProfile(user);
        review.setRating(reviewRequestDTO.getRating());
        review.setText(reviewRequestDTO.getComment());
        review.setCreatedAt(LocalDateTime.now());

        return mapToResponseDTO(reviewRepository.save(review));
    }

    public List<ProductReviewResponseDTO> getReviews(UUID productId, Optional<UUID> storeId, Pageable pageable) {
        List<ProductReviewResponseDTO> reviews = new ArrayList<>();

        if (storeId.isPresent()) {
            reviewRepository.findByProductIdAndStoreId(productId, storeId.get(), pageable).forEach(
                    review -> reviews.add(mapToResponseDTO(review))
            );
        } else {
            reviewRepository.findByProductId(productId, pageable).forEach(
                    review -> reviews.add(mapToResponseDTO(review))
            );
        }

        return reviews;
    }

    public void deleteReview(UUID userId, UUID reviewId) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        if (!review.getCustomerProfile().getKeycloakId().equals(userId)) {
            log.error("Usuário não autorizado para excluir esta avaliação");
            throw new RuntimeException("Usuário não autorizado para excluir esta avaliação");
        }

        reviewRepository.delete(review);
    }

    public ProductReviewResponseDTO updateReview(UUID userId, UUID reviewId, UpdateReviewRequestDTO reviewRequestDTO) {
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));

        if (!review.getCustomerProfile().getKeycloakId().equals(userId)) {
            throw new RuntimeException("Usuário não autorizado para atualizar esta avaliação");
        }

        review.setRating(reviewRequestDTO.getRating());
        review.setText(reviewRequestDTO.getComment());
        review.setCreatedAt(LocalDateTime.now());

        return mapToResponseDTO(reviewRepository.save(review));
    }

    private ProductReviewResponseDTO mapToResponseDTO(ProductReview review) {
        ProductReviewResponseDTO responseDTO = new ProductReviewResponseDTO();
        responseDTO.setId(review.getId());
        responseDTO.setProductId(review.getProductId());
        responseDTO.setStoreId(review.getStoreId());
        responseDTO.setUser(profileClient.getProfile(review.getCustomerProfile().getKeycloakId()));
        responseDTO.setReview(review.getText());
        responseDTO.setRating(review.getRating());
        responseDTO.setCreatedAt(review.getCreatedAt().toString());
        return responseDTO;
    }

    public TotalReviewsResponseDTO getTotalReviews() {
        return new TotalReviewsResponseDTO(reviewRepository.count());
    }
}


