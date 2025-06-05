package rmp.expediente_electronico.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.servicio.IPacienteServicio;

@Component
public class ExpElec_PacientesForma extends JFrame{
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
    public ExpElec_PacientesForma(IPacienteServicio pacienteServicio){
        this.pacienteServicio = pacienteServicio;
        iniciarForma();
    }

    private void iniciarForma(){
        setTitle("Gestion de Pacientes");
        setContentPane(panelPrincipal);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createUIComponents() {
        this.tablaModeloPacientes = new DefaultTableModel(0, 5);
        String[] nombresColumnas = {"Matricula", "Nombre", "Apellido", "Carrera", "Fecha de Nacimiento"};
        this.tablaModeloPacientes.setColumnIdentifiers(nombresColumnas);
        this.PacientesTabla = new JTable(this.tablaModeloPacientes);
    }
}
