package dimap.ufrn.spe.api.v1.controllers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dimap.ufrn.spe.api.v1.dtos.DadosDTO;
import dimap.ufrn.spe.api.v1.dtos.PasswordDTO;
import dimap.ufrn.spe.api.v1.dtos.PontoDTO;
import dimap.ufrn.spe.api.v1.models.Ponto;
import dimap.ufrn.spe.api.v1.models.User;
import dimap.ufrn.spe.api.v1.repositories.PontoRepository;
import dimap.ufrn.spe.api.v1.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/spe/api/bolsista")
public class BolsistaController {

    @Autowired
    private PontoRepository pontoRepository;

    @Autowired
    private UserRepository userRepository;

    
@Operation(
    summary = "Registrar entrada",
    description = "Endpoint para registrar a entrada de um bolsista.",
    parameters = {
        @Parameter(
            name = "Authorization",
            description = "Token JWT no formato: **Bearer <token>**",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        )
    }
)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrada registrada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Erro ao registrar entrada."),
        @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('BOLSISTA')")
    @PostMapping("/entrada")
    public String registrarEntrada(@AuthenticationPrincipal User bolsista) {
        // Verifica se já existe um ponto aberto para o bolsista
        Optional<Ponto> pontoAberto = pontoRepository.findAllByBolsista(bolsista)
                .stream()
                .filter(p -> p.getHoraDeSaida() == null)
                .findFirst();

        if (pontoAberto.isPresent()) {
            return registrarSaida(bolsista);
        }

        Ponto novoPonto = new Ponto();
        novoPonto.setBolsista(bolsista);
        novoPonto.setHoraDeEntrada(LocalDateTime.now());
        pontoRepository.save(novoPonto);

        return "Entrada registrada com sucesso!";
    }

    @Operation(summary = "Registrar saída", description = "Endpoint para registrar a saída de um bolsista.",parameters = {
        @Parameter(
            name = "Authorization",
            description = "Token JWT no formato: **Bearer <token>**",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        )
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saída registrada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Erro ao registrar saída."),
        @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.")
    })

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('BOLSISTA')")
    @PostMapping("/saida")
    public String registrarSaida(@AuthenticationPrincipal User bolsista) {
        // Busca o último ponto aberto do bolsista
        Optional<Ponto> pontoAberto = pontoRepository.findAllByBolsista(bolsista)
                .stream()
                .filter(p -> p.getHoraDeSaida() == null)
                .findFirst();

        if (pontoAberto.isEmpty()) {
            return "Nenhum ponto aberto para finalizar.";
        }

        Ponto ponto = pontoAberto.get();
        ponto.setHoraDeSaida(LocalDateTime.now());
        ponto.calcularHorasFeitas();

        pontoRepository.save(ponto);
        return "Saída registrada com sucesso! Total de horas: " + ponto.getQtdDeHorasFeitas();
    }

    @Operation(summary = "Visualizar pontos", description = "Endpoint para visualizar os pontos registrados pelo bolsista.",parameters = {
        @Parameter(
            name = "Authorization",
            description = "Token JWT no formato: **Bearer <token>**",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        )
    })
    @ApiResponses(value = {
       @ApiResponse(responseCode = "200", description = "Pontos retornados com sucesso.",
                 content = @Content(array = @ArraySchema(schema = @Schema(implementation = PontoDTO.class)))),
        @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.",
                 content = @Content(schema = @Schema(implementation = String.class)))
    })

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('BOLSISTA')")
    @GetMapping("/meus-pontos")
    public Stream<Object> visualizarMeusPontos(@AuthenticationPrincipal User bolsista) {
        var pontos = pontoRepository.findAllByBolsista(bolsista);

        return pontos.stream()
                .map(p -> new PontoDTO(p.getHoraDeEntrada(), p.getHoraDeSaida(),
                        String.valueOf(p.getQtdDeHorasFeitas()) + " Hrs"));
    }

    @Operation(summary = "Obter total de horas", description = "Endpoint para obter o total de horas trabalhadas pelo bolsista.",parameters = {
        @Parameter(
            name = "Authorization",
            description = "Token JWT no formato: **Bearer <token>**",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        )
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total de horas retornado com sucesso."),
        @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.")
    })
    @PreAuthorize("hasRole('BOLSISTA')")
    @GetMapping("/total-horas")
    public String getTotalHoras(@AuthenticationPrincipal User bolsista) {
        var totalHoras = pontoRepository.findAllByBolsista(bolsista)
                .stream()
                .mapToDouble(Ponto::getQtdDeHorasFeitas)
                .sum();
        totalHoras = Math.round(totalHoras * 100.0) / 100.0; 
        return "Total de horas trabalhadas: " + totalHoras + " Hrs";
    }

    @Operation(
    summary = "Atualizar meus dados",
    description = "Endpoint para o bolsista atualizar seu nome, username e email.",
    parameters = {
        @Parameter(
            name = "Authorization",
            description = "Token JWT no formato: **Bearer <token>**",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        )
    },
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Dados do bolsista para atualização.",
        required = true,
        content = @Content(
            schema = @Schema(implementation = DadosDTO.class)
        )
    )
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso."),
    @ApiResponse(responseCode = "400", description = "Erro de validação ou conflito de dados."),
    @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.")
})
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('BOLSISTA')")
@PutMapping("/update/meus-dados")
public ResponseEntity<String> atualizarMeusDados(
        @AuthenticationPrincipal User bolsista,
        @RequestBody @Valid DadosDTO dados) {

    var usuarioComMesmoUsername = userRepository.findByUsername(dados.username());
    if (usuarioComMesmoUsername != null && usuarioComMesmoUsername.getId() != bolsista.getId()) {
        return ResponseEntity.badRequest().body("Username indisponível.");
    }

    var usuarioComMesmoEmail = userRepository.findByEmail(dados.email());
    if (usuarioComMesmoEmail != null && usuarioComMesmoEmail.getId() != bolsista.getId()) {
        return ResponseEntity.badRequest().body("Email já em uso.");
    }

    bolsista.setName(dados.name());
    bolsista.setUsername(dados.username());
    bolsista.setEmail(dados.email());

    userRepository.save(bolsista);

    return ResponseEntity.ok("Dados atualizados com sucesso.");
}

@Operation(
    summary = "Atualizar senha do bolsista",
    description = "Endpoint para o bolsista alterar sua senha atual por uma nova senha.",
    parameters = {
        @Parameter(
            name = "Authorization",
            description = "Token JWT no formato: **Bearer <token>**",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        )
    },
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Objeto contendo a nova senha.",
        required = true,
        content = @Content(
            schema = @Schema(implementation = PasswordDTO.class)
        )
    )
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso."),
    @ApiResponse(responseCode = "400", description = "Erro de validação, senha vazia ou fraca."),
    @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.")
})
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('BOLSISTA')")
@PutMapping("/update/meu-password")
public ResponseEntity<String> mudarSenha(
        @AuthenticationPrincipal User bolsista,
        @RequestBody @Valid PasswordDTO dado) {

    if (dado.senhaNova().isEmpty())
        return ResponseEntity.badRequest().body("Erro: senha vazia.");

    if (dado.senhaNova().length() < 8)
        return ResponseEntity.badRequest().body("Senha muito fraca, mínimo 8 caracteres.");

    var encoder = new BCryptPasswordEncoder();
    bolsista.setPassword(encoder.encode(dado.senhaNova()));

    userRepository.save(bolsista);

    return ResponseEntity.ok("Senha atualizada com sucesso.");
}

}
