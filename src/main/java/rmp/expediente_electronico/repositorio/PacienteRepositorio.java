package rmp.expediente_electronico.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import rmp.expediente_electronico.modelo.Paciente;

public interface PacienteRepositorio extends JpaRepository<Paciente, Integer> {
}
