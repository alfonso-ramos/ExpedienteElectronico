/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package rmp.expediente_electronico.gui;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.modelo.Diagnostico;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.ConsultaServicio;
import rmp.expediente_electronico.servicio.DiagnosticoServicio;
import rmp.expediente_electronico.servicio.PacienteServicio;

import javax.swing.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

@Component
public class VistaConsulta extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaConsulta.class.getName());
    @Setter
    private VistaMain vistaMain;

    private ConsultaServicio consultaServicio;
    private Consulta consulta;
    private PacienteServicio pacienteServicio;
    private DiagnosticoServicio diagnosticoServicio;

    @Autowired
    public VistaConsulta(ConsultaServicio consultaServicio, PacienteServicio pacienteServicio, DiagnosticoServicio diagnosticoServicio) {
        this.consultaServicio = consultaServicio;
        this.pacienteServicio = pacienteServicio;
        this.diagnosticoServicio = diagnosticoServicio;
        this.consulta = new Consulta();

        initComponents();
        listarPacientes();
        listarDiagnosticos();
    }

    public void listarDiagnosticos(){
        List<Diagnostico> diagnosticos = diagnosticoServicio.listarDiagnosticos();

        diagnosticoComboBox.removeAllItems();
        diagnosticos.forEach(diagnostico -> {
            diagnosticoComboBox.addItem(diagnostico);
        });
    }


    public void listarPacientes(){
        List<Paciente> pacientes = pacienteServicio.listarPacientes();

        llenarPacientesBox(pacientes);
    }

    public void llenarPacientesBox(List<Paciente> pacientes){
        pacientesComboBox.removeAllItems();

        pacientes.forEach(paciente -> {
            pacientesComboBox.addItem(paciente);
        });
    }

    public void guardar(){
        if(pacientesComboBox.getSelectedItem() == null){
            mostrarMensaje("Seleccione un paciente");
        }

        if(diagnosticoComboBox.getSelectedItem() == null){
            mostrarMensaje("Seleccione una causa de diagnostico");
        }

        if(tallaComboBox.getSelectedItem() == null){
            mostrarMensaje("Seleccione una talla");
        }

        if(alturaSpinner.getValue() == null){
            mostrarMensaje("Ingrese la altura del paciente");
        }

        if(pesoSpinner.getValue() == null){
            mostrarMensaje("Ingrese el peso del paciente");
        }

        Paciente paciente = (Paciente) pacientesComboBox.getSelectedItem();
        Integer edad = calcularEdad(paciente.getFechaNacimiento());
        Float imc = ((Float) pesoSpinner.getValue()) / (((Float) alturaSpinner.getValue()) * ((Float) alturaSpinner.getValue()));
        String imcEstado = sacarImcEstado(imc);

        consulta.setEdad(edad);
        consulta.setAltura((Float) alturaSpinner.getValue());
        consulta.setPaciente(paciente);
        consulta.setImc(imc);
        consulta.setDiagnosticoKey((Diagnostico) diagnosticoComboBox.getSelectedItem());
        consulta.setDiagnostico(diagnosticoField.getText());
        consulta.setFechaReg(java.sql.Date.valueOf(LocalDate.now()));
        consulta.setMedicamento(medicamentoField.getText());
        consulta.setImc_estado(imcEstado);
        consulta.setObservaciones(observacionesField.getText());
        consulta.setPeso((Float) pesoSpinner.getValue());
        consulta.setTalla(tallaComboBox.getSelectedItem().toString());

        Integer id = consulta.getIdConsulta();

        consultaServicio.guardarConsulta(consulta);

        if(id == null){
            mostrarMensaje("Consulta agregada con exito");
        }else{
           mostrarMensaje("Consulta Modificada");
        }

        regresar();
    }

    public void limpiarFormulario(){
        buscarField.setText("");
        diagnosticoField.setText("");
        observacionesField.setText("");
        medicamentoField.setText("");
        consulta = new Consulta();
        listarPacientes();
    }

    public String sacarImcEstado(Float imc){
        if(imc < 18.5) return "Inferior al normal";
        if(imc < 25) return "Normal";
        if(imc < 30) return "Superior al normal";
        return "Sobrepeso";
    }

    public void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(this,mensaje);
    }

    public int calcularEdad(java.util.Date fechaNacimiento) {
        if (fechaNacimiento == null) {
            return 0;
        }

        LocalDate nacimiento;

        if (fechaNacimiento instanceof java.sql.Date) {
            nacimiento = ((java.sql.Date) fechaNacimiento).toLocalDate();
        } else {
            nacimiento = fechaNacimiento.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        return Period.between(nacimiento, LocalDate.now()).getYears();
    }

    public void buscarPaciente(){
        if(buscarField.getText() == ""){
            listarPacientes();
            return;
        }

        var pacientes = pacienteServicio.buscarPacientes(buscarField.getText());
        llenarPacientesBox(pacientes);
    }

    public void modificarTextoGuardar(String texto){
        guardarButton.setText(texto);
    }

    public void rellenar(Consulta consulta){
        this.consulta = consulta;

        pacientesComboBox.setSelectedItem(consulta.getPaciente());
        diagnosticoField.setText(consulta.getDiagnostico());
        observacionesField.setText(consulta.getObservaciones());
        medicamentoField.setText(consulta.getMedicamento());
        tallaComboBox.setSelectedItem(consulta.getTalla());
        alturaSpinner.setValue(consulta.getAltura());
        pesoSpinner.setValue(consulta.getPeso());
        diagnosticoComboBox.setSelectedItem(consulta.getDiagnosticoKey());
    }

    public void regresar(){
        this.setVisible(false);
        limpiarFormulario();
        vistaMain.listarPacientes();
        vistaMain.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        diagnosticoField = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        medicamentoField = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        observacionesField = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        tallaComboBox = new javax.swing.JComboBox<>();
        alturaSpinner = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        pesoSpinner = new javax.swing.JSpinner();
        guardarButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        diagnosticoComboBox = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        buscarField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pacientesComboBox = new javax.swing.JComboBox<>();
        regresarButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        diagnosticoField.setColumns(20);
        diagnosticoField.setRows(5);
        jScrollPane1.setViewportView(diagnosticoField);

        bg.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 170, 310, 100));

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel3.setText("Diagnostico");
        bg.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 130, 210, 40));

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel4.setText("Medicamento");
        bg.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 450, 210, 30));

        medicamentoField.setColumns(20);
        medicamentoField.setRows(5);
        jScrollPane2.setViewportView(medicamentoField);

        bg.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 490, 310, 100));

        observacionesField.setColumns(20);
        observacionesField.setRows(5);
        jScrollPane3.setViewportView(observacionesField);

        bg.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 320, 310, 100));

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel5.setText("Observaciones");
        bg.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 280, 200, 30));

        tallaComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "M", "L", "XL", "XXL" }));
        bg.add(tallaComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 180, 240, 50));

        alturaSpinner.setModel(new javax.swing.SpinnerNumberModel(1.7f, null, null, 0.01f));
        alturaSpinner.setToolTipText("");
        bg.add(alturaSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 300, 240, 50));

        jLabel6.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel6.setText("Talla");
        bg.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 150, 210, 30));

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel7.setText("Altura");
        bg.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 270, 150, 30));

        pesoSpinner.setModel(new javax.swing.SpinnerNumberModel(50.0f, null, null, 1.0f));
        bg.add(pesoSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 400, 240, 50));

        guardarButton.setBackground(new java.awt.Color(26, 188, 156));
        guardarButton.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        guardarButton.setForeground(new java.awt.Color(255, 255, 255));
        guardarButton.setText("Guardar");
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });
        bg.add(guardarButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 660, 130, 50));

        jLabel8.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel8.setText("Peso");
        bg.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 370, 160, 30));

        bg.add(diagnosticoComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 510, 360, 50));

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel1.setText("Causa de diagnostico");
        bg.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 470, 330, 40));

        jPanel1.setBackground(new java.awt.Color(56, 89, 152));

        buscarField.setText("Buscar paciente");
        buscarField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarFieldActionPerformed(evt);
            }
        });
        buscarField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                buscarFieldKeyTyped(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Paciente");

        regresarButton.setBackground(new java.awt.Color(255, 51, 51));
        regresarButton.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        regresarButton.setForeground(new java.awt.Color(255, 255, 255));
        regresarButton.setText("Regresar");
        regresarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regresarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(pacientesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buscarField, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regresarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(regresarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(buscarField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pacientesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 1187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 847, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void regresarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regresarButtonActionPerformed
        regresar();
    }//GEN-LAST:event_regresarButtonActionPerformed

    private void buscarFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscarFieldKeyTyped
        buscarPaciente();
    }//GEN-LAST:event_buscarFieldKeyTyped

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        guardar();
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void buscarFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner alturaSpinner;
    private javax.swing.JPanel bg;
    private javax.swing.JTextField buscarField;
    private javax.swing.JComboBox<Diagnostico> diagnosticoComboBox;
    private javax.swing.JTextArea diagnosticoField;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea medicamentoField;
    private javax.swing.JTextArea observacionesField;
    private javax.swing.JComboBox<Paciente> pacientesComboBox;
    private javax.swing.JSpinner pesoSpinner;
    private javax.swing.JButton regresarButton;
    private javax.swing.JComboBox<String> tallaComboBox;
    // End of variables declaration//GEN-END:variables
}
