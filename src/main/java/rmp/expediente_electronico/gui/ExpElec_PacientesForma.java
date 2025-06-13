package rmp.expediente_electronico.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.IPacienteServicio;
import rmp.expediente_electronico.servicio.PacienteServicio;

import java.awt.event.*;
import java.sql.Date;
import java.util.List;

@Component
public class    ExpElec_PacientesForma extends JFrame{
    private JTextField MatriculaTexto;
    private JTable pacientesTabla;
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
    private Integer idPaciente;
    private ExpElec_PacienteEdicion vistaEdicion;


    @Autowired
    public ExpElec_PacientesForma(PacienteServicio pacienteServicio) {
        this.pacienteServicio = pacienteServicio;
        this.vistaEdicion = new ExpElec_PacienteEdicion(pacienteServicio,this);
        iniciarForma();
        guardarButton.addActionListener(actionEvent -> guardarPaciente());
        limpiarButton.addActionListener(actionEvent -> limpiarFormulario());

        pacientesTabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                cargarPacienteSeleccionado();
            }
        });
        buscarPacienteTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                buscarPaciente();
            }
        });
        pacientesTabla.addMouseListener(new MouseAdapter() {
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

        String[] nombresColumnas = {"ID", "Matricula", "Nombre", "Apellido", "Carrera","Fecha nacimiento"};
        this.tablaModeloPacientes.setColumnIdentifiers(nombresColumnas);
        this.pacientesTabla = new JTable(tablaModeloPacientes);
        this.pacientesTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
        if (MatriculaTexto.getText().equals("")){
            mostrarMensaje("La matricula es obligatoria");
            MatriculaTexto.requestFocusInWindow();
            return;
        }
        if(NombreTexto.getText().equals("")){
            mostrarMensaje("El nombre es requerido");
            NombreTexto.requestFocusInWindow();
            return;
        }
        if(ApellidoTexto.getText().equals("")){
            mostrarMensaje("El apellido es requerido");
            ApellidoTexto.requestFocusInWindow();
            return;
        }
        if(FechaNacimiento.getDate().equals("")){
            mostrarMensaje("La fecha de nacimiento es requerida");
            FechaNacimiento.requestFocusInWindow();
            return;
        }

        var matricula = MatriculaTexto.getText();
        var nombre = NombreTexto.getText();
        var apellido = ApellidoTexto.getText();
        java.sql.Date fechaNac = new java.sql.Date(FechaNacimiento.getDate().getTime());
        String programaAca = carreraComboBox.getSelectedItem().toString();
        var paciente = new Paciente(this.idPaciente, matricula, nombre, apellido, programaAca, fechaNac);
        this.pacienteServicio.guardarPaciente(paciente);

        if(this.idPaciente == null){
            mostrarMensaje("Paciente agregado correctamente");
        } else{
            mostrarMensaje("Datos del paciente actualizados");
        }
        limpiarFormulario();
        listarPacientes();
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
        var renglon = pacientesTabla.getSelectedRow();
        Integer idPaciente = (Integer) pacientesTabla.getModel().getValueAt(renglon,0);
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
        listarPacientes();
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

    private void cargarPacienteSeleccionado(){
        var renglon = pacientesTabla.getSelectedRow();
        if(renglon != -1){
            var id = pacientesTabla.getModel().getValueAt(renglon, 0).toString();
            this.idPaciente = Integer.parseInt(id);
            var matricula = pacientesTabla.getModel().getValueAt(renglon, 1).toString();
            this.MatriculaTexto.setText(matricula);
            var nombre = pacientesTabla.getModel().getValueAt(renglon, 2).toString();
            this.NombreTexto.setText(nombre);
            var apellido = pacientesTabla.getModel().getValueAt(renglon, 3).toString();
            this.ApellidoTexto.setText(apellido);
            var carrera = pacientesTabla.getModel().getValueAt(renglon, 4).toString();
            this.carreraComboBox.setSelectedItem(carrera);
            var fechaNac = pacientesTabla.getModel().getValueAt(renglon, 5);
            this.FechaNacimiento.setDate((Date) fechaNac);
        }
    }

    private void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
