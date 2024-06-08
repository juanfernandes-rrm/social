package br.ufpr.tads.social.social.dto.request;

import java.util.UUID;

public class PostCommentRequestDTO {

    private UUID userId;
    private UUID parentCommentId;
    private String comment;

}
