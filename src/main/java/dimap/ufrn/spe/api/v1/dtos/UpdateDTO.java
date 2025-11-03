package dimap.ufrn.spe.api.v1.dtos;

import dimap.ufrn.spe.api.v1.models.Roles;

public record UpdateDTO(String name, String username, String email, Roles role) {
    
}
