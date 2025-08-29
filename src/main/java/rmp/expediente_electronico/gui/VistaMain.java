
package rmp.expediente_electronico.gui;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.servicio.ConsultaServicio;
import rmp.expediente_electronico.servicio.PacienteServicio;

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
    private PacienteServicio pacienteServicio;

    @Autowired
    public VistaMain(VistaConsulta vistaConsulta, VistaPaciente vistaPaciente, ConsultaServicio consultaServicio, PacienteServicio pacienteServicio) {
        this.vistaConsulta = vistaConsulta;
        this.vistaPaciente = vistaPaciente;
        this.consultaServicio = consultaServicio;
        this.pacienteServicio = pacienteServicio;

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

        listarConsultas();
    }

    public void listarConsultas(){
        List<Consulta> consultas = consultaServicio.listarConsultas();

        listar(consultas);
    }


    public void listar(List<Consulta> consultas){
        this.tablaModelo.setRowCount(0);

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
    
    public void buscarPaciente(){
        if(buscarConsultaField.getText() == ""){
            listarConsultas();
            return;
        }

        var pacientes = pacienteServicio.buscarPacientes(buscarConsultaField.getText());
        var consultas = consultaServicio.buscarConsultaPacientes(pacientes);
        listar(consultas);

    }

    public void editar(){
        var renglon = tabla.getSelectedRow();

        if(renglon != -1){
            var idConsulta = (Integer) tabla.getModel().getValueAt(renglon,0);
            Consulta consulta = consultaServicio.buscarPorId(idConsulta);

            vistaConsulta.modificarTextoGuardar("Modificar");
            vistaConsulta.rellenar(consulta);
            setVisible(false);
            vistaConsulta.setVisible(true);
        }
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
        buscarConsultaField = new javax.swing.JTextField();

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
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabla);

        buscarConsultaField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                buscarConsultaFieldKeyTyped(evt);
            }
        });

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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buscarConsultaField, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                .addGap(32, 32, 32)
                .addComponent(buscarConsultaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
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
        vistaConsulta.modificarTextoGuardar("Guardar");
        vistaConsulta.setVisible(true);
    }//GEN-LAST:event_consultaButtonActionPerformed

    private void reporteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reporteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reporteButtonActionPerformed

    private void buscarConsultaFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscarConsultaFieldKeyTyped
        buscarPaciente();
    }//GEN-LAST:event_buscarConsultaFieldKeyTyped

    private void tablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMouseClicked
        editar();
    }//GEN-LAST:event_tablaMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField buscarConsultaField;
    private javax.swing.JButton consultaButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pacienteButton;
    private javax.swing.JButton reporteButton;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
