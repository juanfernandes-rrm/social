package br.ufpr.tads.social.social.dto.response.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserProfileDTO {

    private UUID id;
    private UUID keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;

    public GetUserProfileDTO(UUID id, UUID keycloakId) {
        this.id = id;
        this.keycloakId = keycloakId;
    }
}
