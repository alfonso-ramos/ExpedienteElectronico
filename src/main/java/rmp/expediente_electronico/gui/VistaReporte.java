/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package rmp.expediente_electronico.gui;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.PacienteServicio;
import rmp.expediente_electronico.servicio.ReporteServicio;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;

@Component
public class VistaReporte extends JFrame {

    private ReporteServicio reporteServicio;
    private PacienteServicio pacienteServicio;
    @Setter
    private VistaMain vistaMain;
    private String[] programas;

    // Altura Titulo Principal (ej: 40 puntos = 800 twips)
    final short ALTURA_TITULO_PRINCIPAL = 800;

    // Altura Subtítulos (ej: 30 puntos = 600 twips)
    final short ALTURA_SUBTITULO = 600;

    @Autowired
    public VistaReporte(ReporteServicio reporteServicio, PacienteServicio pacienteServicio){
        configurarIcono();
        this.reporteServicio = reporteServicio;
        this.pacienteServicio = pacienteServicio;

        initComponents();
        buscarPacientesInput.setText("");
        configurarBusquedaPacientes();
        listarPacientes();
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    public void configurarIcono(){
        try{
            Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/upsinIcon.png"))).getImage();
            this.setIconImage(icon);
        }catch (Exception e){
            System.err.println("No se pudo cargar el icono "+ e.getMessage());
        }
    }

    public void setProgramas(String[] programas){
        this.programas = programas;
        reporteServicio.setProgramas(programas);
    }

    public void generarReporteFecha(){
        var inicio = inicioReporte.getDate();
        var fin = finReporte.getDate();

        if (inicio == null) {
            mostrarMensaje("Seleccione una fecha de inicio para el reporte");
            return;
        }
        if (fin == null) {
            mostrarMensaje("Seleccione una fecha de fin para el reporte");
            return;
        }

        reporteServicio.generarReporteFecha(inicio,fin);
    }

    public void generarReporteMensual(){

        int mes = mesReporteMensualChooser.getMonth() + 1;
        int year = yearReporteMensualChooser.getYear();

        reporteServicio.generarReporteMensual(mes, year);
    }

    public void generarReporteAnual(){
        int year = yearReporteAnualChooser.getYear();
        reporteServicio.generarReporteAnual(year);
    }
    
    public void generarReportePacienteSeleccionado() {
        Paciente seleccionado = (Paciente) pacientesComboBox.getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("Seleccione un paciente para generar el reporte");
            return;
        }
        reporteServicio.generarReportePaciente(seleccionado);
    }

