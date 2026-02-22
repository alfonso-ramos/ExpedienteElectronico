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
    private String[] programas;

    @Autowired
    public VistaPaciente(PacienteServicio pacienteServicio) {
        this.pacienteServicio = pacienteServicio;
        this.paciente = new Paciente();

        initComponents();

        setTitle("Gestion de Pacientes");
        setLocationRelativeTo(vistaMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        iniciarTabla();
    }

    public void setProgramas(String[] programas){
        this.programas = programas;
        iniciarProgramasAcademicos();
    }

    public void iniciarTabla(){

        this.tablaModelo = new DefaultTableModel(0, 7){
            @Override
            public boolean isCellEditable(int row,int column){return false;}
        };

        String[] nombresColumnas = {"Id","Matricula","Nombres","Apellidos","Sexo","Carrera","Fecha de nacimiento"};

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
                    paciente.getSexo(),
                    paciente.getProgramaAcademico(),
                    paciente.getFechaNacimiento(),
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
        var sexo = (String) sexoComboBox.getSelectedItem();
        java.sql.Date fechaNac = new java.sql.Date(fechaNacimiento.getDate().getTime());
        String programaAca = carrerasComboBox.getSelectedItem().toString();

        paciente.setNombres(nombre);
        paciente.setApellidos(apellido);
        paciente.setMatricula(matricula);
        paciente.setFechaNacimiento(fechaNac);
        paciente.setProgramaAcademico(programaAca);
        paciente.setSexo(sexo);

        var id = paciente.getIdPaciente();
        this.pacienteServicio.guardarPaciente(paciente);

        if(id == null){
            mostrarMensaje("Paciente agregado correctamente");
        } else{
            mostrarMensaje("Datos del paciente actualizados");
        }
        if(vistaMain != null){
            vistaMain.listarConsultas();
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
            sexoComboBox.setSelectedItem(paciente.getSexo());
        }
    }

    public void cargarPacientePorId(Integer idPaciente){
        if(idPaciente == null){
            return;
        }

        paciente = pacienteServicio.buscarPacientePorId(idPaciente);

        if(paciente == null){
            mostrarMensaje("No se encontró el paciente seleccionado");
            return;
        }

        matriculaTexto.setText(paciente.getMatricula());
        nombresTexto.setText(paciente.getNombres());
        apellidosTexto.setText(paciente.getApellidos());
        carrerasComboBox.setSelectedItem(paciente.getProgramaAcademico());
        fechaNacimiento.setDate(paciente.getFechaNacimiento());
        sexoComboBox.setSelectedItem(paciente.getSexo());
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
        DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<>(programas);
        carrerasComboBox.setModel(modelo);
    }

    private void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(this, mensaje);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        regresarButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        matriculaTexto = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        fechaNacimiento = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        nombresTexto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        apellidosTexto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        carrerasComboBox = new javax.swing.JComboBox<>();
        guardarButton = new javax.swing.JButton();
        limpiarButton = new javax.swing.JButton();
        modificarButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        buscarPacienteField = new javax.swing.JTextField();
        sexoComboBox = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        regresarButton.setBackground(new java.awt.Color(255, 51, 51));
        regresarButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        regresarButton.setForeground(new java.awt.Color(255, 255, 255));
        regresarButton.setText("Regresar");
        regresarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regresarButtonActionPerformed(evt);
            }
        });
        bg.add(regresarButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 43, 120, 40));

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel2.setText("Matrícula");
        bg.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, -1, -1));
        bg.add(matriculaTexto, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, 250, 30));

        jLabel6.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel6.setText("Fecha de nacimiento");
        bg.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 220, -1, -1));
        bg.add(fechaNacimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 250, 280, 30));

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel3.setText("Nombres");
        bg.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 300, -1, -1));
        bg.add(nombresTexto, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 330, 491, 33));

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel4.setText("Apellidos");
        bg.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 390, -1, -1));
        bg.add(apellidosTexto, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 420, 491, 35));

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel5.setText("Programa académico");
        bg.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 490, -1, -1));

        carrerasComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                carrerasComboBoxActionPerformed(evt);
            }
        });
        bg.add(carrerasComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 520, 490, 34));

        guardarButton.setBackground(new java.awt.Color(26, 188, 156));
        guardarButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        guardarButton.setForeground(new java.awt.Color(255, 255, 255));
        guardarButton.setText("Guardar");
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });
        bg.add(guardarButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 640, 120, 40));

        limpiarButton.setBackground(new java.awt.Color(178, 247, 233));
        limpiarButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        limpiarButton.setText("limpiar");
        limpiarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarButtonActionPerformed(evt);
            }
        });
        bg.add(limpiarButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 640, 110, 40));

        modificarButton.setBackground(new java.awt.Color(95, 192, 227));
        modificarButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        modificarButton.setText("Modificar");
        modificarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarButtonActionPerformed(evt);
            }
        });
        bg.add(modificarButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 640, 130, 40));

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

        bg.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(627, 124, 810, 550));

        buscarPacienteField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                buscarPacienteFieldKeyTyped(evt);
            }
        });
        bg.add(buscarPacienteField, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 62, 340, 40));

        sexoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hombre", "Mujer" }));
        sexoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sexoComboBoxActionPerformed(evt);
            }
        });
        bg.add(sexoComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 250, 180, 30));

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel1.setText("Sexo");
        bg.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 220, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 1502, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 856, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void regresarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regresarButtonActionPerformed
        this.setVisible(false);
        if(vistaMain != null){
            vistaMain.listarConsultas();
            vistaMain.setVisible(true);
        }
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

    private void carrerasComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_carrerasComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_carrerasComboBoxActionPerformed

    private void sexoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sexoComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sexoComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField apellidosTexto;
    private javax.swing.JPanel bg;
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
    private javax.swing.JComboBox<String> sexoComboBox;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
