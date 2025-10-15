package dimap.ufrn.spe.api.v1.spe;

import dimap.ufrn.spe.api.v1.spe.models.Roles;
import dimap.ufrn.spe.api.v1.spe.models.User;
import dimap.ufrn.spe.api.v1.spe.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class SpeApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpeApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existe um usuário admin
        if (userRepository.findByUsername("redes") == null) {
            // Criptografar a senha
            var encryptedPassword = new BCryptPasswordEncoder().encode("123456789");

            // Criar o usuário administrador com o papel ADMIN
            User adminUser = new User();
            adminUser.setName("Redes");
            adminUser.setUsername("redes");
            adminUser.setPassword(encryptedPassword);
            adminUser.setEmail("redes@dimap.ufrn.br");
            adminUser.setRoles(Roles.ADMIN); // Usando o enum Roles.ADMIN

            // Salvar no repositório
            userRepository.save(adminUser);

            System.out.println("Usuário administrador criado automaticamente.");
        }
    }
}
