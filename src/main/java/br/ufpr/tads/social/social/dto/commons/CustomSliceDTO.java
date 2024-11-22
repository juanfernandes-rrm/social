package br.ufpr.tads.social.social.dto.commons;

import lombok.Data;

import java.util.List;

@Data
public class CustomSliceDTO<T> {
    private List<T> content;
    private boolean hasNext;
    private int pageNumber;
    private int pageSize;
}
