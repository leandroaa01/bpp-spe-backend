package dimap.ufrn.spe.api.v1.spe.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dimap.ufrn.spe.api.v1.spe.dtos.BolsistaPontoDTO;
import dimap.ufrn.spe.api.v1.spe.dtos.RegisterDTO;
import dimap.ufrn.spe.api.v1.spe.models.User;
import dimap.ufrn.spe.api.v1.spe.repositories.UserRepository;
import dimap.ufrn.spe.api.v1.spe.services.PontoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("spe/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdmController {

    @Autowired
    private PontoService pontoService;

    @Autowired
     private UserRepository repository;

   @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data, BindingResult result) {
    if (result.hasErrors()) {
        return ResponseEntity.badRequest().body(result.getAllErrors());
    }

    if (this.repository.findByUsername(data.username()) != null) {
        result.rejectValue("username", "409", "Username already in use");
        return ResponseEntity.badRequest().body("Username already in use");
    }

    String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
    User newUser = new User(data.name(), data.username(), encryptedPassword, data.email(), data.roles());

    this.repository.save(newUser);

    return ResponseEntity.ok("User registered successfully");
}


   
    @GetMapping("/pontos")
   
    public List<BolsistaPontoDTO> listarPontos(@AuthenticationPrincipal User admin) {
        return pontoService.listarTodosOsPontos();
    }
}
