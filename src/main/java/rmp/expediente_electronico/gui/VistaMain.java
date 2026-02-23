
package rmp.expediente_electronico.gui;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.modelo.Diagnostico;
import rmp.expediente_electronico.modelo.Paciente;
import rmp.expediente_electronico.servicio.ConsultaServicio;
import rmp.expediente_electronico.servicio.DiagnosticoServicio;
import rmp.expediente_electronico.servicio.PacienteServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

@Component
public class VistaMain extends javax.swing.JFrame {
    

    private VistaPaciente vistaPaciente;
    private VistaConsulta vistaConsulta;
    private DefaultTableModel tablaModelo;
    private ConsultaServicio consultaServicio;
    private PacienteServicio pacienteServicio;
    private DiagnosticoServicio diagnosticoServicio;
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
    public VistaMain(VistaConsulta vistaConsulta, VistaPaciente vistaPaciente, VistaReporte vistaReporte, VistaContacto vistaContacto, PacienteServicio pacienteServicio, ConsultaServicio consultaServicio, DiagnosticoServicio diagnosticoServicio) {
        this.vistaConsulta = vistaConsulta;
        this.vistaPaciente = vistaPaciente;
        this.vistaReporte = vistaReporte;
        this.vistaContacto = vistaContacto;
        this.pacienteServicio = pacienteServicio;
        this.consultaServicio = consultaServicio;
        this.diagnosticoServicio = diagnosticoServicio;

        this.vistaPaciente.setVistaMain(this);
        this.vistaConsulta.setVistaMain(this);
        this.vistaReporte.setVistaMain(this);

        this.vistaPaciente.setProgramas(programas);
        this.vistaReporte.setProgramas(programas);

        initComponents();
        iniciarCombos();
        iniciarTabla();
    }

    public void iniciarTabla(){

        this.tablaModelo = new DefaultTableModel(0, 9){
            @Override
            public boolean isCellEditable(int row,int column){return false;}
        };

        String[] nombresColumnas = {"No de consulta","Nombre del paciente","Matricula","Programa academico","Edad","Diagnostico","Medicamento","Observaciones","Fecha de consulta"};

        this.tablaModelo.setColumnIdentifiers(nombresColumnas);
        this.tabla.setModel(tablaModelo);
        this.tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listarConsultas();
    }

    public void listarConsultas(){
        List<Consulta> consultas = consultaServicio.listarConsultas();

        // Ordenar por fecha de consulta (más recientes primero). Las sin fecha quedan al final.
        consultas.sort((c1, c2) -> {
            if (c1.getFechaReg() == null && c2.getFechaReg() == null) return 0;
            if (c1.getFechaReg() == null) return 1;
            if (c2.getFechaReg() == null) return -1;
            return c2.getFechaReg().compareTo(c1.getFechaReg());
        });

        listar(consultas);
    }


    public void listar(List<Consulta> consultas){
        this.tablaModelo.setRowCount(0);

        consultas.forEach(consulta -> {

            Paciente paciente = consulta.getPaciente();

            String fechaConsulta = "";
            if (consulta.getFechaReg() != null) {
                // fechaReg es java.sql.Date, su toString() es yyyy-MM-dd (solo fecha)
                fechaConsulta = consulta.getFechaReg().toString();
            }

            Object[] renglon ={
                    consulta.getIdConsulta(),
                    paciente != null ? paciente.getNombres().concat(" ").concat(paciente.getApellidos()) : "",
                    paciente != null ? paciente.getMatricula() : "",
                    paciente != null ? paciente.getProgramaAcademico() : "",
                    consulta.getEdad(),
                    consulta.getDiagnostico(),
                    consulta.getMedicamento(),
                    consulta.getObservaciones(),
                    fechaConsulta,
            };
            tablaModelo.addRow(renglon);
        });
    }

