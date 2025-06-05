package rmp.expediente_electronico.servicio;

import rmp.expediente_electronico.modelo.Paciente;

import java.util.List;

public interface IPacienteServicio {
    public List<Paciente> listarPacientes();

    public Paciente buscarPacientePorId(Integer idPaciente);

    public void guardarPaciente(Paciente paciente);

    public void eliminarPaciente(Paciente paciente);
}
