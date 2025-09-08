/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package rmp.expediente_electronico.gui;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.ConsultaServicio;
import rmp.expediente_electronico.servicio.PacienteServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    @Autowired
    public VistaConsulta(ConsultaServicio consultaServicio, PacienteServicio pacienteServicio) {
        this.consultaServicio = consultaServicio;
        this.pacienteServicio = pacienteServicio;
        this.consulta = new Consulta();

        initComponents();
        listarPacientes();
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
        Float peso = (Float) pesoSpinner.getValue();
        Float altura = (Float) alturaSpinner.getValue();
        Float imc = ((Float) pesoSpinner.getValue()) / (((Float) alturaSpinner.getValue()) * ((Float) alturaSpinner.getValue()));
        String imcEstado = sacarImcEstado(imc);

        consulta.setEdad(edad);
        consulta.setAltura((Float) alturaSpinner.getValue());
        consulta.setPaciente(paciente);
        consulta.setImc(imc);
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
    }

    public void regresar(){
        this.setVisible(false);
        limpiarFormulario();
        vistaMain.listarConsultas();
        vistaMain.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        regresarButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        pacientesComboBox = new javax.swing.JComboBox<>();
        buscarField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        diagnosticoField = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        medicamentoField = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        observacionesField = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        tallaComboBox = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        alturaSpinner = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        pesoSpinner = new javax.swing.JSpinner();
        guardarButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("vista consulta");

        regresarButton.setText("Regresar");
        regresarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regresarButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Paciente");

        buscarField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                buscarFieldKeyTyped(evt);
            }
        });

        jLabel3.setText("Diagnostico");

        diagnosticoField.setColumns(20);
        diagnosticoField.setRows(5);
        jScrollPane1.setViewportView(diagnosticoField);

        jLabel4.setText("Medicamento");

        medicamentoField.setColumns(20);
        medicamentoField.setRows(5);
        jScrollPane2.setViewportView(medicamentoField);

        jLabel5.setText("Observaciones");

        observacionesField.setColumns(20);
        observacionesField.setRows(5);
        jScrollPane3.setViewportView(observacionesField);

        jLabel6.setText("Talla");

        tallaComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "S", "M", "L", "XL", "XXL" }));

        jLabel7.setText("Altura");

        alturaSpinner.setModel(new javax.swing.SpinnerNumberModel(1.7f, null, null, 0.01f));
        alturaSpinner.setToolTipText("");

        jLabel8.setText("Peso");

        pesoSpinner.setModel(new javax.swing.SpinnerNumberModel(50.0f, null, null, 1.0f));

        guardarButton.setText("Guardar");
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });

        jLabel9.setText("Buscar paciente");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(431, 431, 431)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(regresarButton)
                .addGap(18, 18, 18)
                .addComponent(guardarButton)
                .addGap(70, 70, 70))
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buscarField, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pacientesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(43, 43, 43)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)))
                            .addComponent(jLabel2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tallaComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel8))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(26, 26, 26)
                                        .addComponent(pesoSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(31, 31, 31)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(alturaSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(88, 88, 88))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(buscarField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addComponent(jLabel2))
                    .addComponent(pacientesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(guardarButton)
                            .addComponent(regresarButton))
                        .addGap(36, 36, 36))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addGap(18, 18, 18)
                                    .addComponent(alturaSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tallaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pesoSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(143, Short.MAX_VALUE))))
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner alturaSpinner;
    private javax.swing.JTextField buscarField;
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
    private javax.swing.JLabel jLabel9;
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
