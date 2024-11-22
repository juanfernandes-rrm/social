package br.ufpr.tads.social.social.dto.commons;

import lombok.Data;

import java.util.UUID;

@Data
public class StoreDTO {

    private UUID id;
    private String name;
    private AddressDTO address;
    private String cnpj;

}
