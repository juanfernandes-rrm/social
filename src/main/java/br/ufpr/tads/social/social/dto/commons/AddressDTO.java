package br.ufpr.tads.social.social.dto.commons;

import lombok.Data;

@Data
public class AddressDTO {

    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;

}
