package br.ufpr.tads.social.social.domain.service;

import br.ufpr.tads.social.social.domain.model.ProductComment;
import br.ufpr.tads.social.social.domain.model.ProductReview;
import br.ufpr.tads.social.social.domain.repository.ProductCommentRepository;
import br.ufpr.tads.social.social.domain.repository.ProductReviewRepository;
import br.ufpr.tads.social.social.dto.request.CreateCommentRequestDTO;
import br.ufpr.tads.social.social.dto.response.comment.*;
import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import br.ufpr.tads.social.social.infrastructure.adapter.CatalogClientImpl;
import br.ufpr.tads.social.social.infrastructure.adapter.ProfileClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class CommentService {

    @Autowired
    private ProductCommentRepository productCommentRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private CatalogClientImpl catalogClient;

    @Autowired
    private ProfileClient profileClient;

    public CommentResponseDTO createComment(UUID userKeycloakId, CreateCommentRequestDTO createCommentRequestDTO) {
        validateComment(createCommentRequestDTO);

        ProductComment comment = new ProductComment();
        comment.setProductId(createCommentRequestDTO.getProductId());
        comment.setStoreId(createCommentRequestDTO.getStoreId());
        comment.setUserProfile(userProfileService.getProfileByKeycloakId(userKeycloakId));
        comment.setText(createCommentRequestDTO.getText());
        comment.setCreatedAt(LocalDateTime.now());

        if (nonNull(createCommentRequestDTO.getParentCommentId())) {
            ProductComment parentComment = productCommentRepository.findById(createCommentRequestDTO.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        if (nonNull(createCommentRequestDTO.getReviewId())) {
            ProductReview productReview = productReviewRepository.findById(createCommentRequestDTO.getReviewId())
                    .orElseThrow(() -> new IllegalArgumentException("Review not found"));
            comment.setReview(productReview);
        }

        return buildCommentResponse(productCommentRepository.save(comment), 1);
    }

    public PaginatedCommentsResponseDTO getComments(UUID productId, UUID storeId, int maxReplies, Pageable pageable) {

        Slice<ProductComment> rootComments = getRootComments(productId, storeId, pageable);

        List<CommentResponseDTO> comments = rootComments.getContent().stream()
                .map(rootComment -> buildCommentResponse(rootComment, maxReplies))
                .collect(Collectors.toList());

        return PaginatedCommentsResponseDTO.builder()
                .content(comments)
                .pageable(PageableDTO.builder()
                        .pageNumber(rootComments.getNumber())
                        .pageSize(rootComments.getSize())
                        .build())
                .hasNext(rootComments.hasNext())
                .build();
    }

    private Slice<ProductComment> getRootComments(UUID productId, UUID storeId, Pageable pageable) {
        if (nonNull(storeId)) {
            return productCommentRepository.findByParentCommentIsNullAndProductIdAndStoreId(
                    productId,
                    storeId,
                    pageable
            );
        }

        return productCommentRepository.findByParentCommentIsNullAndProductId(
                productId,
                pageable
        );
    }

    public SliceImpl<ProductComment> getRootCommentsByProduct(UUID productId, Pageable pageable) {
        Slice<ProductComment> commentSlice = productCommentRepository.findByParentCommentIsNullAndProductId(productId, pageable);
        return new SliceImpl<>(commentSlice.getContent(), commentSlice.getPageable(), commentSlice.hasNext());
    }

    public void deleteComment(UUID commentId) {
        if (!productCommentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Comment not found");
        }
        productCommentRepository.deleteById(commentId);
    }

    public CommentResponseDTO replyToComment(UUID userKeycloakId, CreateCommentRequestDTO createCommentRequestDTO) {
        return createComment(userKeycloakId, createCommentRequestDTO);
    }

    private void validateComment(CreateCommentRequestDTO createCommentRequestDTO) {
        catalogClient.fetchProductById(createCommentRequestDTO.getProductId(), false).orElseThrow(
                () -> new RuntimeException("Product not found"));

        profileClient.getStoreBranch(createCommentRequestDTO.getStoreId()).orElseThrow(
                () -> new RuntimeException("Store not found"));
    }

    private CommentResponseDTO buildCommentResponse(ProductComment comment, int maxReplies) {
        CommentHeaderDTO header = CommentHeaderDTO.builder()
                .productId(comment.getProductId())
                .storeId(comment.getStoreId())
                .reviewId(nonNull(comment.getReview()) ? comment.getReview().getId() : null)
                .build();

        Slice<ProductComment> repliesSlice = productCommentRepository.findByParentCommentId(
                comment.getId(),
                PageRequest.of(0, maxReplies)
        );

        List<ReplyDTO> replies = repliesSlice.getContent().stream()
                .map(reply -> ReplyDTO.builder()
                        .id(reply.getId())
                        .user(new GetUserProfileDTO(
                                reply.getUserProfile().getId(),
                                reply.getUserProfile().getKeycloakId()
                        ))
                        .text(reply.getText())
                        .createdAt(reply.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return CommentResponseDTO.builder()
                .header(header)
                .id(comment.getId())
                .user(new GetUserProfileDTO(
                        comment.getUserProfile().getId(),
                        comment.getUserProfile().getKeycloakId()
                ))
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .replies(replies)
                .hasMoreReplies(repliesSlice.hasNext())
                .build();
    }

}

