package rmp.expediente_electronico.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.modelo.Paciente;

import java.util.Date;
import java.util.List;

public interface ConsultaRepositorio extends JpaRepository <Consulta, Integer> {

    List<Consulta> findByPacienteIn(List<Paciente> pacientes);

    List<Consulta> findByFechaRegBetween(Date inicio, Date fin);
}
