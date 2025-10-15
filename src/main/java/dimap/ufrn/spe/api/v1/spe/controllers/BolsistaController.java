package dimap.ufrn.spe.api.v1.spe.controllers;

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

import dimap.ufrn.spe.api.v1.spe.dtos.PontoDTO;
import dimap.ufrn.spe.api.v1.spe.models.Ponto;
import dimap.ufrn.spe.api.v1.spe.models.User;
import dimap.ufrn.spe.api.v1.spe.repositories.PontoRepository;
import dimap.ufrn.spe.api.v1.spe.repositories.UserRepository;
@RestController
@RequestMapping("/spe/api/bolsista")
public class BolsistaController {

    @Autowired
    private PontoRepository pontoRepository;

    @Autowired
    private UserRepository userRepository;

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

    @PreAuthorize("hasRole('BOLSISTA')")
    @GetMapping("/meus-pontos")
    public Stream<Object> visualizarMeusPontos(@AuthenticationPrincipal User bolsista) {
        var pontos = pontoRepository.findAllByBolsista(bolsista);

    return pontos.stream()
                 .map(p -> new PontoDTO(p.getHoraDeEntrada(),p.getHoraDeSaida(),String.valueOf(p.getQtdDeHorasFeitas())+" Hrs"));
    }
}