    private void iniciarCombos(){

        // Programas académicos desde arreglo programas
        DefaultComboBoxModel<String> modeloProgramas = new DefaultComboBoxModel<>(programas);
        ProgramaAcademicoComboBox.setModel(modeloProgramas);

        // Diagnósticos desde base de datos
        DefaultComboBoxModel<String> modeloDiagnosticos = new DefaultComboBoxModel<>();
        var diagnosticos = diagnosticoServicio.listarDiagnosticos();
        for(Diagnostico d : diagnosticos){
            modeloDiagnosticos.addElement(d.getDiagnostico());
        }
        DiagnosticoCombobox.setModel(modeloDiagnosticos);
    }


    private void limpiarTodosLosCampos(){
        // Limpiar campos del paciente
        MatriculaTextField.setText("");
        nombreTextField.setText("");
        apellidoTextField.setText("");
        ProgramaAcademicoComboBox.setSelectedIndex(-1);
        jTextField1.setText("");
        
        // Limpiar campos de la consulta
        DiagnosticoCombobox.setSelectedIndex(-1);
        diagnosticoTextField.setText("");
        medicamentoTextField.setText("");
        ObservacionesTextField.setText("");
    }

    private Paciente obtenerPacientePorMatriculaExacta(String matricula){
        if(matricula == null || matricula.isBlank()){
            return null;
        }

        var pacientes = pacienteServicio.buscarPacientes(matricula);
        return pacientes.stream()
                .filter(p -> p.getMatricula() != null && p.getMatricula().equalsIgnoreCase(matricula))
                .findFirst()
                .orElse(null);
    }

    private void cargarPacientePorMatricula(){
        String matricula = MatriculaTextField.getText();
        if(matricula == null || matricula.isBlank()){
            limpiarTodosLosCampos();
            return;
        }

        Paciente paciente = obtenerPacientePorMatriculaExacta(matricula);

        if(paciente != null){
            nombreTextField.setText(paciente.getNombres());
            apellidoTextField.setText(paciente.getApellidos());
            ProgramaAcademicoComboBox.setSelectedItem(paciente.getProgramaAcademico());
            if(paciente.getEdad() != null){
                jTextField1.setText(paciente.getEdad().toString());
            } else {
                jTextField1.setText("");
            }
        } else {
            limpiarTodosLosCampos();
        }
    }

