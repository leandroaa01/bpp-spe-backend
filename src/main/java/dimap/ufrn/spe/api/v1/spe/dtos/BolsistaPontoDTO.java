package dimap.ufrn.spe.api.v1.spe.dtos;

import java.time.LocalDateTime;

public record BolsistaPontoDTO(
long id,
    LocalDateTime horaDeEntrada,
    LocalDateTime horaDeSaida,
    double qtdDeHorasFeitas
) {}
