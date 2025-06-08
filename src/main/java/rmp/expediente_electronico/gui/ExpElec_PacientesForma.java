package rmp.expediente_electronico.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.IPacienteServicio;
import rmp.expediente_electronico.servicio.PacienteServicio;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.List;

@Component
public class    ExpElec_PacientesForma extends JFrame{
    private JTextField MatriculaTexto;
    private JTable PacientesTabla;
    private JTextField NombreTexto;
    private JTextField ApellidoTexto;
    private JComboBox carreraComboBox;
    private JDateChooser FechaNacimiento;
    private JButton guardarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JTextField buscarPacienteTextField;
    private JPanel panelPrincipal;
    IPacienteServicio pacienteServicio;
    private DefaultTableModel tablaModeloPacientes;
    private ExpElec_PacienteEdicion vistaEdicion;


    @Autowired
    public ExpElec_PacientesForma(PacienteServicio pacienteServicio) {
        this.pacienteServicio = pacienteServicio;
        this.vistaEdicion = new ExpElec_PacienteEdicion(pacienteServicio,this);
        iniciarForma();
        guardarButton.addActionListener(actionEvent -> guardarPaciente());
        limpiarButton.addActionListener(actionEvent -> limpiarFormulario());

        buscarPacienteTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                buscarPaciente();
            }
        });
        PacientesTabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    editarPaciente();
                }
            }
        });
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
        // evitar la edicion de tablas
        this.tablaModeloPacientes = new DefaultTableModel(0, 6){
            @Override
            public boolean isCellEditable(int row,int column){return false;}
        };

        String[] nombresColumnas = {"Id","Matricula", "Nombre", "Apellido", "Carrera","Fecha nacimiento"};
        this.tablaModeloPacientes.setColumnIdentifiers(nombresColumnas);

        this.PacientesTabla = new JTable(tablaModeloPacientes);

        this.PacientesTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        //Cargar listado de pacientes
        listarPacientes();
    }

    private void listarPacientes(){
        this.tablaModeloPacientes.setRowCount(0);
        var pacientes = this.pacienteServicio.listarPacientes();

        listar(pacientes);
    }

    public void listar(List<Paciente> pacientes){
        pacientes.forEach( paciente -> {
            Object[] renglonPaciente = {
                    paciente.getIdPaciente(),
                    paciente.getMatricula(),
                    paciente.getNombres(),
                    paciente.getApellidos(),
                    paciente.getProgramaAcademico(),
                    paciente.getFechaNacimiento()
            };
            this.tablaModeloPacientes.addRow(renglonPaciente);
        });
    }

    public void guardarPaciente(){
        String matricula = MatriculaTexto.getText();
        String nombres = NombreTexto.getText();
        String apellidos = ApellidoTexto.getText();
        java.sql.Date fechaNac = new java.sql.Date(FechaNacimiento.getDate().getTime());
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

    public void buscarPaciente(){
        String buscar = buscarPacienteTextField.getText();
        if(buscar.equals("")){
            listarPacientes();
        }else{
            this.tablaModeloPacientes.setRowCount(0);
            var pacientes = this.pacienteServicio.buscarPacientes(buscar);

            listar(pacientes);
        }
    }

    public void editarPaciente(){
        var renglon = PacientesTabla.getSelectedRow();
        Integer idPaciente = (Integer) PacientesTabla.getModel().getValueAt(renglon,0);
        Paciente paciente = pacienteServicio.buscarPacientePorId(idPaciente);
        vistaEdicion.setPaciente(paciente);
        vistaEdicion.rellenarFormulario();
        vistaEdicion.setSize(this.getSize());
        this.setVisible(false);
        vistaEdicion.setVisible(true);
    }

    public void cerrarEdicion(){
        vistaEdicion.setVisible(false);
        this.setVisible(true);
    }

    public void limpiarFormulario(){
        NombreTexto.setText("");
        MatriculaTexto.setText("");
        ApellidoTexto.setText("");
        FechaNacimiento.setDate(null);
        buscarPacienteTextField.setText("");
        listarPacientes();
    }

    public void iniciarProgramasAcademicos(){
        String[] programas = {"Tecnologías", "Mecatrónica"};
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>(programas);
        carreraComboBox.setModel(modelo);
    }
}
