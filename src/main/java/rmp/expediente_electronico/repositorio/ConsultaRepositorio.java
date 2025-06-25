package rmp.expediente_electronico.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import rmp.expediente_electronico.modelo.Consulta;

public interface ConsultaRepositorio extends JpaRepository<Consulta, Integer> {
    
}
