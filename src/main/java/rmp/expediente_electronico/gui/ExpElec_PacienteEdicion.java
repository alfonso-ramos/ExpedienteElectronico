package rmp.expediente_electronico.gui;

import com.toedter.calendar.JDateChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.IPacienteServicio;
import rmp.expediente_electronico.servicio.PacienteServicio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class ExpElec_PacienteEdicion extends JFrame{
    private JPanel PanelPrincipal;
    private JButton regresarButton;
    private JButton editarButton;
    private JButton eliminarButton;
    private JTextField matriculaTexto;
    private JTextField nombresTexto;
    private JTextField apellidosTexto;
    private JDateChooser fechaNac;
    private JComboBox carreraComboBox;
    private IPacienteServicio pacienteServicio;
    private Paciente paciente;
    private ExpElec_PacientesForma vistaPacientes;

    @Autowired
    public ExpElec_PacienteEdicion(PacienteServicio pacienteServicio, ExpElec_PacientesForma vistaPacientes){
        this.pacienteServicio = pacienteServicio;
        this.paciente = paciente;
        this.vistaPacientes = vistaPacientes;
        iniciarForma();
        regresarButton.addActionListener(actionEvent -> vistaPacientes.cerrarEdicion());
    }

    public void setPaciente(Paciente paciente){
        this.paciente = paciente;
    }

    public void iniciarForma(){
        iniciarProgramasAcademicos();
        setTitle("Edicion de paciente");
        setContentPane(PanelPrincipal);
        setSize(800,600);
        setLocationRelativeTo(vistaPacientes);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void rellenarFormulario(){
        matriculaTexto.setText(paciente.getMatricula());
        nombresTexto.setText(paciente.getNombres());
        apellidosTexto.setText(paciente.getApellidos());
        carreraComboBox.setSelectedItem(paciente.getProgramaAcademico());
        fechaNac.setDate(paciente.getFechaNacimiento());
    }

    public void iniciarProgramasAcademicos(){
        String[] programas = {"Tecnologías", "Mecatrónica"};
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>(programas);
        carreraComboBox.setModel(modelo);
    }
}
