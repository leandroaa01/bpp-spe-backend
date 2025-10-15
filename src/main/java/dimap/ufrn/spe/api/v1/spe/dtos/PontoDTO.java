package dimap.ufrn.spe.api.v1.spe.dtos;

import java.time.LocalDateTime;

public record PontoDTO( LocalDateTime horasDeEntrada, LocalDateTime horasDeSaida, String qtdDeHorasFeitas) {

}