    public void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    private void configurarBusquedaPacientes() {
        buscarPacientesInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarPacientesPorTexto();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarPacientesPorTexto();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarPacientesPorTexto();
            }
        });
    }

    private void listarPacientes() {
        var pacientes = pacienteServicio.listarPacientes();
        llenarPacientesBox(pacientes);
    }

    private void buscarPacientesPorTexto() {
        String criterio = buscarPacientesInput.getText().trim();
        if (criterio.isEmpty()) {
            listarPacientes();
            return;
        }

        var pacientes = pacienteServicio.buscarPacientes(criterio);
        llenarPacientesBox(pacientes);
    }

    private void llenarPacientesBox(List<Paciente> pacientes) {
        DefaultComboBoxModel<Paciente> model = new DefaultComboBoxModel<>();
        for (Paciente paciente : pacientes) {
            model.addElement(paciente);
        }
        
        // Set the model to the combo box
        pacientesComboBox.setModel(model);
        
        // Set a custom renderer to display the patient information
        pacientesComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings({"rawtypes", "unchecked"})
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, 
                                                        boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Paciente) {
                    Paciente paciente = (Paciente) value;
                    setText(paciente.getMatricula() + " - " + 
                           paciente.getNombres() + " " + 
                           paciente.getApellidos());
                } else if (value == null) {
                    setText("Seleccione un paciente");
                }
                return this;
            }
        });
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        inicioReporte = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        finReporte = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        mesReporteMensualChooser = new com.toedter.calendar.JMonthChooser();
        generarAnualButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        yearReporteAnualChooser = new com.toedter.calendar.JYearChooser();
        jLabel7 = new javax.swing.JLabel();
        generarMensualButton = new javax.swing.JButton();
        generarPorPacienteButton = new javax.swing.JButton();
        yearReporteMensualChooser = new com.toedter.calendar.JYearChooser();
        generarPorFechaButton1 = new javax.swing.JButton();
        pacientesComboBox = new javax.swing.JComboBox<>();
        buscarPacientesInput = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        regresarButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        inicioReporte.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(inicioReporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 460, 250, 40));

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel3.setText("Fecha final");
        bg.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 510, -1, -1));

        finReporte.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(finReporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 550, 250, 40));

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel2.setText("Año");
        bg.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 410, -1, -1));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Generar reporte anual");
        bg.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 330, -1, 50));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel4.setText("Generar reporte por paciente");
        bg.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 620, -1, 50));

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel5.setText("Fecha de inicio");
        bg.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 410, -1, -1));

        mesReporteMensualChooser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(mesReporteMensualChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 450, 150, 50));

        generarAnualButton.setBackground(new java.awt.Color(26, 188, 156));
        generarAnualButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        generarAnualButton.setForeground(new java.awt.Color(255, 255, 255));
        generarAnualButton.setText("Generar reporte anual");
        generarAnualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarAnualButtonActionPerformed(evt);
            }
        });
        bg.add(generarAnualButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 510, 260, 60));

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel6.setText("Generar reporte mensual");
        bg.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 330, -1, 50));

        yearReporteAnualChooser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(yearReporteAnualChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 440, 140, 50));

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel7.setText("Mes");
        bg.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 400, -1, -1));

        generarMensualButton.setBackground(new java.awt.Color(26, 188, 156));
        generarMensualButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        generarMensualButton.setForeground(new java.awt.Color(255, 255, 255));
        generarMensualButton.setText("Generar reporte mensual");
        generarMensualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarMensualButtonActionPerformed(evt);
            }
        });
        bg.add(generarMensualButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 530, 250, 60));

        generarPorPacienteButton.setBackground(new java.awt.Color(26, 188, 156));
        generarPorPacienteButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        generarPorPacienteButton.setForeground(new java.awt.Color(255, 255, 255));
        generarPorPacienteButton.setText("Generar reporte del paciente");
        generarPorPacienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarPorPacienteButtonActionPerformed(evt);
            }
        });
        bg.add(generarPorPacienteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 720, 290, 60));

        yearReporteMensualChooser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(yearReporteMensualChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 450, 120, 50));

        generarPorFechaButton1.setBackground(new java.awt.Color(26, 188, 156));
        generarPorFechaButton1.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        generarPorFechaButton1.setForeground(new java.awt.Color(255, 255, 255));
        generarPorFechaButton1.setText("Generar reporte");
        generarPorFechaButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarPorFechaButton1ActionPerformed(evt);
            }
        });
        bg.add(generarPorFechaButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 490, 170, 70));

        bg.add(pacientesComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 760, 510, 50));

        buscarPacientesInput.setText("Buscar Paciente");
        bg.add(buscarPacientesInput, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 690, 510, 50));

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel8.setText("Generar reporte por fecha");
        bg.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 330, -1, 50));

        regresarButton.setBackground(new java.awt.Color(255, 0, 51));
        regresarButton.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        regresarButton.setForeground(new java.awt.Color(255, 255, 255));
        regresarButton.setText("Regresar");
        regresarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regresarButtonActionPerformed(evt);
            }
        });
        bg.add(regresarButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 210, 150, 50));

        jLabel9.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 102));
        jLabel9.setText("Generacion de Reportes");
        bg.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 130, -1, -1));

        jLabel10.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabel10.setText("Registro de Actividades Diarias en Consulta Externa");
        bg.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, -1, -1));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoUpsin.png"))); // NOI18N
        bg.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med(1).jpg"))); // NOI18N
        bg.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1410, 10, -1, -1));

        jLabel13.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabel13.setText("Expediente Electronico");
        bg.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 70, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, 1614, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 855, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void regresarButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_regresarButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_regresarButtonActionPerformed

    private void generarAnualButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarAnualButtonActionPerformed
        generarReporteAnual();
    }//GEN-LAST:event_generarAnualButtonActionPerformed

    private void generarMensualButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarMensualButtonActionPerformed
        generarReporteMensual();
    }//GEN-LAST:event_generarMensualButtonActionPerformed

    private void generarPorPacienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarPorPacienteButtonActionPerformed
        generarReportePacienteSeleccionado();
    }//GEN-LAST:event_generarPorPacienteButtonActionPerformed

    private void generarPorFechaButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarPorFechaButton1ActionPerformed
        generarReporteFecha();
    }//GEN-LAST:event_generarPorFechaButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bg;
    private javax.swing.JTextField buscarPacientesInput;
    private com.toedter.calendar.JDateChooser finReporte;
    private javax.swing.JButton generarAnualButton;
    private javax.swing.JButton generarMensualButton;
    private javax.swing.JButton generarPorFechaButton1;
    private javax.swing.JButton generarPorPacienteButton;
    private com.toedter.calendar.JDateChooser inicioReporte;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private com.toedter.calendar.JMonthChooser mesReporteMensualChooser;
    private javax.swing.JComboBox<Paciente> pacientesComboBox;
    private javax.swing.JButton regresarButton;
    private com.toedter.calendar.JYearChooser yearReporteAnualChooser;
    private com.toedter.calendar.JYearChooser yearReporteMensualChooser;
    // End of variables declaration//GEN-END:variables
}
