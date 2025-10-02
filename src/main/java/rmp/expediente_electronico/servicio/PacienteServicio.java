package rmp.expediente_electronico.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.repositorio.PacienteRepositorio;

import java.util.List;

@Service
public class PacienteServicio{

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    public List<Paciente> listarPacientes() {
        List<Paciente> pacientes = pacienteRepositorio.findAll();
        return pacientes;
    }

    public Paciente buscarPacientePorId(Integer idPaciente) {
        return pacienteRepositorio.findById(idPaciente).orElse(null);
    }

    public void guardarPaciente(Paciente paciente) {
        pacienteRepositorio.save(paciente); 
    }

    public void eliminarPaciente(Paciente paciente) {
        pacienteRepositorio.delete(paciente);

    }

    public List<Paciente> buscarPacientes(String busqueda){
        return pacienteRepositorio.findByNombresContainingIgnoreCaseOrMatriculaContainingIgnoreCaseOrApellidosContainingIgnoreCase(busqueda,busqueda,busqueda);
    }
}
