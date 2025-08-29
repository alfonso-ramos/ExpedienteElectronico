package rmp.expediente_electronico.gui;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.PacienteServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

@Component
public class VistaPaciente extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaPaciente.class.getName());

    private PacienteServicio pacienteServicio;
    @Setter
    private VistaMain vistaMain;
    private DefaultTableModel tablaModelo;
    private Paciente paciente;

    @Autowired
    public VistaPaciente(PacienteServicio pacienteServicio) {
        this.pacienteServicio = pacienteServicio;
        this.paciente = new Paciente();

        initComponents();

        iniciarProgramasAcademicos();
        setTitle("Gestion de Pacientes");
        setLocationRelativeTo(vistaMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        iniciarTabla();
    }

    public void iniciarTabla(){

        this.tablaModelo = new DefaultTableModel(0, 6){
            @Override
            public boolean isCellEditable(int row,int column){return false;}
        };

        String[] nombresColumnas = {"Id","Matricula","Nombres","Apellidos","Carrera","Fecha de nacimiento"};

        this.tablaModelo.setColumnIdentifiers(nombresColumnas);
        this.tabla.setModel(tablaModelo);
        this.tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listarPacientes();
    }

    private void listarPacientes(){
        var pacientes = this.pacienteServicio.listarPacientes();

        listar(pacientes);
    }

    public void listar(List<Paciente> pacientes){
        this.tablaModelo.setRowCount(0);

        pacientes.forEach( paciente -> {
            Object[] renglonPaciente = {
                    paciente.getIdPaciente(),
                    paciente.getMatricula(),
                    paciente.getNombres(),
                    paciente.getApellidos(),
                    paciente.getProgramaAcademico(),
                    paciente.getFechaNacimiento()
            };
            this.tablaModelo.addRow(renglonPaciente);
        });
    }

    public void guardarPaciente(){
        if (matriculaTexto.getText().equals("")){
            mostrarMensaje("La matricula es obligatoria");
            matriculaTexto.requestFocusInWindow();
            return;
        }
        if(nombresTexto.getText().equals("")){
            mostrarMensaje("El nombre es requerido");
            nombresTexto.requestFocusInWindow();
            return;
        }
        if(apellidosTexto.getText().equals("")){
            mostrarMensaje("El apellido es requerido");
            apellidosTexto.requestFocusInWindow();
            return;
        }
        if(fechaNacimiento.getDate().equals("")){
            mostrarMensaje("La fecha de nacimiento es requerida");
            fechaNacimiento.requestFocusInWindow();
            return;
        }

        var matricula = matriculaTexto.getText();
        var nombre = nombresTexto.getText();
        var apellido = apellidosTexto.getText();
        java.sql.Date fechaNac = new java.sql.Date(fechaNacimiento.getDate().getTime());
        String programaAca = carrerasComboBox.getSelectedItem().toString();

        paciente.setNombres(nombre);
        paciente.setApellidos(apellido);
        paciente.setMatricula(matricula);
        paciente.setFechaNacimiento(fechaNac);
        paciente.setProgramaAcademico(programaAca);

        var id = paciente.getIdPaciente();
        this.pacienteServicio.guardarPaciente(paciente);

        if(id == null){
            mostrarMensaje("Paciente agregado correctamente");
        } else{
            mostrarMensaje("Datos del paciente actualizados");
        }
        limpiarFormulario();
        listarPacientes();
    }

    public void buscarPaciente(){
        String buscar = buscarPacienteField.getText();
        if(buscar.equals("")){
            listarPacientes();
        }else{
            var pacientes = this.pacienteServicio.buscarPacientes(buscar);
            listar(pacientes);
        }
    }

    public void cargarPaciente(){
        var renglon = tabla.getSelectedRow();

        if(renglon != -1){
            var idPaciente = (Integer) tabla.getModel().getValueAt(renglon,0);
            paciente = pacienteServicio.buscarPacientePorId(idPaciente);

            matriculaTexto.setText(paciente.getMatricula());
            nombresTexto.setText(paciente.getNombres());
            apellidosTexto.setText(paciente.getApellidos());
            carrerasComboBox.setSelectedItem(paciente.getProgramaAcademico());
            fechaNacimiento.setDate(paciente.getFechaNacimiento());
        }
    }
//
//    public void cerrarEdicion(){
//        vistaEdicion.setVisible(false);
//        this.setVisible(true);
//        listarPacientes();
//    }

    public void limpiarFormulario(){
        nombresTexto.setText("");
        matriculaTexto.setText("");
        apellidosTexto.setText("");
        fechaNacimiento.setDate(null);
        buscarPacienteField.setText("");
        paciente = new Paciente();
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
        carrerasComboBox.setModel(modelo);
    }

    private void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(this, mensaje);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        regresarButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        matriculaTexto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        nombresTexto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        apellidosTexto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fechaNacimiento = new com.toedter.calendar.JDateChooser();
        carrerasComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        guardarButton = new javax.swing.JButton();
        limpiarButton = new javax.swing.JButton();
        modificarButton = new javax.swing.JButton();
        buscarPacienteField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("vista paciente");

        regresarButton.setText("Regresar");
        regresarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regresarButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("matricula");

        jLabel3.setText("nombres");

        jLabel4.setText("apellidos");

        jLabel5.setText("carrera");

        jLabel6.setText("fecha de nacimiento");

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabla);

        guardarButton.setText("Guardar");
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });

        limpiarButton.setText("limpiar");
        limpiarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarButtonActionPerformed(evt);
            }
        });

        modificarButton.setText("Modificar");
        modificarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarButtonActionPerformed(evt);
            }
        });

        buscarPacienteField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                buscarPacienteFieldKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(420, 420, 420)
                        .addComponent(jLabel1)
                        .addGap(76, 76, 76)
                        .addComponent(buscarPacienteField, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(regresarButton))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addGap(72, 72, 72))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(matriculaTexto)
                                                .addGap(50, 50, 50)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(fechaNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(guardarButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(limpiarButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(modificarButton))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(carrerasComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 75, Short.MAX_VALUE)
                                        .addComponent(apellidosTexto, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(nombresTexto, javax.swing.GroupLayout.Alignment.LEADING)))))
                        .addGap(86, 86, 86)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel1)
                        .addGap(36, 36, 36))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(buscarPacienteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel6))
                                .addGap(18, 18, 18)
                                .addComponent(matriculaTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(fechaNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(23, 23, 23)
                        .addComponent(nombresTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(apellidosTexto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(carrerasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(guardarButton)
                            .addComponent(limpiarButton)
                            .addComponent(modificarButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(regresarButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 17, Short.MAX_VALUE)))
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void regresarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regresarButtonActionPerformed
        this.setVisible(false);
        vistaMain.setVisible(true);
    }//GEN-LAST:event_regresarButtonActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        guardarPaciente();
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void limpiarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarButtonActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_limpiarButtonActionPerformed

    private void modificarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarButtonActionPerformed
        if(paciente.getIdPaciente() == null){
            mostrarMensaje("Seleccione un paciente para modificar");
            return;
        }
        guardarPaciente();
    }//GEN-LAST:event_modificarButtonActionPerformed

    private void buscarPacienteFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscarPacienteFieldKeyTyped
        buscarPaciente();
    }//GEN-LAST:event_buscarPacienteFieldKeyTyped

    private void tablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMouseClicked
        cargarPaciente();
    }//GEN-LAST:event_tablaMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField apellidosTexto;
    private javax.swing.JTextField buscarPacienteField;
    private javax.swing.JComboBox<String> carrerasComboBox;
    private com.toedter.calendar.JDateChooser fechaNacimiento;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton limpiarButton;
    private javax.swing.JTextField matriculaTexto;
    private javax.swing.JButton modificarButton;
    private javax.swing.JTextField nombresTexto;
    private javax.swing.JButton regresarButton;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
