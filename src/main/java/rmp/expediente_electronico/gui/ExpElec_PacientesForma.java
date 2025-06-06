package rmp.expediente_electronico.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.IPacienteServicio;
import rmp.expediente_electronico.servicio.PacienteServicio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;

@Component
public class    ExpElec_PacientesForma extends JFrame{
    private JTextField MatriculaTexto;
    private JTable PacientesTabla;
    private JTextField NombreTexto;
    private JTextField ApellidoTexto;
    private JComboBox carreraComboBox;
    private JFormattedTextField FechaNacimiento;
    private JButton guardarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JTextField buscarPacienteTextField;
    private JPanel panelPrincipal;
    IPacienteServicio pacienteServicio;
    private DefaultTableModel tablaModeloPacientes;


    @Autowired
    public ExpElec_PacientesForma(PacienteServicio pacienteServicio) {
        this.pacienteServicio = pacienteServicio;
        iniciarForma();
        guardarButton.addActionListener(actionEvent -> guardarPaciente());
    }

    private void iniciarForma(){
        iniciarProgramasAcademicos();
        setTitle("Gestion de Pacientes");
        setContentPane(panelPrincipal);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createUIComponents() {
        this.tablaModeloPacientes = new DefaultTableModel(0, 4);
        String[] nombresColumnas = {"Matricula", "Nombre", "Apellido", "Carrera"};
        this.tablaModeloPacientes.setColumnIdentifiers(nombresColumnas);
        this.PacientesTabla = new JTable(tablaModeloPacientes);
        this.PacientesTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Cargar listado de pacientes
        listarPacientes();
    }

    private void listarPacientes(){
        this.tablaModeloPacientes.setRowCount(0);
        var pacientes = this.pacienteServicio.listarPacientes();

        pacientes.forEach( paciente -> {
            Object[] renglonPaciente = {
                    paciente.getMatricula(),
                    paciente.getNombres(),
                    paciente.getApellidos(),
                    paciente.getProgramaAcademico()
            };
            this.tablaModeloPacientes.addRow(renglonPaciente);
        });
    }

    public void guardarPaciente(){
        String matricula = MatriculaTexto.getText();
        String nombres = NombreTexto.getText();
        String apellidos = ApellidoTexto.getText();
        String fechaNac = FechaNacimiento.getText();
        String programaAca = carreraComboBox.getSelectedItem().toString();

        //aqui irian las validaciones

        if(true){
            Paciente paciente = new Paciente(null,matricula,nombres,apellidos,programaAca,fechaNac);
            pacienteServicio.guardarPaciente(paciente);
            listarPacientes();
        }else{
            //aca si falla
            System.out.println("hola");
        }
    }

    public void iniciarProgramasAcademicos(){
        String[] programas = {"Tecnologías", "Mecatrónica"};
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>(programas);
        carreraComboBox.setModel(modelo);
    }
}
