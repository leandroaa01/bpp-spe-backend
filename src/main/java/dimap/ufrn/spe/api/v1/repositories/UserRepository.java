package dimap.ufrn.spe.api.v1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import dimap.ufrn.spe.api.v1.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByUsername(String username);

    
     
}
