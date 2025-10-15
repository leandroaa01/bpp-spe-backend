package dimap.ufrn.spe.api.v1.spe.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dimap.ufrn.spe.api.v1.spe.dtos.BolsistaPontoDTO;
import dimap.ufrn.spe.api.v1.spe.models.User;
import dimap.ufrn.spe.api.v1.spe.services.PontoService;

@RestController
@RequestMapping("/spe/api/admin")
public class AdmController {

    @Autowired
    private PontoService pontoService;

    // 🔹 GET /spe/api/admin/pontos
    @GetMapping("/pontos")
    @CrossOrigin(origins = "http://localhost:5173") // permite frontend local
    public List<BolsistaPontoDTO> listarPontos(@AuthenticationPrincipal User admin) {
        return pontoService.listarTodosOsPontos();
    }
}
