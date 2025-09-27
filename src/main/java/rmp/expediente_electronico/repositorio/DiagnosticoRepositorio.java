package rmp.expediente_electronico.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import rmp.expediente_electronico.modelo.Diagnostico;

public interface DiagnosticoRepositorio extends JpaRepository<Diagnostico,Integer> {
}
