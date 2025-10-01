
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
    private VistaReporte vistaReporte;

    // Programas academicos
    private String[] programas = {
            "Docente",
            "Recursos humanos (RRHH)",
            "Intendencia",
            "Maestrias",
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

    @Autowired
    public VistaMain(VistaConsulta vistaConsulta, VistaPaciente vistaPaciente,VistaReporte vistaReporte, ConsultaServicio consultaServicio, PacienteServicio pacienteServicio) {
        this.vistaConsulta = vistaConsulta;
        this.vistaPaciente = vistaPaciente;
        this.vistaReporte = vistaReporte;
        this.consultaServicio = consultaServicio;
        this.pacienteServicio = pacienteServicio;

        this.vistaPaciente.setVistaMain(this);
        this.vistaConsulta.setVistaMain(this);
        this.vistaReporte.setVistaMain(this);

        this.vistaPaciente.setProgramas(programas);
        this.vistaReporte.setProgramas(programas);

        initComponents();
        iniciarTabla();
    }

    public void iniciarTabla(){

        this.tablaModelo = new DefaultTableModel(0, 11){
            @Override
            public boolean isCellEditable(int row,int column){return false;}
        };

        String[] nombresColumnas = {"Id","Paciente","Causa de diagnostico","Diagnostico","Medicamento","Fecha de registro","Observaciones","Talla","Imc","Estado","Edad"};

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
                    consulta.getDiagnosticoKey(),
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

        bg = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        consultaButton = new javax.swing.JButton();
        reporteButton = new javax.swing.JButton();
        pacienteButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        buscarConsultaField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bg.setBackground(new java.awt.Color(51, 51, 51));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(56, 89, 152));

        consultaButton.setBackground(new java.awt.Color(26, 188, 156));
        consultaButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        consultaButton.setForeground(new java.awt.Color(255, 255, 255));
        consultaButton.setText("Registrar consulta");
        consultaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consultaButtonActionPerformed(evt);
            }
        });

        reporteButton.setBackground(new java.awt.Color(26, 188, 156));
        reporteButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        reporteButton.setForeground(new java.awt.Color(255, 255, 255));
        reporteButton.setText("Generar reporte");
        reporteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reporteButtonActionPerformed(evt);
            }
        });

        pacienteButton.setBackground(new java.awt.Color(26, 188, 156));
        pacienteButton.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        pacienteButton.setForeground(new java.awt.Color(255, 255, 255));
        pacienteButton.setText("Registrar paciente");
        pacienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pacienteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reporteButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(consultaButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pacienteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(206, 206, 206)
                .addComponent(consultaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(pacienteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(reporteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(230, Short.MAX_VALUE))
        );

        bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 730));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

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

        buscarConsultaField.setText("Buscar");
        buscarConsultaField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarConsultaFieldActionPerformed(evt);
            }
        });
        buscarConsultaField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                buscarConsultaFieldKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 897, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buscarConsultaField, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(buscarConsultaField, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        bg.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 1030, 730));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        setVisible(false);
        vistaReporte.setLocationRelativeTo(this);
        vistaReporte.setVisible(true);
    }//GEN-LAST:event_reporteButtonActionPerformed

    private void buscarConsultaFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buscarConsultaFieldKeyTyped
        buscarPaciente();
    }//GEN-LAST:event_buscarConsultaFieldKeyTyped

    private void buscarConsultaFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarConsultaFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarConsultaFieldActionPerformed

    private void tablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMouseClicked
        editar();
    }//GEN-LAST:event_tablaMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bg;
    private javax.swing.JTextField buscarConsultaField;
    private javax.swing.JButton consultaButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pacienteButton;
    private javax.swing.JButton reporteButton;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
