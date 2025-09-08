package rmp.expediente_electronico.gui;

import com.toedter.calendar.JDateChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.IPacienteServicio;
import rmp.expediente_electronico.servicio.PacienteServicio;

import javax.swing.*;
import java.util.Date;

@Component
public class ExpElec_PacienteEdicion extends JFrame {
    private JPanel PanelPrincipal;
    private JButton regresarButton;
    private JButton editarButton;
    private JButton eliminarButton;
    private JTextField matriculaTexto;
    private JTextField nombresTexto;
    private JTextField apellidosTexto;
    private JDateChooser fechaNac;
    private JComboBox<String> carreraComboBox;
    private IPacienteServicio pacienteServicio;
    private Paciente paciente;
    private ExpElec_PacientesForma vistaPacientes;

    @Autowired
    public ExpElec_PacienteEdicion(PacienteServicio pacienteServicio){
        this.pacienteServicio = pacienteServicio;
        createUIComponents();  // Asegurarnos de que los componentes están inicializados
        iniciarForma();
        regresarButton.addActionListener(actionEvent -> vistaPacientes.cerrarEdicion());
        editarButton.addActionListener(actionEvent -> actualizarPaciente());
        eliminarButton.addActionListener(actionEvent -> eliminarPaciente());
    }

    public void setVistaPacientes(ExpElec_PacientesForma pacientesForma){
        this.vistaPacientes = pacientesForma;
    }

    public void setPaciente(Paciente paciente){
        this.paciente = paciente;
    }

    public void iniciarForma(){
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

    private void createUIComponents() {
        PanelPrincipal = new JPanel();
        carreraComboBox = new JComboBox<>();
        String[] programas = {
                "Ingeniería en Tecnologías de la Información e Innovación digital",
                "Ingeniería en BiotecnologÍa",
                "Ingeniería Mecatrónica",
                "Ingeniería en Energía y Desarrollo Sostenible",
                "Ingeniería ambiental y sustentabilidad",
                "Ingeniería en Logística",
                "Ingeniería Biomédica",
                "Ingeniería en animación y efectos visuales",
                "Ingeniería en Nanotecnología",
                "Licenciatura en Terapia Física",
                "Licenciatura en administración"
        };
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>(programas);
        carreraComboBox.setModel(modelo);
    }

    public void iniciarProgramasAcademicos(){
        // Método ahora vacío, la inicialización se hace en createUIComponents
    }

    public void actualizarPaciente(){
        // Validación simple
        paciente.setMatricula(matriculaTexto.getText());
        paciente.setNombres(nombresTexto.getText());
        paciente.setApellidos(apellidosTexto.getText());
        paciente.setProgramaAcademico(carreraComboBox.getSelectedItem().toString());
        paciente.setFechaNacimiento(new Date(fechaNac.getDate().getTime()));
        pacienteServicio.guardarPaciente(paciente);
        vistaPacientes.cerrarEdicion();
    }

    public void eliminarPaciente(){
        pacienteServicio.eliminarPaciente(paciente);
        vistaPacientes.cerrarEdicion();
    }
}
