package br.ufpr.tads.social.social.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;



@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageWrapper<T> {
    private List<T> content;
    private int number;
    private int size;
    private boolean last;
    private boolean first;
    private boolean empty;
    private Boolean hasNext;

    private Long totalElements;
    private Integer totalPages;

    public Page<T> toPage() {
        Pageable pageable = PageRequest.of(number, size);

        if (totalElements == null || totalPages == null) {
            return new PageImpl<>(content, pageable, hasNext != null && hasNext ? Long.MAX_VALUE : content.size());
        } else {
            return new PageImpl<>(content, pageable, totalElements);
        }
    }
}
