package dimap.ufrn.spe.api.v1.spe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import dimap.ufrn.spe.api.v1.spe.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByUsername(String username);

    
     
}
