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
import java.util.ArrayList;
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

        return buildCommentResponseWithReplies(productCommentRepository.save(comment), 0, 0, 0);
    }

    public PaginatedCommentsResponseDTO getComments(UUID productId, UUID storeId, int maxRepliesPerLevel, int maxDepth, Pageable pageable) {
        Slice<ProductComment> rootComments = getRootComments(productId, storeId, pageable);

        List<CommentResponseDTO> comments = rootComments.getContent().stream()
                .map(rootComment -> buildCommentResponseWithReplies(rootComment, maxDepth, 0, maxRepliesPerLevel))
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

    //TODO: adicionar comentários no retorno das consultas das reviews
    public PaginatedCommentsResponseDTO getReviewComments(UUID reviewId, int maxRepliesPerLevel, int maxDepth, Pageable pageable) {
        Slice<ProductComment> rootComments = getRootReviewComments(reviewId, pageable);

        List<CommentResponseDTO> comments = rootComments.getContent().stream()
                .map(rootComment -> buildCommentResponseWithReplies(rootComment, maxDepth, 0, maxRepliesPerLevel))
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

    private Slice<ProductComment> getRootReviewComments(UUID reviewId, Pageable pageable) {
        return productCommentRepository.findByParentCommentIsNullAndReviewId(reviewId, pageable);
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

    private CommentResponseDTO buildCommentResponseWithReplies(ProductComment comment, int maxDepth, int currentDepth, int maxRepliesPerLevel) {
        // Constrói o comentário básico
        CommentResponseDTO rootComment = buildRootComment(comment);

        // Se o nível atual for menor que o máximo, buscamos as respostas
        if (currentDepth < maxDepth) {
            // Usando maxRepliesPerLevel para determinar o número máximo de respostas por nível
            List<ProductComment> replies = productCommentRepository.findByParentCommentId(comment.getId(), PageRequest.of(0, maxRepliesPerLevel));
            List<ReplyDTO> repliesDTO = replies.stream()
                    .map(reply -> buildReplyDTO(reply, maxDepth, currentDepth + 1, maxRepliesPerLevel))
                    .collect(Collectors.toList());
            rootComment.setReplies(repliesDTO);  // Aqui ajustamos para `ReplyDTO`
            rootComment.setHasMoreReplies(replies.size() == maxRepliesPerLevel); // Indica se há mais respostas (pode ajustar com base em sua lógica de paginação)
        } else {
            // Indica que há mais replies caso o nível máximo tenha sido atingido
            rootComment.setHasMoreReplies(true);
        }

        return rootComment;
    }

    private static CommentResponseDTO buildRootComment(ProductComment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .productId(comment.getProductId())
                .storeId(comment.getStoreId())
                .reviewId(comment.getReview() != null ? comment.getReview().getId() : null)
                .user(new GetUserProfileDTO(comment.getUserProfile().getId(), comment.getUserProfile().getKeycloakId()))
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .build();
    }


    private ReplyDTO buildReplyDTO(ProductComment reply, int maxDepth, int currentDepth, int maxRepliesPerLevel) {
        ReplyDTO replyDTO = ReplyDTO.builder()
                .id(reply.getId())
                .user(new GetUserProfileDTO(reply.getUserProfile().getId(), reply.getUserProfile().getKeycloakId()))
                .text(reply.getText())
                .createdAt(reply.getCreatedAt())
                .parentCommentId(reply.getParentComment() != null ? reply.getParentComment().getId() : null)
                .replies(new ArrayList<>())
                .hasMoreReplies(false)
                .build();

        // Recursão para buscar replies dentro das replies
        if (currentDepth < maxDepth) {
            // Usando maxRepliesPerLevel para determinar o número máximo de respostas por nível
            List<ProductComment> nestedReplies = productCommentRepository.findByParentCommentId(reply.getId(), PageRequest.of(0, maxRepliesPerLevel));
            List<ReplyDTO> nestedRepliesDTO = nestedReplies.stream()
                    .map(nestedReply -> buildReplyDTO(nestedReply, maxDepth, currentDepth + 1, maxRepliesPerLevel))
                    .collect(Collectors.toList());
            replyDTO.setReplies(nestedRepliesDTO);
            replyDTO.setHasMoreReplies(nestedReplies.size() == maxRepliesPerLevel); // Indica se há mais respostas
        }

        return replyDTO;
    }




}

