package br.ufpr.tads.social.social.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedCommentsResponseDTO {
    private List<CommentResponseDTO> content; // Lista de comentários
    private PageableDTO pageable; // Dados de paginação
    private boolean hasNext; // Indica se há mais páginas
}
