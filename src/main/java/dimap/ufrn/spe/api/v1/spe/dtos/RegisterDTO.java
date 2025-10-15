package dimap.ufrn.spe.api.v1.spe.dtos;

import dimap.ufrn.spe.api.v1.spe.models.Roles;

public record RegisterDTO(String name, String username,String password, String email, Roles roles) {
    
}
