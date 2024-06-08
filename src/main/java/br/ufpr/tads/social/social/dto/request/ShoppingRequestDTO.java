package br.ufpr.tads.social.social.dto.request;

import java.util.List;
import java.util.UUID;

public class ShoppingRequestDTO {

    private UUID userId;
    private String cep;
    private List<UUID> products;

}
