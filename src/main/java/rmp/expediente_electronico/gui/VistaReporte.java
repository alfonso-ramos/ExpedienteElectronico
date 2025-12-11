/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package rmp.expediente_electronico.gui;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import rmp.expediente_electronico.servicio.ReporteServicio;

import java.awt.event.ActionEvent;

@Component
public class VistaReporte extends JFrame {

    private ReporteServicio reporteServicio;
    @Setter
    private VistaMain vistaMain;
    private String[] programas;

    // Altura Titulo Principal (ej: 40 puntos = 800 twips)
    final short ALTURA_TITULO_PRINCIPAL = 800;

    // Altura Subtítulos (ej: 30 puntos = 600 twips)
    final short ALTURA_SUBTITULO = 600;

    @Autowired
    public VistaReporte(ReporteServicio reporteServicio){
        this.reporteServicio = reporteServicio;

        initComponents();
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

    public void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
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
        generarPorFechaButton = new javax.swing.JButton();
        yearReporteMensualChooser = new com.toedter.calendar.JYearChooser();
        jPanel1 = new javax.swing.JPanel();
        regresarButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        inicioReporte.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(inicioReporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 170, 250, 40));

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel3.setText("Fecha final");
        bg.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, -1, -1));

        finReporte.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(finReporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 290, 250, 40));

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel2.setText("Año");
        bg.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 130, -1, -1));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Generar reporte por año");
        bg.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 60, -1, 50));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel4.setText("Generar reporte por fecha");
        bg.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, -1, 50));

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel5.setText("Fecha de inicio");
        bg.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, -1, -1));

        mesReporteMensualChooser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(mesReporteMensualChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 160, 140, 50));

        generarAnualButton.setBackground(new java.awt.Color(26, 188, 156));
        generarAnualButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        generarAnualButton.setForeground(new java.awt.Color(255, 255, 255));
        generarAnualButton.setText("Generar reporte");
        generarAnualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarAnualButtonActionPerformed(evt);
            }
        });
        bg.add(generarAnualButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 270, 180, 60));

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel6.setText("Generar reporte por mes");
        bg.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 60, -1, 50));

        yearReporteAnualChooser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(yearReporteAnualChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 160, 140, 50));

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel7.setText("Mes");
        bg.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 130, -1, -1));

        generarMensualButton.setBackground(new java.awt.Color(26, 188, 156));
        generarMensualButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        generarMensualButton.setForeground(new java.awt.Color(255, 255, 255));
        generarMensualButton.setText("Generar reporte");
        generarMensualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarMensualButtonActionPerformed(evt);
            }
        });
        bg.add(generarMensualButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 270, 180, 60));

        generarPorFechaButton.setBackground(new java.awt.Color(26, 188, 156));
        generarPorFechaButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        generarPorFechaButton.setForeground(new java.awt.Color(255, 255, 255));
        generarPorFechaButton.setText("Generar reporte");
        generarPorFechaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generarPorFechaButtonActionPerformed(evt);
            }
        });
        bg.add(generarPorFechaButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, 180, 60));

        yearReporteMensualChooser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        bg.add(yearReporteMensualChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 160, 120, 50));

        jPanel1.setBackground(new java.awt.Color(56, 89, 152));

        regresarButton.setBackground(new java.awt.Color(255, 0, 51));
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addComponent(regresarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(regresarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(768, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 1310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void regresarButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_regresarButtonActionPerformed
        setVisible(false);
        vistaMain.setVisible(true);
    }//GEN-LAST:event_regresarButtonActionPerformed

    private void generarAnualButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarAnualButtonActionPerformed
        generarReporteAnual();
    }//GEN-LAST:event_generarAnualButtonActionPerformed

    private void generarMensualButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarMensualButtonActionPerformed
        generarReporteMensual();
    }//GEN-LAST:event_generarMensualButtonActionPerformed

    private void generarPorFechaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarPorFechaButtonActionPerformed
        generarReporteFecha();
    }//GEN-LAST:event_generarPorFechaButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bg;
    private com.toedter.calendar.JDateChooser finReporte;
    private javax.swing.JButton generarAnualButton;
    private javax.swing.JButton generarMensualButton;
    private javax.swing.JButton generarPorFechaButton;
    private com.toedter.calendar.JDateChooser inicioReporte;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private com.toedter.calendar.JMonthChooser mesReporteMensualChooser;
    private javax.swing.JButton regresarButton;
    private com.toedter.calendar.JYearChooser yearReporteAnualChooser;
    private com.toedter.calendar.JYearChooser yearReporteMensualChooser;
    // End of variables declaration//GEN-END:variables
}
