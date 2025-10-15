package dimap.ufrn.spe.api.v1.spe.models;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="pontos")
public class Ponto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime horaDeEntrada;
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime horaDeSaida;
    private double qtdDeHorasFeitas;

    @ManyToOne
    @JoinColumn(name = "bolsista_id")
    private User bolsista;

    public void calcularHorasFeitas() {
        if (isHorarioValido()) {
            this.qtdDeHorasFeitas = calcularDiferencaHoras(horaDeEntrada, horaDeSaida);
        }
    }

    private boolean isHorarioValido() {
        return horaDeEntrada != null && horaDeSaida != null && !horaDeSaida.isBefore(horaDeEntrada);
    }

    private double calcularDiferencaHoras(LocalDateTime entrada, LocalDateTime saida) {
        final int MINUTES_IN_AN_HOUR = 60;
        long minutos = Duration.between(entrada, saida).toMinutes();
        return Math.round((double) minutos / MINUTES_IN_AN_HOUR * 100.0) / 100.0;
    }
}