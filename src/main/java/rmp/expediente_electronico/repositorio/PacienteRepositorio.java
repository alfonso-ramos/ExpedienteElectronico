package rmp.expediente_electronico.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import rmp.expediente_electronico.modelo.Paciente;

import java.util.List;

public interface PacienteRepositorio extends JpaRepository<Paciente, Integer> {

    List<Paciente> findByNombresContainingIgnoreCaseOrMatriculaContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String matricula,String apellidos);
}
