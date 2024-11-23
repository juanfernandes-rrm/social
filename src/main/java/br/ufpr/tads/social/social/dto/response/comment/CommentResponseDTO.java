package br.ufpr.tads.social.social.dto.response.comment;

import br.ufpr.tads.social.social.dto.response.profile.GetUserProfileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {

    private CommentHeaderDTO header;
    private UUID id;
    private GetUserProfileDTO user;
    private String text;
    private LocalDateTime createdAt;
    private UUID parentCommentId;
    private List<ReplyDTO> replies;
    private boolean hasMoreReplies;
}
