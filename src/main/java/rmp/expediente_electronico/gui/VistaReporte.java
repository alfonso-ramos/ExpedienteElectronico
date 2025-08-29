/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package rmp.expediente_electronico.gui;

import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.servicio.ConsultaServicio;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class VistaReporte extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaReporte.class.getName());

    private ConsultaServicio consultaServicio;
    @Setter
    private VistaMain vistaMain;

    @Autowired
    public VistaReporte(ConsultaServicio consultaServicio) {
        this.consultaServicio = consultaServicio;
        initComponents();
    }

    public void generarReporte() throws IOException {
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

        var consultas = consultaServicio.buscarPorFecha(inicio, fin);
        SimpleDateFormat sdp = new SimpleDateFormat("d-MMMM-yyyy");
        String nombre = "reporte ".concat(sdp.format(inicio).concat(" ".concat(sdp.format(fin))));
        generarReporteConsultas(consultas, nombre);
    }

    public void generarReporteConsultas(List<Consulta> consultas, String nombreArchivo) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte de consultas");
        fileChooser.setSelectedFile(new File(nombreArchivo));

        // Filtro pa’ solo mostrar .xlsx
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();

            // Asegurar que termine con .xlsx
            String rutaArchivo = archivoSeleccionado.getAbsolutePath();
            if (!rutaArchivo.toLowerCase().endsWith(".xlsx")) {
                rutaArchivo += ".xlsx";
            }

            // Creamos el workbook (archivo Excel)
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Reporte");



            // Estilo para cabecera azul con letra blanca
            CellStyle estiloHeader = workbook.createCellStyle();
            Font font = (Font) workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.index); // letra blanca
            estiloHeader.setFont(font);

            // Fondo azul
            estiloHeader.setFillForegroundColor(IndexedColors.ROYAL_BLUE.index);
            estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Encabezados
            String[] columnas = {"Matricula", "Nombres", "Apellidos", "Edad", "Programa academico",
                    "Diagnóstico", "Medicamento", "Observaciones", "Fecha", "Altura", "Peso", "IMC", "Estado IMC"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(estiloHeader);
            }

            // Llenado de datos
            int rowNum = 1;
            for (Consulta consulta : consultas) {
                Row row = sheet.createRow(rowNum++);
                var paciente = consulta.getPaciente();
                row.createCell(0).setCellValue(paciente.getMatricula());
                row.createCell(1).setCellValue(paciente.getNombres());
                row.createCell(2).setCellValue(paciente.getApellidos());
                row.createCell(3).setCellValue(consulta.getEdad() != null ? consulta.getEdad() : 0);
                row.createCell(4).setCellValue(paciente.getProgramaAcademico());
                row.createCell(5).setCellValue(consulta.getDiagnostico());
                row.createCell(6).setCellValue(consulta.getMedicamento());
                row.createCell(7).setCellValue(consulta.getObservaciones());
                row.createCell(8).setCellValue(consulta.getFechaReg() != null ? consulta.getFechaReg().toString() : "");
                row.createCell(9).setCellValue(consulta.getAltura() != null ? consulta.getAltura() : 0f);
                row.createCell(10).setCellValue(consulta.getPeso() != null ? consulta.getPeso() : 0f);
                row.createCell(11).setCellValue(consulta.getImc() != null ? consulta.getImc() : 0f);
                row.createCell(12).setCellValue(consulta.getImc_estado());
            }

            // Autoajustar columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
            workbook.close();
            JOptionPane.showMessageDialog(null, "Reporte guardado en:\n" + rutaArchivo);
        }
    }

    public void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        inicioReporte = new com.toedter.calendar.JDateChooser();
        finReporte = new com.toedter.calendar.JDateChooser();
        generarButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        regresarButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Reportes");

        generarButton.setText("Generar reporte");
        generarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    generarButtonActionPerformed(evt);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        jLabel2.setText("Inicio");

        jLabel3.setText("Fin");

        regresarButton.setText("Regresar");
        regresarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regresarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(136, 136, 136)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inicioReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 298, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(finReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(275, 275, 275))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(379, 379, 379)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(generarButton)
                            .addComponent(jLabel1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(regresarButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addGap(86, 86, 86)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inicioReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(finReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(81, 81, 81)
                .addComponent(generarButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 174, Short.MAX_VALUE)
                .addComponent(regresarButton)
                .addGap(42, 42, 42))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generarButtonActionPerformed(java.awt.event.ActionEvent evt) throws IOException {//GEN-FIRST:event_generarButtonActionPerformed
        generarReporte();
    }//GEN-LAST:event_generarButtonActionPerformed

    private void regresarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regresarButtonActionPerformed
        setVisible(false);
        vistaMain.setVisible(true);
    }//GEN-LAST:event_regresarButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser finReporte;
    private javax.swing.JButton generarButton;
    private com.toedter.calendar.JDateChooser inicioReporte;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton regresarButton;
    // End of variables declaration//GEN-END:variables
}
