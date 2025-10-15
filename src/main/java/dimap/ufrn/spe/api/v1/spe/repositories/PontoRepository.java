package dimap.ufrn.spe.api.v1.spe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dimap.ufrn.spe.api.v1.spe.models.Ponto;
import dimap.ufrn.spe.api.v1.spe.models.User;

public interface PontoRepository extends JpaRepository<Ponto, Long> {

   

    List<Ponto> findAllByBolsista(User bolsista);
}
