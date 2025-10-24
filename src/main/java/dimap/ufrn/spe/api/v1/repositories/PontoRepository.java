package dimap.ufrn.spe.api.v1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dimap.ufrn.spe.api.v1.models.Ponto;
import dimap.ufrn.spe.api.v1.models.User;

public interface PontoRepository extends JpaRepository<Ponto, Long> {

   

    List<Ponto> findAllByBolsista(User bolsista);
}
