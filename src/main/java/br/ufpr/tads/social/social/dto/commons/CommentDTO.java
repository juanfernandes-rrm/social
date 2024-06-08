package br.ufpr.tads.social.social.dto.commons;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CommentDTO {

    private UUID userId;
    private String text;
    private LocalDateTime createdAt;
    private CommentDTO parentComment;
    private List<CommentDTO> replies;

}
