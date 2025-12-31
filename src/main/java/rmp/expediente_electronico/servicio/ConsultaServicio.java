package rmp.expediente_electronico.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.repositorio.ConsultaRepositorio;

import java.util.Date;
import java.util.List;

@Service
public class ConsultaServicio {

    @Autowired
    private ConsultaRepositorio consultaRepositorio;

    public List<Consulta> listarConsultas(){
        List<Consulta> consultas = consultaRepositorio.findAll();
        return consultas;
    }

    public void guardarConsulta(Consulta consulta){
        consultaRepositorio.save(consulta);
    }

    public void eliminarConsulta(Consulta consulta){
        consultaRepositorio.delete(consulta);
    }

    public Consulta buscarPorId(Integer id){
        return consultaRepositorio.findById(id).orElse(null);
    }

    public List<Consulta> buscarConsultaPacientes(List<Paciente> pacientes){
        return consultaRepositorio.findByPacienteIn(pacientes);
    }

    public List<Consulta> buscarPorPaciente(Paciente paciente){
        return consultaRepositorio.findByPacienteOrderByFechaRegAsc(paciente);
    }

    public List<Consulta> buscarPorFecha(Date inicio, Date fin){
        return consultaRepositorio.findByFechaRegBetween(inicio,fin);
    }
}
