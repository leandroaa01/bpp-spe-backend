package dimap.ufrn.spe.api.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dimap.ufrn.spe.api.v1.dtos.AuthenticationDTO;
import dimap.ufrn.spe.api.v1.dtos.LoginResponseDTO;
import dimap.ufrn.spe.api.v1.models.User;
import dimap.ufrn.spe.api.v1.repositories.UserRepository;
import dimap.ufrn.spe.api.v1.security.TokenService;
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
    
    }
