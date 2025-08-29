
package rmp.expediente_electronico.gui;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.servicio.ConsultaServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

@Component
public class VistaMain extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaMain.class.getName());

    private VistaPaciente vistaPaciente;
    private VistaConsulta vistaConsulta;
    private ConsultaServicio consultaServicio;
    private DefaultTableModel tablaModelo;

    @Autowired
    public VistaMain(VistaConsulta vistaConsulta, VistaPaciente vistaPaciente, ConsultaServicio consultaServicio) {
        this.vistaConsulta = vistaConsulta;
        this.vistaPaciente = vistaPaciente;
        this.consultaServicio = consultaServicio;

        this.vistaPaciente.setVistaMain(this);
        this.vistaConsulta.setVistaMain(this);

        initComponents();
        iniciarTabla();
    }

    public void iniciarTabla(){

        this.tablaModelo = new DefaultTableModel(0, 12){
            @Override
            public boolean isCellEditable(int row,int column){return false;}
        };

        String[] nombresColumnas = {"Id","Paciente","Diagnostico","Medicamento","Fecha de registro","Observaciones","Talla","Imc","Estado","Edad"};

        this.tablaModelo.setColumnIdentifiers(nombresColumnas);
        this.tabla.setModel(tablaModelo);
        this.tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listar();
    }

    public void listar(){
        this.tablaModelo.setRowCount(0);

        List<Consulta> consultas = consultaServicio.listarConsultas();

        consultas.forEach(consulta -> {

            Object[] renglon ={
                    consulta.getIdConsulta(),
                    consulta.getPaciente(),
                    consulta.getDiagnostico(),
                    consulta.getMedicamento(),
                    consulta.getFechaReg(),
                    consulta.getObservaciones(),
                    consulta.getTalla(),
                    consulta.getAltura(),
                    consulta.getPeso(),
                    consulta.getImc(),
                    consulta.getImc_estado(),
                    consulta.getEdad()
            };
            tablaModelo.addRow(renglon);
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        pacienteButton = new javax.swing.JButton();
        consultaButton = new javax.swing.JButton();
        reporteButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("vista main");

        pacienteButton.setText("Registrar paciente");
        pacienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pacienteButtonActionPerformed(evt);
            }
        });

        consultaButton.setText("Registrar consulta");
        consultaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consultaButtonActionPerformed(evt);
            }
        });

        reporteButton.setText("Generar reporte");
        reporteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reporteButtonActionPerformed(evt);
            }
        });

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
        jScrollPane1.setViewportView(tabla);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(435, 435, 435))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(pacienteButton)
                        .addGap(105, 105, 105)
                        .addComponent(consultaButton)
                        .addGap(138, 138, 138)
                        .addComponent(reporteButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(121, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(82, 82, 82)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pacienteButton)
                    .addComponent(consultaButton)
                    .addComponent(reporteButton))
                .addGap(75, 75, 75)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pacienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pacienteButtonActionPerformed
        this.setVisible(false);
        vistaPaciente.setLocationRelativeTo(this);
        vistaPaciente.setVisible(true);
    }//GEN-LAST:event_pacienteButtonActionPerformed

    private void consultaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_consultaButtonActionPerformed
        this.setVisible(false);
        vistaConsulta.setLocationRelativeTo(this);
        vistaConsulta.setVisible(true);
    }//GEN-LAST:event_consultaButtonActionPerformed

    private void reporteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reporteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reporteButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton consultaButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pacienteButton;
    private javax.swing.JButton reporteButton;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
