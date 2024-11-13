package br.ufpr.tads.social.social.dto.response.profile;

import lombok.Data;

import java.util.UUID;

@Data
public class GetUserProfileDTO {

    private UUID id;
    private UUID keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;

}
