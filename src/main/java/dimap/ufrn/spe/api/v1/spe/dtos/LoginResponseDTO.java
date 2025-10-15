package dimap.ufrn.spe.api.v1.spe.dtos;

import dimap.ufrn.spe.api.v1.spe.models.Roles;

public record LoginResponseDTO(String token, Roles role) {
    
}
