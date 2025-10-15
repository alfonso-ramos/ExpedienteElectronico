package rmp.expediente_electronico.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rmp.expediente_electronico.modelo.Diagnostico;
import rmp.expediente_electronico.repositorio.DiagnosticoRepositorio;

import java.util.List;

@Service
public class DiagnosticoServicio {

    @Autowired
    DiagnosticoRepositorio diagnosticoRepositorio;

    public List<Diagnostico> listarDiagnosticos(){
        return diagnosticoRepositorio.findAll();
    }

    public Diagnostico buscarPorId(Integer id){
        return diagnosticoRepositorio.findById(id).orElse(null);
    }
}