    public void editar(){
        // Función de edición deshabilitada: toda la gestión ahora se realiza directamente en VistaMain
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        LogoUpsin = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        nombreLabel = new javax.swing.JLabel();
        MatriculaLabel = new javax.swing.JLabel();
        MatriculaTextField = new javax.swing.JTextField();
        nombreTextField = new javax.swing.JTextField();
        ProgramaAcademicoComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        DiagnosticoCombobox = new javax.swing.JComboBox<>();
        diagnosticoTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        medicamentoTextField = new javax.swing.JTextField();
        ObservacionesTextField = new javax.swing.JTextField();
        GuardarConsultaBotton = new javax.swing.JButton();
        nombreLabel1 = new javax.swing.JLabel();
        apellidoTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bg.setBackground(new java.awt.Color(51, 51, 51));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabel1.setText("Exprediente Electronico");

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabel2.setText("Registro de Actividades Diarias en Consulta Externa");

        LogoUpsin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logoUpsin.png"))); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/med(1).jpg"))); // NOI18N
        jLabel3.setText("jLabel3");
        jLabel3.setToolTipText("");

        jButton1.setBackground(new java.awt.Color(26, 188, 156));
        jButton1.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Contacto");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        nombreLabel.setText("Nombre");

        MatriculaLabel.setText("Matricula");

        MatriculaTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MatriculaTextFieldActionPerformed(evt);
            }
        });

        ProgramaAcademicoComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("Programa Academico");

        jLabel5.setText("Edad");

        jTextField1.setText("jTextField1");

        jLabel6.setText("Diagnostico");

        DiagnosticoCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        DiagnosticoCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosticoComboboxActionPerformed(evt);
            }
        });

        diagnosticoTextField.setText("jTextField2");

        jLabel7.setText("Medicamento");

        jLabel8.setText("Observaciones");

        medicamentoTextField.setText("jTextField3");

        ObservacionesTextField.setText("jTextField4");

        GuardarConsultaBotton.setBackground(new java.awt.Color(26, 188, 156));
        GuardarConsultaBotton.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        GuardarConsultaBotton.setForeground(new java.awt.Color(255, 255, 255));
        GuardarConsultaBotton.setText("Guardar Consulta");
        GuardarConsultaBotton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarConsultaBottonActionPerformed(evt);
            }
        });

        nombreLabel1.setText("Apellido");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(MatriculaLabel)
                                            .addComponent(nombreLabel))
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(39, 39, 39)
                                                .addComponent(MatriculaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(apellidoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(nombreTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                    .addComponent(jLabel4)
                                    .addComponent(ProgramaAcademicoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nombreLabel1))
                                .addGap(74, 74, 74)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(medicamentoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel7)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(diagnosticoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addGap(36, 36, 36)
                                                .addComponent(DiagnosticoCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(GuardarConsultaBotton, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(ObservacionesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1408, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(LogoUpsin)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(219, 219, 219)
                                .addComponent(jLabel2))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(381, 381, 381)
                                .addComponent(jLabel1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(210, 210, 210)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(DiagnosticoCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(diagnosticoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nombreLabel)
                            .addComponent(nombreTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ObservacionesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(nombreLabel1)
                            .addComponent(apellidoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(GuardarConsultaBotton)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(32, 32, 32)
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel1))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(LogoUpsin, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(22, 22, 22)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(41, 41, 41)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(MatriculaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(MatriculaLabel)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(medicamentoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ProgramaAcademicoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                .addContainerGap())
        );

        bg.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1500, 890));

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

    private void tablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMouseClicked
        editar();
    }//GEN-LAST:event_tablaMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        vistaContacto.setLocationRelativeTo(this);
        vistaContacto.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void MatriculaTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MatriculaTextFieldActionPerformed
        cargarPacientePorMatricula();
    }//GEN-LAST:event_MatriculaTextFieldActionPerformed

    private void DiagnosticoComboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DiagnosticoComboboxActionPerformed
        Object seleccionado = DiagnosticoCombobox.getSelectedItem();
        if(seleccionado != null){
            diagnosticoTextField.setText(seleccionado.toString());
        }
    }//GEN-LAST:event_DiagnosticoComboboxActionPerformed

    private void GuardarConsultaBottonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarConsultaBottonActionPerformed
        String matricula = MatriculaTextField.getText();
        String nombresInput = nombreTextField.getText();
        String apellidosInput = apellidoTextField.getText();
        Object programaSeleccionado = ProgramaAcademicoComboBox.getSelectedItem();

        if(matricula == null || matricula.isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese la matrícula del paciente");
            MatriculaTextField.requestFocusInWindow();
            return;
        }

        if(nombresInput == null || nombresInput.isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del paciente");
            nombreTextField.requestFocusInWindow();
            return;
        }

        if(apellidosInput == null || apellidosInput.isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese el apellido del paciente");
            apellidoTextField.requestFocusInWindow();
            return;
        }

        if(programaSeleccionado == null){
            JOptionPane.showMessageDialog(this, "Seleccione un programa académico");
            ProgramaAcademicoComboBox.requestFocusInWindow();
            return;
        }

        // Edad capturada en el campo de texto
        String edadTexto = jTextField1.getText();
        if(edadTexto == null || edadTexto.isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese la edad del paciente");
            jTextField1.requestFocusInWindow();
            return;
        }

        Integer edad;
        try {
            edad = Integer.parseInt(edadTexto.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número válido");
            jTextField1.requestFocusInWindow();
            return;
        }

        Paciente paciente = obtenerPacientePorMatriculaExacta(matricula);
        if(paciente == null){
            // Crear paciente básico directamente desde VistaMain usando campos de nombre y apellido
            paciente = new Paciente();
            paciente.setMatricula(matricula.trim());

            String nombres = nombresInput.trim();
            String apellidos = apellidosInput.trim();

            paciente.setNombres(nombres);
            paciente.setApellidos(apellidos);

            paciente.setProgramaAcademico(programaSeleccionado.toString());
            paciente.setEdad(edad);

            // Campos sin captura directa en VistaMain se dejan null (sexo)
            try{
                pacienteServicio.guardarPaciente(paciente);
            } catch (Exception e){
                JOptionPane.showMessageDialog(this, "Error al guardar el paciente: " + e.getMessage());
                return;
            }
        } else {
            // Actualizar edad y programa si el paciente ya existe
            paciente.setProgramaAcademico(programaSeleccionado.toString());
            paciente.setEdad(edad);
            try{
                pacienteServicio.guardarPaciente(paciente);
            } catch (Exception e){
                JOptionPane.showMessageDialog(this, "Error al actualizar el paciente: " + e.getMessage());
                return;
            }
        }

        String diagnosticoTexto = diagnosticoTextField.getText();
        String medicamento = medicamentoTextField.getText();
        String observaciones = ObservacionesTextField.getText();

        if(diagnosticoTexto == null || diagnosticoTexto.isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese un diagnóstico para registrar la consulta");
            return;
        }

        if(medicamento == null || medicamento.isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese un medicamento para registrar la consulta");
            return;
        }

        if(observaciones == null || observaciones.isBlank()){
            JOptionPane.showMessageDialog(this, "Ingrese observaciones para registrar la consulta");
            return;
        }

        // Buscar objeto Diagnostico correspondiente al texto seleccionado en el combo (si aplica)
        Diagnostico diagnosticoKey = null;
        Object seleccionado = DiagnosticoCombobox.getSelectedItem();
        if(seleccionado != null){
            String textoSeleccionado = seleccionado.toString();
            var diagnosticos = diagnosticoServicio.listarDiagnosticos();
            diagnosticoKey = diagnosticos.stream()
                    .filter(d -> d.getDiagnostico() != null && d.getDiagnostico().equalsIgnoreCase(textoSeleccionado))
                    .findFirst()
                    .orElse(null);
        }

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setDiagnosticoKey(diagnosticoKey);
        consulta.setDiagnostico(diagnosticoTexto.trim());
        consulta.setMedicamento(medicamento.trim());
        // Guardamos solo la fecha (sin hora) de la consulta
        consulta.setFechaReg(java.sql.Date.valueOf(java.time.LocalDate.now()));
        consulta.setObservaciones(observaciones.trim());

        // Valores por defecto para campos relacionados con IMC que ya no se capturan en VistaMain
        consulta.setAltura(0.0f);
        consulta.setPeso(0.0f);
        consulta.setTalla("");
        consulta.setImc(0.0f);
        consulta.setImc_estado("");

        // Usar la edad capturada (ya validada) para la consulta
        consulta.setEdad(edad);

        try {
            consultaServicio.guardarConsulta(consulta);
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error al guardar la consulta: " + e.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Consulta registrada correctamente");

        // Limpiar todos los campos después de registrar consulta
        limpiarTodosLosCampos();

        // Actualizar tabla principal
        listarConsultas();
    }//GEN-LAST:event_GuardarConsultaBottonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> DiagnosticoCombobox;
    private javax.swing.JButton GuardarConsultaBotton;
    private javax.swing.JLabel LogoUpsin;
    private javax.swing.JLabel MatriculaLabel;
    private javax.swing.JTextField MatriculaTextField;
    private javax.swing.JTextField ObservacionesTextField;
    private javax.swing.JComboBox<String> ProgramaAcademicoComboBox;
    private javax.swing.JTextField apellidoTextField;
    private javax.swing.JPanel bg;
    private javax.swing.JTextField diagnosticoTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField medicamentoTextField;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JLabel nombreLabel1;
    private javax.swing.JTextField nombreTextField;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
