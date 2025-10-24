package dimap.ufrn.spe.api.v1.spe.services;

import java.util.List;

import org.springframework.stereotype.Service;

import dimap.ufrn.spe.api.v1.spe.dtos.BolsistaPontoDTO;
import dimap.ufrn.spe.api.v1.spe.repositories.PontoRepository;

@Service
public class PontoService {

    private final PontoRepository pontoRepository;

    public PontoService(PontoRepository pontoRepository) {
        this.pontoRepository = pontoRepository;
    }

    public List<BolsistaPontoDTO> listarTodosOsPontos() {
        var pontos = pontoRepository.findAll();

        return pontos.stream()
                     .map(p -> new BolsistaPontoDTO(
                         p.getBolsista() != null ? p.getBolsista().getName() : " ",
                         p.getHoraDeEntrada(),
                         p.getHoraDeSaida(),
                         p.getQtdDeHorasFeitas()  
                     ))
                     .toList();
    }
}
