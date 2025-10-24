package dimap.ufrn.spe.api.v1.controllers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dimap.ufrn.spe.api.v1.dtos.PontoDTO;
import dimap.ufrn.spe.api.v1.models.Ponto;
import dimap.ufrn.spe.api.v1.models.User;
import dimap.ufrn.spe.api.v1.repositories.PontoRepository;
import dimap.ufrn.spe.api.v1.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/spe/api/bolsista")
public class BolsistaController {

    @Autowired
    private PontoRepository pontoRepository;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Registrar entrada", description = "Endpoint para registrar a entrada de um bolsista.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrada registrada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Erro ao registrar entrada."),
        @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.")
    })
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

    @Operation(summary = "Registrar saída", description = "Endpoint para registrar a saída de um bolsista.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saída registrada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Erro ao registrar saída."),
        @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.")
    })
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

    @Operation(summary = "Visualizar pontos", description = "Endpoint para visualizar os pontos registrados pelo bolsista.")
    @ApiResponses(value = {
       @ApiResponse(responseCode = "200", description = "Pontos retornados com sucesso.",
                 content = @Content(array = @ArraySchema(schema = @Schema(implementation = PontoDTO.class)))),
        @ApiResponse(responseCode = "403", description = "Acesso negado, sem permissão de bolsista.",
                 content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PreAuthorize("hasRole('BOLSISTA')")
    @GetMapping("/meus-pontos")
    public Stream<Object> visualizarMeusPontos(@AuthenticationPrincipal User bolsista) {
        var pontos = pontoRepository.findAllByBolsista(bolsista);

        return pontos.stream()
                .map(p -> new PontoDTO(p.getHoraDeEntrada(), p.getHoraDeSaida(),
                        String.valueOf(p.getQtdDeHorasFeitas()) + " Hrs"));
    }

    @Operation(summary = "Obter total de horas", description = "Endpoint para obter o total de horas trabalhadas pelo bolsista.")
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
}
