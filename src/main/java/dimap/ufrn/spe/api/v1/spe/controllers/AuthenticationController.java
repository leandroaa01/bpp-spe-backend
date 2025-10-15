package dimap.ufrn.spe.api.v1.spe.controllers;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import dimap.ufrn.spe.api.v1.spe.dtos.AuthenticationDTO;
import dimap.ufrn.spe.api.v1.spe.dtos.LoginResponseDTO;
import dimap.ufrn.spe.api.v1.spe.dtos.RegisterDTO;
import dimap.ufrn.spe.api.v1.spe.models.User;
import dimap.ufrn.spe.api.v1.spe.repositories.UserRepository;
import dimap.ufrn.spe.api.v1.spe.security.TokenService;
import jakarta.validation.Valid;
    
    @RestController
    @RequestMapping("auth")
    public class AuthenticationController {
        @Autowired
        private AuthenticationManager authenticationManager;
        @Autowired
        private UserRepository repository;
        @Autowired
        private TokenService tokenService;
    
        @PostMapping("/login")
        public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
    
            var token = tokenService.generateToken((User) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token, ((User) auth.getPrincipal()).getRoles()));
        }
    
        @PostMapping("/register")
        public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
            if(this.repository.findByUsername(data.username()) != null) return ResponseEntity.badRequest().build();
    
            String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
            User newUser = new User(data.name(),data.username(), encryptedPassword,data.email(), data.roles());
    
            this.repository.save(newUser);
    
            return ResponseEntity.ok().build();
        }
    }
