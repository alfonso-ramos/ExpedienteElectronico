
package rmp.expediente_electronico.gui;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.PacienteServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

@Component
public class VistaMain extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaMain.class.getName());

    private VistaPaciente vistaPaciente;
    private VistaConsulta vistaConsulta;
    private DefaultTableModel tablaModelo;
    private PacienteServicio pacienteServicio;
    private VistaReporte vistaReporte;
    private VistaContacto vistaContacto;

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
    public VistaMain(VistaConsulta vistaConsulta, VistaPaciente vistaPaciente, VistaReporte vistaReporte, VistaContacto vistaContacto, PacienteServicio pacienteServicio) {
        this.vistaConsulta = vistaConsulta;
        this.vistaPaciente = vistaPaciente;
        this.vistaReporte = vistaReporte;
        this.vistaContacto = vistaContacto;
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

        this.tablaModelo = new DefaultTableModel(0, 7){
            @Override
            public boolean isCellEditable(int row,int column){return false;}
        };

        String[] nombresColumnas = {"Id","Matricula","Nombres","Apellidos","Sexo","Programa","Fecha de nacimiento"};

        this.tablaModelo.setColumnIdentifiers(nombresColumnas);
        this.tabla.setModel(tablaModelo);
        this.tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listarPacientes();
    }

    public void listarPacientes(){
        List<Paciente> pacientes = pacienteServicio.listarPacientes();
        listar(pacientes);
    }


    public void listar(List<Paciente> pacientes){
        this.tablaModelo.setRowCount(0);

        pacientes.forEach(paciente -> {

            Object[] renglon ={
                    paciente.getIdPaciente(),
                    paciente.getMatricula(),
                    paciente.getNombres(),
                    paciente.getApellidos(),
                    paciente.getSexo(),
                    paciente.getProgramaAcademico(),
                    paciente.getFechaNacimiento()
            };
            tablaModelo.addRow(renglon);
        });
    }
    
    public void buscarPaciente(){
        String criterio = buscarConsultaField.getText();
        if(criterio == null || criterio.isBlank()){
            listarPacientes();
            return;
        }

        var pacientes = pacienteServicio.buscarPacientes(criterio);
        listar(pacientes);
    }

    public void editar(){
        var renglon = tabla.getSelectedRow();

        if(renglon != -1){
            var idPaciente = (Integer) tabla.getModel().getValueAt(renglon,0);
            setVisible(false);
            vistaPaciente.setLocationRelativeTo(this);
            vistaPaciente.setVisible(true);
            vistaPaciente.cargarPacientePorId(idPaciente);
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
        jButton1 = new javax.swing.JButton();
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

        jButton1.setBackground(new java.awt.Color(26, 188, 156));
        jButton1.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Contacto");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                    .addComponent(pacienteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 261, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 860));

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
                .addGap(88, 88, 88)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1097, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buscarConsultaField, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(buscarConsultaField, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        bg.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 1240, 860));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        vistaContacto.setLocationRelativeTo(this);
        vistaContacto.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bg;
    private javax.swing.JTextField buscarConsultaField;
    private javax.swing.JButton consultaButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pacienteButton;
    private javax.swing.JButton reporteButton;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
