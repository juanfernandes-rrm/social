package br.ufpr.tads.social.social.dto.commons;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductDTO {

    private UUID id;
    private String name;
    private String code;
    private String category;
    private String image;
    private String price;
    private String unit;
    private int quantity;
    private UUID storeId;
    private String description;

}
