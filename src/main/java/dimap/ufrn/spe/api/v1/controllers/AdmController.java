package dimap.ufrn.spe.api.v1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dimap.ufrn.spe.api.v1.dtos.BolsistaPontoDTO;
import dimap.ufrn.spe.api.v1.dtos.PasswordDTO;
import dimap.ufrn.spe.api.v1.dtos.RegisterDTO;
import dimap.ufrn.spe.api.v1.dtos.UpdateDTO;
import dimap.ufrn.spe.api.v1.models.User;
import dimap.ufrn.spe.api.v1.repositories.UserRepository;
import dimap.ufrn.spe.api.v1.services.PontoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("spe/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Tecnico", description = "Endpoints técnicos para gestão de usuários e pontos")
public class AdmController {

    @Autowired
    private PontoService pontoService;

    @Autowired
    private UserRepository repository;

    @Operation(summary = "Registrar um novo usuário", description = "Endpoint para registrar um novo usuário no sistema.", parameters = {
            @Parameter(name = "Authorization", description = "Token JWT no formato: **Bearer <token>**", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou já em uso", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão administrativa", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data, BindingResult result) {
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.username(), encryptedPassword, data.email(), data.roles());

        this.repository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

    @Operation(summary = "Listar todos os pontos", description = "Endpoint para listar todos os pontos registrados no sistema.", parameters = {
            @Parameter(name = "Authorization", description = "Token JWT no formato: **Bearer <token>**", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pontos retornada com sucesso", content = @Content(schema = @Schema(implementation = BolsistaPontoDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão administrativa", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/pontos")
    public List<BolsistaPontoDTO> listarPontos(@AuthenticationPrincipal User admin) {
        return pontoService.listarTodosOsPontos();
    }

    @Operation(summary = "Atualizar senha do bolsista", description = "Endpoint para atualizar a senha do bolsista em caso de esquecimento ou troca de senha.", parameters = {
            @Parameter(name = "Authorization", description = "Token JWT no formato: **Bearer <token>**", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")),
            @Parameter(name = "id", description = "ID do bolsista a ter a senha atualizada", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64", example = "42"))
    }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto contendo a nova senha.", required = true, content = @Content(schema = @Schema(implementation = PasswordDTO.class))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação, senha vazia ou fraca."),
            @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('BOLSISTA')")
    @PutMapping("/mudar-senha/bolsista/{id}")
    public ResponseEntity<String> mudarSenha(@PathVariable("id") Long id, @RequestBody @Valid PasswordDTO dado) {

        User bolsista = repository.findById(id).orElse(null);
        if (bolsista == null) {
            return ResponseEntity.badRequest().body("Erro: bolsista não encontrado.");
        }
        if (dado.senhaNova().isEmpty())
            return ResponseEntity.badRequest().body("Erro: senha vazia.");

        if (dado.senhaNova().length() < 8)
            return ResponseEntity.badRequest().body("Senha muito fraca, mínimo 8 caracteres.");

        var encoder = new BCryptPasswordEncoder();
        bolsista.setPassword(encoder.encode(dado.senhaNova()));

        repository.save(bolsista);

        return ResponseEntity.ok("Password updated successfully");
    }

    @Operation(summary = "Atualizar dados do bolsista", description = "Endpoint para atualizar os dados do bolsista.", parameters = {
            @Parameter(name = "Authorization", description = "Token JWT no formato: **Bearer <token>**", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")),
            @Parameter(name = "id", description = "ID do bolsista a ter os dados atualizados", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64", example = "42"))
    }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto contendo os novos dados do bolsista.", required = true, content = @Content(schema = @Schema(implementation = UpdateDTO.class))))
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('BOLSISTA')")
    @PutMapping("/mudar-dados/bolsista/{id}")
    public ResponseEntity<String> mudarDados(@PathVariable("id") Long id, @RequestBody @Valid UpdateDTO dados) {

        User bolsista = repository.findById(id).orElse(null);
        if (bolsista == null) {
            return ResponseEntity.badRequest().body("Erro: bolsista não encontrado.");
        }

        bolsista.setName(dados.name());
        bolsista.setUsername(dados.username());
        bolsista.setEmail(dados.email());
        bolsista.setRoles(dados.role());

        repository.save(bolsista);

        return ResponseEntity.ok("User data updated successfully.");
    }
}