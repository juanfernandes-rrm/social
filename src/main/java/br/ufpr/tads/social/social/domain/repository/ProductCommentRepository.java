package br.ufpr.tads.social.social.domain.repository;

import br.ufpr.tads.social.social.domain.model.ProductComment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductCommentRepository extends JpaRepository<ProductComment, UUID> {
    Slice<ProductComment> findByProductIdAndStoreId(UUID productId, UUID storeId, Pageable pageable);

    SliceImpl<ProductComment> findByProductId(UUID productId, Pageable pageable);

    Slice<ProductComment> findByParentCommentIsNullAndProductId(UUID productId, Pageable pageable);

    List<ProductComment> findByParentCommentId(UUID id, PageRequest pageRequest);

    Slice<ProductComment> findByParentCommentIsNullAndProductIdAndStoreId(UUID productId, UUID storeId, Pageable pageable);

    Slice<ProductComment> findByParentCommentIsNullAndReviewId(UUID reviewId, Pageable pageable);
}
