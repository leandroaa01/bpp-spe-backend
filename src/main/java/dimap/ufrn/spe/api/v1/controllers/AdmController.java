package dimap.ufrn.spe.api.v1.controllers;

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

import dimap.ufrn.spe.api.v1.dtos.BolsistaPontoDTO;
import dimap.ufrn.spe.api.v1.dtos.RegisterDTO;
import dimap.ufrn.spe.api.v1.models.User;
import dimap.ufrn.spe.api.v1.repositories.UserRepository;
import dimap.ufrn.spe.api.v1.services.PontoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("spe/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdmController {

    @Autowired
    private PontoService pontoService;

    @Autowired
     private UserRepository repository;

  @Operation(summary = "Registrar um novo usuário", description = "Endpoint para registrar um novo usuário no sistema.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso"),
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou já em uso", 
                 content = @Content(schema = @Schema(implementation = String.class))),
    @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão administrativa", 
                 content = @Content(schema = @Schema(implementation = String.class)))
})
   @PostMapping("/register")
   public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data, BindingResult result) {
       if (result.hasErrors()) {
           return ResponseEntity.badRequest().body(result.getAllErrors());
       }

       if (this.repository.findByUsername(data.username()) != null) {
           result.rejectValue("username", "409", "Username already in use");
           return ResponseEntity.badRequest().body("Username already in use");
       }

       if(this.repository.findByEmail(data.email()) != null){
           result.rejectValue("email", "409", "Email already in use");
           return ResponseEntity.badRequest().body("Email already in use");
       }

       String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
       User newUser = new User(data.name(), data.username(), encryptedPassword, data.email(), data.roles());

       this.repository.save(newUser);

       return ResponseEntity.ok("User registered successfully");
   }

   @Operation(summary = "Listar todos os pontos", description = "Endpoint para listar todos os pontos registrados no sistema.")
   @ApiResponses(value = {
       @ApiResponse(responseCode = "200", description = "Lista de pontos retornada com sucesso", 
                    content = @Content(schema = @Schema(implementation = BolsistaPontoDTO.class))),
       @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão administrativa", 
                    content = @Content(schema = @Schema(implementation = String.class)))
   })
   @GetMapping("/pontos")
   public List<BolsistaPontoDTO> listarPontos(@AuthenticationPrincipal User admin) {
       return pontoService.listarTodosOsPontos();
   }
}
