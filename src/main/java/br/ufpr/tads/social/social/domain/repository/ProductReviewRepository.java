package br.ufpr.tads.social.social.domain.repository;

import br.ufpr.tads.social.social.domain.model.CustomerProfile;
import br.ufpr.tads.social.social.domain.model.ProductReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {
    List<ProductReview> findByProductIdAndStoreId(UUID productId, UUID storeId, Pageable pageable);

    List<ProductReview> findByProductId(UUID productId, Pageable pageable);

    Slice<ProductReview> findByApprovedNull(Pageable pageable);

    Slice<ProductReview> findByCustomerProfile(CustomerProfile customerProfile, Pageable pageable);
}
