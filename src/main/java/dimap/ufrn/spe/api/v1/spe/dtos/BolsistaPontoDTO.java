package dimap.ufrn.spe.api.v1.spe.dtos;

import java.time.LocalDateTime;

public record BolsistaPontoDTO(
String nome,
    LocalDateTime horaDeEntrada,
    LocalDateTime horaDeSaida,
    double qtdDeHorasFeitas
) {}
