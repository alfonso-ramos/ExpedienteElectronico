package rmp.expediente_electronico.servicio;

import com.toedter.calendar.JDateChooser;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xddf.usermodel.chart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.modelo.Diagnostico;
import rmp.expediente_electronico.modelo.Paciente;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteServicio {

    @Setter
    private String[] programas;
    @Autowired
    private ConsultaServicio consultaServicio;
    @Autowired
    private PacienteServicio pacienteServicio;
    @Autowired
    private DiagnosticoServicio diagnosticoServicio;

    // Altura Titulo Principal (ej: 40 puntos = 800 twips)
    final short ALTURA_TITULO_PRINCIPAL = 800;

    // Altura Subtítulos (ej: 30 puntos = 600 twips)
    final short ALTURA_SUBTITULO = 600;

    public void generarReporteFecha(Date inicio, Date fin) {

        var consultas = consultaServicio.buscarPorFecha(inicio, fin);
        var diagosticos = diagnosticoServicio.listarDiagnosticos();
        SimpleDateFormat sdp = new SimpleDateFormat("d-MMMM-yyyy");
        String nombreArchivo = "EXPELEC reporte ".concat(sdp.format(inicio).concat(" ".concat(sdp.format(fin))));

        try {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar reporte de consultas");
            fileChooser.setSelectedFile(new File(nombreArchivo));

            // Filtro pa’ solo mostrar .xlsx
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));

            int userSelection = fileChooser.showSaveDialog(null);

            String rutaArchivo = "";
            Workbook workbook = null;
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = fileChooser.getSelectedFile();

                // Asegurar que termine con .xlsx
                rutaArchivo = archivoSeleccionado.getAbsolutePath();
                if (!rutaArchivo.toLowerCase().endsWith(".xlsx")) {
                    rutaArchivo += ".xlsx";
                }

                // Creamos el workbook (archivo Excel)
                workbook = new XSSFWorkbook();

                // Generar los reportes
                String titulo =  sdp.format(inicio).concat(" / ").concat(sdp.format(fin));
                generarReporteConsultas(consultas, diagosticos, workbook, titulo);
                generarEstadisticasConsultas(workbook,consultas,diagosticos,sdp.format(inicio),sdp.format(fin));
            }

            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                assert workbook != null;
                workbook.write(fileOut);
            }
            workbook.close();
            JOptionPane.showMessageDialog(null, "Reporte guardado en:\n" + rutaArchivo);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }

    public void generarReportePaciente(Paciente paciente) {

        if (paciente == null) {
            mostrarMensaje("Seleccione un paciente válido para generar el reporte");
            return;
        }

        var consultas = consultaServicio.buscarPorPaciente(paciente);

        if (consultas.isEmpty()) {
            mostrarMensaje("El paciente seleccionado no tiene consultas registradas");
            return;
        }

        var diagnosticos = diagnosticoServicio.listarDiagnosticos();
        SimpleDateFormat sdp = new SimpleDateFormat("yyyyMMdd_HHmm");
        String nombreArchivo = "EXPELEC reporte_paciente_".concat(paciente.getMatricula()).concat("_" + sdp.format(new Date()));

        try {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar reporte histórico del paciente");
            fileChooser.setSelectedFile(new File(nombreArchivo));

            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));

            int userSelection = fileChooser.showSaveDialog(null);

            String rutaArchivo = null;
            Workbook workbook = null;
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = fileChooser.getSelectedFile();

                rutaArchivo = archivoSeleccionado.getAbsolutePath();
                if (!rutaArchivo.toLowerCase().endsWith(".xlsx")) {
                    rutaArchivo += ".xlsx";
                }

                workbook = new XSSFWorkbook();

                generarResumenPaciente(workbook, paciente, consultas.size());
                String titulo = paciente.toString();
                generarReporteConsultas(consultas, diagnosticos, workbook, titulo);
            }

            if (workbook == null || rutaArchivo == null) {
                return;
            }

            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
            workbook.close();
            JOptionPane.showMessageDialog(null, "Reporte guardado en:\n" + rutaArchivo);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }


    public void generarReporteConsultas(List<Consulta> consultas, List<Diagnostico> diagnosticos, Workbook workbook, String titulo) throws IOException {

        Sheet sheet = workbook.createSheet("Consultas");

        // =========================================================
        // CREACIÓN DE ESTILOS REQUERIDOS
        // =========================================================

        // 1. ESTILO PARA EL TÍTULO PRINCIPAL (Fondo Oscuro, Letra Blanca, 24px)
        CellStyle estiloTituloPrincipal = crearEstiloTitulo(workbook);


        // 2. ESTILO PARA SUBTÍTULOS / ENCABEZADOS DE SECCIÓN (Negro, 16px, Fondo Blanco)
        CellStyle estiloSubtitulo = crearEstiloSubtitulo(workbook);


        // 3. ESTILO NORMAL (Letra 14px y Bordes en todas las direcciones)
        CellStyle estiloNormal = crearEstiloNormal(workbook);

        // estilo decimales
        CellStyle estiloNumero = crearEstiloNumeroDosDecimales(workbook, estiloNormal);

        // estilo negro
        CellStyle estiloNegro = crearEstiloNegro(workbook);

        // titulo
        Row tituloRow = sheet.createRow(0);
        tituloRow.setHeight(ALTURA_TITULO_PRINCIPAL);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellStyle(estiloTituloPrincipal);
        tituloCell.setCellValue("Consultas: ".concat(titulo));
        for (int i = 1; i<13;i++){
            tituloRow.createCell(i).setCellStyle(estiloNegro);
        }

        // Encabezados
        String[] columnas = {"Matricula", "Nombres", "Apellidos", "Edad", "Programa academico",
                "Diagnóstico", "Causa diagnóstico", "Medicamento", "Observaciones", "Fecha", "Altura", "Peso", "IMC", "Estado IMC"};
        Row headerRow = sheet.createRow(1);
        headerRow.setHeight(ALTURA_SUBTITULO);
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(estiloSubtitulo);
        }

        // Llenado de datos
        int rowNum = 2;
        for (Consulta consulta : consultas) {
            Row row = sheet.createRow(rowNum++);

            var paciente = consulta.getPaciente();

            // ----------------------------------------------------
            // APLICACIÓN DE ESTILO NORMAL A CELDAS DE TEXTO
            // ----------------------------------------------------

            // Columna 0: Matricula
            Cell c0 = row.createCell(0);
            c0.setCellValue(paciente.getMatricula());
            c0.setCellStyle(estiloNormal);

            // Columna 1: Nombres
            Cell c1 = row.createCell(1);
            c1.setCellValue(paciente.getNombres());
            c1.setCellStyle(estiloNormal);

            // Columna 2: Apellidos
            Cell c2 = row.createCell(2);
            c2.setCellValue(paciente.getApellidos());
            c2.setCellStyle(estiloNormal);

            // Columna 3: Edad (es entero, usa estiloNormal)
            Cell c3 = row.createCell(3);
            c3.setCellValue(consulta.getEdad() != null ? consulta.getEdad() : 0);
            c3.setCellStyle(estiloNormal);

            // Columna 4: Programa academico
            Cell c4 = row.createCell(4);
            c4.setCellValue(paciente.getProgramaAcademico());
            c4.setCellStyle(estiloNormal);

            // Columna 5: Diagnóstico (texto libre)
            Cell c5 = row.createCell(5);
            c5.setCellValue(consulta.getDiagnostico());
            c5.setCellStyle(estiloNormal);

            // Columna 6: Causa del diagnóstico (catálogo)
            Cell c6 = row.createCell(6);
            String causaDiagnostico = consulta.getDiagnosticoKey() != null ? consulta.getDiagnosticoKey().toString() : "";
            c6.setCellValue(causaDiagnostico);
            c6.setCellStyle(estiloNormal);

            // Columna 7: Medicamento
            Cell c7 = row.createCell(7);
            c7.setCellValue(consulta.getMedicamento());
            c7.setCellStyle(estiloNormal);

            // Columna 8: Observaciones
            Cell c8 = row.createCell(8);
            c8.setCellValue(consulta.getObservaciones());
            c8.setCellStyle(estiloNormal);

            // Columna 9: Fecha de la consulta (solo fecha, sin hora)
            Cell c9 = row.createCell(9);
            if (consulta.getFechaReg() != null) {
                java.time.LocalDate fecha = consulta.getFechaReg().toLocalDate();
                c9.setCellValue(fecha.toString()); // yyyy-MM-dd
            } else {
                c9.setCellValue("");
            }
            c9.setCellStyle(estiloNormal);

            // ----------------------------------------------------
            // APLICACIÓN DE ESTILO NÚMERO (Float)
            // ----------------------------------------------------

            // Columna 10: Altura
            Cell c10 = row.createCell(10);
            // Usamos setCellValue(double) para que aplique el formato de número
            c10.setCellValue(consulta.getAltura() != null ? consulta.getAltura() : 0f);
            c10.setCellStyle(estiloNumero);

            // Columna 11: Peso
            Cell c11 = row.createCell(11);
            c11.setCellValue(consulta.getPeso() != null ? consulta.getPeso() : 0f);
            c11.setCellStyle(estiloNumero);

            // Columna 12: IMC
            Cell c12 = row.createCell(12);
            c12.setCellValue(consulta.getImc() != null ? consulta.getImc() : 0f);
            c12.setCellStyle(estiloNumero);

            // Columna 13: Estado IMC (texto)
            Cell c13 = row.createCell(13);
            c13.setCellValue(consulta.getImc_estado());
            c13.setCellStyle(estiloNormal);
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

    }

    public void generarEstadisticasConsultas(Workbook workbook, List<Consulta> consultas, List<Diagnostico> diagnosticos, String fechaInicio, String fechaFin){
        Sheet sheet = workbook.createSheet("Estadisticas");

        // =========================================================
        // CREACIÓN DE ESTILOS REQUERIDOS
        // =========================================================

        // 1. ESTILO PARA EL TÍTULO PRINCIPAL (Fondo Oscuro, Letra Blanca, 24px)
        CellStyle estiloTituloPrincipal = crearEstiloTitulo(workbook);


        // 2. ESTILO PARA SUBTÍTULOS / ENCABEZADOS DE SECCIÓN (Negro, 16px, Fondo Blanco)
        CellStyle estiloSubtitulo = crearEstiloSubtitulo(workbook);


        // 3. ESTILO NORMAL (Letra 14px y Bordes en todas las direcciones)
        CellStyle estiloNormal = crearEstiloNormal(workbook);

        // estilo negro
        CellStyle estiloNegro = crearEstiloNegro(workbook);

        int rowNum = 0;
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue(fechaInicio.concat(" / ".concat(fechaFin)));
        tituloCell.setCellStyle(estiloTituloPrincipal);
        tituloRow.createCell(1).setCellStyle(estiloTituloPrincipal);
        tituloRow.setHeight(ALTURA_TITULO_PRINCIPAL);


        Row subtituloRow = sheet.createRow(rowNum++);
        subtituloRow.setHeight(ALTURA_SUBTITULO);
        Cell subtituloCell = subtituloRow.createCell(0);
        subtituloCell.setCellValue("Total de consultas");
        subtituloCell.setCellStyle(estiloSubtitulo);
        Cell totalConsultas = subtituloRow.createCell(1);
        totalConsultas.setCellValue(consultas.size());
        totalConsultas.setCellStyle(estiloNormal);


        // 1. Conteo por Diagnóstico (filtrando nulos)
        Map<Diagnostico, Long> conteoPorDiagnostico = consultas.stream()
                .filter(c -> c.getDiagnosticoKey() != null)
                .collect(Collectors.groupingBy(Consulta::getDiagnosticoKey, Collectors.counting()));

        rowNum = escribirSeccionDiagnostico(sheet, rowNum, conteoPorDiagnostico, diagnosticos, estiloSubtitulo, estiloNormal, estiloTituloPrincipal);

        // 2. Conteo por Programa Académico (filtrando nulos)
        Map<String, Long> conteoPorPrograma = consultas.stream()
                .filter(c -> c.getPaciente() != null && c.getPaciente().getProgramaAcademico() != null)
                .collect(Collectors.groupingBy(c -> c.getPaciente().getProgramaAcademico(), Collectors.counting()));

        rowNum = escribirSeccionCarreras(sheet, rowNum, conteoPorPrograma, estiloSubtitulo, estiloNormal, estiloTituloPrincipal);

//        // 3. Conteo por Sexo (filtrando nulos)
        Map<String, Long> conteoPorSexo = consultas.stream()
                .filter(c -> c.getPaciente() != null && c.getPaciente().getSexo() != null)
                .collect(Collectors.groupingBy(c -> c.getPaciente().getSexo(), Collectors.counting()));

        rowNum = escribirSeccionSexo(sheet, rowNum, conteoPorSexo, estiloSubtitulo, estiloNormal, estiloTituloPrincipal);

        // 1. Autoajustar columnas
        for (int i = 0; i < 2; i++) {
            sheet.autoSizeColumn(i);
        }

        // 2. FORZAR ANCHO MÍNIMO para evitar que las columnas de números se vean demasiado estrechas.
        // Un valor de 3500 unidades (aproximadamente 13-14 caracteres de ancho) es un buen mínimo para legibilidad.
        final int ANCHO_MINIMO_UNIDADES = 3500;

        // Aplicar el mínimo a las columnas 0 (Categoría) y 1 (Conteo)
        for (int i = 0; i < 2; i++) {
            int anchoActual = sheet.getColumnWidth(i);

            // Si el ancho autoajustado es menor que el mínimo deseado, forzar el mínimo
            if (anchoActual < ANCHO_MINIMO_UNIDADES) {
                sheet.setColumnWidth(i, ANCHO_MINIMO_UNIDADES);
            }
        }
    }

    // Metodo auxiliar para escribir las secciones de causas de diagnostico
    private int escribirSeccionDiagnostico(Sheet sheet, int rowNum, Map<Diagnostico, Long> datos, List<Diagnostico> diagnosticos, CellStyle estiloSubtitulo, CellStyle estiloNormal, CellStyle estiloTitulo) {

        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight(ALTURA_SUBTITULO);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("CAUSAS DE CONSULTA");
        tituloCell.setCellStyle(estiloSubtitulo);
        tituloRow.createCell(1).setCellStyle(estiloTitulo);

        for(Diagnostico diagnostico : diagnosticos){
            Row dataRow = sheet.createRow(rowNum++);

            // Aplicación del ESTILO NORMAL
            Cell diagnosticoCell = dataRow.createCell(0);
            diagnosticoCell.setCellValue(diagnostico.toString());
            diagnosticoCell.setCellStyle(estiloNormal); // APLICADO

            Cell conteoCell = dataRow.createCell(1);
            if(datos.containsKey(diagnostico)){
                conteoCell.setCellValue(datos.get(diagnostico));
            }else{
                conteoCell.setCellValue(0);
            }
            conteoCell.setCellStyle(estiloNormal); // APLICADO
        }


        return rowNum;
    }

    private int escribirSeccionCarreras(Sheet sheet, int rowNum, Map<String, Long> datos, CellStyle estiloSubtitulo, CellStyle estiloNormal, CellStyle estiloTitulo){
        // Aplicación del ESTILO SUBTÍTULO
        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight(ALTURA_SUBTITULO);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("PROGRAMAS ACADEMICOS");
        tituloCell.setCellStyle(estiloSubtitulo); // APLICADO
        tituloCell.setCellStyle(estiloSubtitulo);
        tituloRow.createCell(1).setCellStyle(estiloTitulo);

        for(String programa : programas){
            Row dataRow = sheet.createRow(rowNum++);

            // Aplicación del ESTILO NORMAL
            Cell programaCell = dataRow.createCell(0);
            programaCell.setCellValue(programa);
            programaCell.setCellStyle(estiloNormal); // APLICADO

            Cell conteoCell = dataRow.createCell(1);
            if(datos.containsKey(programa)){
                conteoCell.setCellValue(datos.get(programa));
            }else{
                conteoCell.setCellValue(0);
            }
            conteoCell.setCellStyle(estiloNormal); // APLICADO
        }
        return  rowNum;
    }

    private int escribirSeccionSexo(Sheet sheet, int rowNum, Map<String, Long> datos, CellStyle estiloSubtitulo, CellStyle estiloNormal, CellStyle estiloTitulo){
        // Aplicación del ESTILO SUBTÍTULO
        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight(ALTURA_SUBTITULO);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("GRUPOS (SEXO)");
        tituloCell.setCellStyle(estiloSubtitulo); // APLICADO
        tituloCell.setCellStyle(estiloSubtitulo);
        tituloRow.createCell(1).setCellStyle(estiloTitulo);

        String[] sexos = {"Hombre", "Mujer"};
        for(String sexo : sexos){
            Row dataRow = sheet.createRow(rowNum++);

            // Aplicación del ESTILO NORMAL
            Cell sexoCell = dataRow.createCell(0);
            sexoCell.setCellValue(sexo);
            sexoCell.setCellStyle(estiloNormal); // APLICADO

            Cell conteoCell = dataRow.createCell(1);
            if(datos.containsKey(sexo)){
                conteoCell.setCellValue(datos.get(sexo));
            }else{
                conteoCell.setCellValue(0);
            }
            conteoCell.setCellStyle(estiloNormal); // APLICADO
        }
        return  rowNum;
    }

    public void generarReporteMensual(int mes, int year){
        // 2. Definir el rango del mes para la consulta a la base de datos
        LocalDate startOfMonth = LocalDate.of(year, mes, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        // Convertir a java.util.Date para tu servicio de consulta (si usa el tipo antiguo)
        java.util.Date inicio = java.util.Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        java.util.Date fin = java.util.Date.from(endOfMonth.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        // 3. Consulta de datos (la consulta debe usar el rango completo del mes)
        var consultas = consultaServicio.buscarPorFecha(inicio, fin);
        var diagnosticos = diagnosticoServicio.listarDiagnosticos();
        // 4. Preparar nombre del archivo
        SimpleDateFormat sdp = new SimpleDateFormat("MMMM-yyyy");
        String nombreArchivo = "EXPELEC reporte_mensual_".concat(sdp.format(inicio));

        try {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar reporte de consultas");
            fileChooser.setSelectedFile(new File(nombreArchivo));

            // Filtro pa’ solo mostrar .xlsx
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));

            int userSelection = fileChooser.showSaveDialog(null);

            String rutaArchivo = "";
            Workbook workbook = null;
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = fileChooser.getSelectedFile();

                // Asegurar que termine con .xlsx
                rutaArchivo = archivoSeleccionado.getAbsolutePath();
                if (!rutaArchivo.toLowerCase().endsWith(".xlsx")) {
                    rutaArchivo += ".xlsx";
                }

                // Creamos el workbook (archivo Excel)
                workbook = new XSSFWorkbook();

                // Generar los reportes
                String titulo = sdp.format(inicio);
                generarReporteConsultas(consultas,diagnosticos,workbook,titulo);
                generarEstadisticasMensuales(workbook,consultas,titulo,startOfMonth, diagnosticos);
            }

            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                assert workbook != null;
                workbook.write(fileOut);
            }
            workbook.close();
            JOptionPane.showMessageDialog(null, "Reporte guardado en:\n" + rutaArchivo);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }

    private void generarEstadisticasMensuales(Workbook workbook, List<Consulta> consultas, String titulo, LocalDate startOfMonth, List<Diagnostico> diagnosticos) {
        Sheet sheet = workbook.createSheet("Estadisticas");

        // 1. ESTILO PARA EL TÍTULO PRINCIPAL (Fondo Oscuro, Letra Blanca, 24px)
        CellStyle estiloTituloPrincipal = crearEstiloTitulo(workbook);


        // 2. ESTILO PARA SUBTÍTULOS / ENCABEZADOS DE SECCIÓN (Negro, 16px, Fondo Blanco)
        CellStyle estiloSubtitulo = crearEstiloSubtitulo(workbook);


        // 3. ESTILO NORMAL (Letra 14px y Bordes en todas las direcciones)
        CellStyle estiloNormal = crearEstiloNormal(workbook);

        // estilo negro
        CellStyle estiloNegro = crearEstiloNegro(workbook);

        int rowNum = 0;
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue(titulo);
        tituloCell.setCellStyle(estiloTituloPrincipal);
        tituloRow.createCell(1).setCellStyle(estiloTituloPrincipal);
        tituloRow.setHeight(ALTURA_TITULO_PRINCIPAL);

        // Semanas
        Row semanasRow = sheet.createRow(rowNum++);
        semanasRow.setHeight(ALTURA_SUBTITULO);
        Cell subtituloCell = semanasRow.createCell(0);
        subtituloCell.setCellValue("SEMANAS");
        subtituloCell.setCellStyle(estiloSubtitulo);
        Cell totalConsultas = semanasRow.createCell(1);
        totalConsultas.setCellValue(consultas.size());
        totalConsultas.setCellStyle(estiloNormal);

        var consultasSemanas = agruparConsultasPorSemana(consultas, startOfMonth);
        for(int i=1;i<=consultasSemanas.size();i++){
            Cell semana = semanasRow.createCell(i);
            Cell tituloNegro = tituloRow.createCell(i);
            semana.setCellValue("Semana ".concat(String.valueOf(i)));
            semana.setCellStyle(estiloSubtitulo);
            tituloNegro.setCellStyle(estiloNegro);
        }
        Cell totalTituloCell = semanasRow.createCell(consultasSemanas.size()+1);
        totalTituloCell.setCellValue("Total");
        totalTituloCell.setCellStyle(estiloSubtitulo);
        tituloRow.createCell(consultasSemanas.size()+1).setCellStyle(estiloNegro);

        // totales
        Row totalesRow = sheet.createRow(rowNum++);
        totalesRow.setHeight(ALTURA_SUBTITULO);
        Cell totalesCell = totalesRow.createCell(0);
        totalesCell.setCellValue("TOTAL");
        totalesCell.setCellStyle(estiloSubtitulo);

        for(int i=1;i<=consultasSemanas.size();i++){
            Cell semana = totalesRow.createCell(i);
            semana.setCellValue(consultasSemanas.get("Semana "+i).size());
            semana.setCellStyle(estiloNormal);
        }
        Cell totalConsultasCell = totalesRow.createCell(consultasSemanas.size()+1);
        totalConsultasCell.setCellValue(consultas.size());
        totalConsultasCell.setCellStyle(estiloNormal);



        //Inician las impresiones

        rowNum = escribirSeccionDiagnosticoMensual(
                sheet,
                rowNum,
                diagnosticos, // Lista de Diagnosticos Maestra
                consultasSemanas,
                estiloSubtitulo,
                estiloNormal,
                estiloNegro
        );

        rowNum = escribirSeccionCarrerasMensual(
                sheet,
                rowNum,
                consultasSemanas,
                estiloSubtitulo,
                estiloNormal,
                estiloNegro
        );

        rowNum = escribirSeccionSexoMensual(sheet,rowNum,consultasSemanas,estiloSubtitulo,estiloNormal,estiloNegro);

        // 1. Autoajustar columnas
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }

        // 2. FORZAR ANCHO MÍNIMO para evitar que las columnas de números se vean demasiado estrechas.
        // Un valor de 3500 unidades (aproximadamente 13-14 caracteres de ancho) es un buen mínimo para legibilidad.
        final int ANCHO_MINIMO_UNIDADES = 3500;

        // Aplicar el mínimo a las columnas 0 (Categoría) y 1 (Conteo)
        for (int i = 0; i < 7; i++) {
            int anchoActual = sheet.getColumnWidth(i);

            // Si el ancho autoajustado es menor que el mínimo deseado, forzar el mínimo
            if (anchoActual < ANCHO_MINIMO_UNIDADES) {
                sheet.setColumnWidth(i, ANCHO_MINIMO_UNIDADES);
            }
        }

        // =========================================================
        // CREACIÓN DE GRÁFICA DE BARRAS: TOTAL DE CONSULTAS POR MOTIVO
        // =========================================================

        if (!diagnosticos.isEmpty()) {
            int semanasCount = consultasSemanas.size();

            int tituloDiagnosticoRow = 3;
            int firstDataRow = tituloDiagnosticoRow + 1; // fila TOTAL
            int lastDataRow = firstDataRow + diagnosticos.size(); // TOTAL + todas las causas

            int categoriaCol = 0;
            int totalCol = 1 + semanasCount;

            if (sheet instanceof XSSFSheet xssfSheet) {
                XSSFDrawing drawing = xssfSheet.createDrawingPatriarch();
                CreationHelper helper = workbook.getCreationHelper();

                ClientAnchor anchor = helper.createClientAnchor();
                // Columna H es índice 7 (A=0, B=1, ... H=7)
                anchor.setCol1(7);
                // Colocamos la grafica principal a partir de la fila 2 (indice 1)
                anchor.setRow1(1);
                // Ancho aproximado: hasta la columna R (índice 17)
                anchor.setCol2(17);
                // Alto: unas 18 filas de alto aprox (hasta la fila 20, indice 19)
                anchor.setRow2(19);

                XSSFChart chart = drawing.createChart(anchor);
                chart.setTitleText("Consultas por motivo");
                chart.setTitleOverlay(false);

                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.BOTTOM);

                XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
                bottomAxis.setTitle("Motivo de consulta");
                XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
                leftAxis.setTitle("Total de consultas");
                leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

                XDDFDataSource<String> categorias = XDDFDataSourcesFactory.fromStringCellRange(
                        xssfSheet,
                        new CellRangeAddress(firstDataRow, lastDataRow, categoriaCol, categoriaCol)
                );

                XDDFNumericalDataSource<Double> valores = XDDFDataSourcesFactory.fromNumericCellRange(
                        xssfSheet,
                        new CellRangeAddress(firstDataRow, lastDataRow, totalCol, totalCol)
                );

                XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
                data.setBarDirection(BarDirection.COL);

                XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(categorias, valores);
                series.setTitle("Consultas por motivo", null);

                chart.plot(data);

                // =========================================================
                // GRÁFICAS ADICIONALES: POR SEMANA (HASTA 4 SEMANAS)
                // =========================================================

                int maxSemanasGraficas = Math.min(4, semanasCount);
                int chartHeight = 16; // alto aproximado en filas

                for (int i = 0; i < maxSemanasGraficas; i++) {
                    int weekCol = 1 + i; // columna de la semana i (1 = Semana 1, 2 = Semana 2, ...)

                    ClientAnchor anchorSemana = helper.createClientAnchor();
                    anchorSemana.setCol1(7);
                    // ubicamos cada grafica semanal debajo de la grafica mensual, una debajo de otra
                    int baseRow = 21 + i * (chartHeight + 2); // empieza debajo de la principal
                    anchorSemana.setRow1(baseRow);
                    // hasta columna R (índice 17)
                    anchorSemana.setCol2(17);
                    anchorSemana.setRow2(baseRow + chartHeight);

                    XSSFChart chartSemana = drawing.createChart(anchorSemana);
                    chartSemana.setTitleText("Consultas Semana " + (i + 1));
                    chartSemana.setTitleOverlay(false);

                    XDDFChartLegend legendSemana = chartSemana.getOrAddLegend();
                    legendSemana.setPosition(LegendPosition.BOTTOM);

                    XDDFCategoryAxis bottomAxisSemana = chartSemana.createCategoryAxis(AxisPosition.BOTTOM);
                    bottomAxisSemana.setTitle("Motivo de consulta");
                    XDDFValueAxis leftAxisSemana = chartSemana.createValueAxis(AxisPosition.LEFT);
                    leftAxisSemana.setTitle("Total de consultas");
                    leftAxisSemana.setCrosses(AxisCrosses.AUTO_ZERO);

                    XDDFDataSource<String> categoriasSemana = XDDFDataSourcesFactory.fromStringCellRange(
                            xssfSheet,
                            new CellRangeAddress(firstDataRow, lastDataRow, categoriaCol, categoriaCol)
                    );

                    XDDFNumericalDataSource<Double> valoresSemana = XDDFDataSourcesFactory.fromNumericCellRange(
                            xssfSheet,
                            new CellRangeAddress(firstDataRow, lastDataRow, weekCol, weekCol)
                    );

                    XDDFBarChartData dataSemana = (XDDFBarChartData) chartSemana.createData(ChartTypes.BAR, bottomAxisSemana, leftAxisSemana);
                    dataSemana.setBarDirection(BarDirection.COL);

                    XDDFBarChartData.Series seriesSemana = (XDDFBarChartData.Series) dataSemana.addSeries(categoriasSemana, valoresSemana);
                    seriesSemana.setTitle("Consultas Semana " + (i + 1), null);

                    chartSemana.plot(dataSemana);
                }
            }
        }

    }


    private Map<String, List<Consulta>> agruparConsultasPorSemana(List<Consulta> consultas, LocalDate startOfMonth) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int firstWeekNum = startOfMonth.get(weekFields.weekOfMonth());

        // 1. Agrupación inicial: Solo crea entradas para las semanas CON datos.
        Map<String, List<Consulta>> consultasAgrupadas = consultas.stream()
                .filter(c -> c.getFechaReg() != null)
                .collect(Collectors.groupingBy(consulta -> {
                    LocalDate fecha = consulta.getFechaReg().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int weekNum = fecha.get(weekFields.weekOfMonth());
                    int semanaAjustada = weekNum - firstWeekNum + 1;
                    return "Semana " + semanaAjustada;
                }));

        // 2. Determinar la CANTIDAD TOTAL de semanas que tiene el mes.
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        int lastWeekNum = endOfMonth.get(weekFields.weekOfMonth());
        int totalSemanasDelMes = lastWeekNum - firstWeekNum + 1;

        // 3. Post-verificación e inyección de semanas vacías.
        for (int i = 1; i <= totalSemanasDelMes; i++) {
            String claveSemana = "Semana " + i;

            // Si el mapa NO contiene la clave, significa que no hubo registros.
            // La añadimos con una lista vacía (conteo = 0).
            consultasAgrupadas.putIfAbsent(claveSemana, new ArrayList<>());
        }

        return consultasAgrupadas;
    }

    private int escribirSeccionDiagnosticoMensual(
            Sheet sheet,
            int rowNum,
            List<Diagnostico> diagnosticosMaestros, // Lista completa de todos los diagnósticos (para filas con cero)
            Map<String, List<Consulta>> consultasPorSemana, // Datos agrupados por "Semana 1", "Semana 2", etc.
            CellStyle estiloSubtitulo,
            CellStyle estiloNormal,
            CellStyle estiloNegro
    ) {

        // --- 1. PREPARACIÓN DE LA ESTRUCTURA DE LA TABLA ---

        // Lista ordenada de claves de semanas (ej: ["Semana 1", "Semana 2", ...])
        List<String> semanas = new ArrayList<>(consultasPorSemana.keySet());
        Collections.sort(semanas);
        int totalColumns = 1 + semanas.size() + 1; // Columna Categoría + Semanas + Total

        // --- 2. TÍTULO DE LA SECCIÓN ---
        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight(ALTURA_SUBTITULO);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("CAUSAS DE CONSULTAS");
        tituloCell.setCellStyle(estiloSubtitulo);
        for(int i=1;i<=consultasPorSemana.size()+1;i++){
            tituloRow.createCell(i).setCellStyle(estiloNegro);
        }


        // --- 3. FILA TOTAL GLOBAL (PRIMERA FILA DE DATOS) ---

        long totalGlobal = consultasPorSemana.values()
                .stream()
                .mapToLong(List::size)
                .sum();

        Row totalRow = sheet.createRow(rowNum++);

        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL");
        totalLabelCell.setCellStyle(estiloSubtitulo);

        // Totales por semana en la fila TOTAL
        for (int i = 0; i < semanas.size(); i++) {
            String nombreSemana = semanas.get(i);
            long totalSemana = consultasPorSemana.getOrDefault(nombreSemana, Collections.emptyList()).size();

            Cell celdaSemana = totalRow.createCell(i + 1);
            celdaSemana.setCellValue(totalSemana);
            celdaSemana.setCellStyle(estiloNormal);
        }

        Cell totalGlobalCell = totalRow.createCell(totalColumns - 1);
        totalGlobalCell.setCellValue(totalGlobal);
        totalGlobalCell.setCellStyle(estiloNormal);


        // --- 4. AGRUPACIÓN Y CONTEO POR SEMANA ---

        // Mapa: Diagnóstico (String) -> Semana (String) -> Conteo (Long)
        Map<String, Map<String, Long>> resumenPorDiagnostico = new HashMap<>();

        for (Map.Entry<String, List<Consulta>> entradaSemana : consultasPorSemana.entrySet()) {
            String nombreSemana = entradaSemana.getKey();

            // Agrupar las consultas de ESA semana por el diagnóstico
            Map<String, Long> conteoSemana = entradaSemana.getValue().stream()
                    .filter(c -> c.getDiagnosticoKey() != null)
                    .collect(Collectors.groupingBy(c -> c.getDiagnosticoKey().toString(), Collectors.counting()));

            // Integrar el conteo al mapa de resumen
            for (Map.Entry<String, Long> entradaConteo : conteoSemana.entrySet()) {
                String categoria = entradaConteo.getKey();
                long conteo = entradaConteo.getValue();

                resumenPorDiagnostico.putIfAbsent(categoria, new HashMap<>());
                resumenPorDiagnostico.get(categoria).put(nombreSemana, conteo);
            }
        }

        // --- 5. ESCRITURA DE DATOS FILA POR FILA (POR DIAGNÓSTICO) ---

        // La iteración debe hacerse sobre la lista maestra para asegurar el orden y las filas con conteo cero.
        for (Diagnostico diagnostico : diagnosticosMaestros) {
            String nombreDiagnostico = diagnostico.toString();
            Row dataRow = sheet.createRow(rowNum++);

            // Columna 0: Nombre del Diagnóstico
            Cell categoriaCell = dataRow.createCell(0);
            categoriaCell.setCellValue(nombreDiagnostico);
            categoriaCell.setCellStyle(estiloNormal);

            long totalFila = 0;

            // Columnas 1 hasta N: Conteo por Semana
            for (int i = 0; i < semanas.size(); i++) {
                String nombreSemana = semanas.get(i);

                // Obtener el conteo: si no existe en el mapa de resumen, es 0
                long conteo = resumenPorDiagnostico
                        .getOrDefault(nombreDiagnostico, Collections.emptyMap())
                        .getOrDefault(nombreSemana, 0L);

                totalFila += conteo;

                Cell conteoCell = dataRow.createCell(i + 1);
                conteoCell.setCellValue(conteo);
                conteoCell.setCellStyle(estiloNormal);
            }

            // Última Columna: Total de la Fila (Diagnóstico)
            Cell totalCell = dataRow.createCell(totalColumns - 1);
            totalCell.setCellValue(totalFila);
            totalCell.setCellStyle(estiloNormal);
        }
        return rowNum;
    }

    private int escribirSeccionCarrerasMensual(
            Sheet sheet,
            int rowNum,
            Map<String, List<Consulta>> consultasPorSemana, // Datos agrupados por "Semana 1", "Semana 2", etc.
            CellStyle estiloSubtitulo,
            CellStyle estiloNormal,
            CellStyle estiloNegro
    ) {

        // --- 1. PREPARACIÓN DE LA ESTRUCTURA DE LA TABLA ---

        // Lista ordenada de claves de semanas (ej: ["Semana 1", "Semana 2", ...])
        List<String> semanas = new ArrayList<>(consultasPorSemana.keySet());
        Collections.sort(semanas);
        int totalColumns = 1 + semanas.size() + 1; // Columna Categoría + Semanas + Total

        // --- 2. TÍTULO DE LA SECCIÓN ---
        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight(ALTURA_SUBTITULO);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("PROGRAMAS ACADEMICOS");
        tituloCell.setCellStyle(estiloSubtitulo);
        for(int i=1;i<=consultasPorSemana.size()+1;i++){
            tituloRow.createCell(i).setCellStyle(estiloNegro);
        }

        // --- 3. AGRUPACIÓN Y CONTEO POR SEMANA ---

        // Mapa: Programa (String) -> Semana (String) -> Conteo (Long)
        Map<String, Map<String, Long>> resumenPorPrograma = new HashMap<>();

        for (Map.Entry<String, List<Consulta>> entradaSemana : consultasPorSemana.entrySet()) {
            String nombreSemana = entradaSemana.getKey();

            // Agrupar las consultas de ESA semana por el Programa Académico
            Map<String, Long> conteoSemana = entradaSemana.getValue().stream()
                    .filter(c -> c.getPaciente() != null && c.getPaciente().getProgramaAcademico() != null)
                    .collect(Collectors.groupingBy(c -> c.getPaciente().getProgramaAcademico(), Collectors.counting()));

            // Integrar el conteo al mapa de resumen
            for (Map.Entry<String, Long> entradaConteo : conteoSemana.entrySet()) {
                String categoria = entradaConteo.getKey();
                long conteo = entradaConteo.getValue();

                resumenPorPrograma.putIfAbsent(categoria, new HashMap<>());
                resumenPorPrograma.get(categoria).put(nombreSemana, conteo);
            }
        }

        // --- 4. ESCRITURA DE DATOS FILA POR FILA ---

        // Iteramos sobre la lista maestra para asegurar el orden y las filas con conteo cero.
        for (String programa : programas) {
            Row dataRow = sheet.createRow(rowNum++);

            // Columna 0: Nombre del Programa
            Cell programaCell = dataRow.createCell(0);
            programaCell.setCellValue(programa);
            programaCell.setCellStyle(estiloNormal);

            long totalFila = 0;

            // Columnas 1 hasta N: Conteo por Semana
            for (int i = 0; i < semanas.size(); i++) {
                String nombreSemana = semanas.get(i);

                // Obtener el conteo: si no existe en el mapa de resumen, es 0
                long conteo = resumenPorPrograma
                        .getOrDefault(programa, Collections.emptyMap())
                        .getOrDefault(nombreSemana, 0L);

                totalFila += conteo;

                Cell conteoCell = dataRow.createCell(i + 1);
                conteoCell.setCellValue(conteo);
                conteoCell.setCellStyle(estiloNormal);
            }

            // Última Columna: Total de la Fila (Programa)
            Cell totalCell = dataRow.createCell(totalColumns - 1);
            totalCell.setCellValue(totalFila);
            totalCell.setCellStyle(estiloNormal);
        }

        return rowNum;
    }

    private int escribirSeccionSexoMensual(
            Sheet sheet,
            int rowNum,
            Map<String, List<Consulta>> consultasPorSemana, // Datos agrupados por "Semana 1", "Semana 2", etc.
            CellStyle estiloSubtitulo,
            CellStyle estiloNormal,
            CellStyle estiloNegro
    ) {

        // --- 1. PREPARACIÓN DE LA ESTRUCTURA DE LA TABLA ---

        // Lista ordenada de claves de semanas (ej: ["Semana 1", "Semana 2", ...])
        List<String> semanas = new ArrayList<>(consultasPorSemana.keySet());
        Collections.sort(semanas);
        int totalColumns = 1 + semanas.size() + 1; // Columna Categoría + Semanas + Total

        // Lista maestra de categorías a iterar
        List<String> sexosMaestros = List.of("Hombre", "Mujer");

        // --- 2. TÍTULO DE LA SECCIÓN ---
        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight(ALTURA_SUBTITULO);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("GRUPOS (SEXO)");
        tituloCell.setCellStyle(estiloSubtitulo);
        for(int i=1;i<=consultasPorSemana.size()+1;i++){
            tituloRow.createCell(i).setCellStyle(estiloNegro);
        }



        // --- 3. AGRUPACIÓN Y CONTEO POR SEMANA ---

        // Mapa: Sexo (String) -> Semana (String) -> Conteo (Long)
        Map<String, Map<String, Long>> resumenPorSexo = new HashMap<>();

        for (Map.Entry<String, List<Consulta>> entradaSemana : consultasPorSemana.entrySet()) {
            String nombreSemana = entradaSemana.getKey();

            // Agrupar las consultas de ESA semana por el Sexo
            Map<String, Long> conteoSemana = entradaSemana.getValue().stream()
                    .filter(c -> c.getPaciente() != null && c.getPaciente().getSexo() != null)
                    .collect(Collectors.groupingBy(c -> c.getPaciente().getSexo(), Collectors.counting()));

            // Integrar el conteo al mapa de resumen
            for (Map.Entry<String, Long> entradaConteo : conteoSemana.entrySet()) {
                String categoria = entradaConteo.getKey();
                long conteo = entradaConteo.getValue();

                resumenPorSexo.putIfAbsent(categoria, new HashMap<>());
                resumenPorSexo.get(categoria).put(nombreSemana, conteo);
            }
        }

        // --- 4. ESCRITURA DE DATOS FILA POR FILA ---

        // Iteramos sobre la lista maestra ("Hombre", "Mujer").
        for (String sexo : sexosMaestros) {
            Row dataRow = sheet.createRow(rowNum++);

            // Columna 0: Nombre del Sexo
            Cell sexoCell = dataRow.createCell(0);
            sexoCell.setCellValue(sexo);
            sexoCell.setCellStyle(estiloNormal);

            long totalFila = 0;

            // Columnas 1 hasta N: Conteo por Semana
            for (int i = 0; i < semanas.size(); i++) {
                String nombreSemana = semanas.get(i);

                // Obtener el conteo: si no existe en el mapa de resumen, es 0
                long conteo = resumenPorSexo
                        .getOrDefault(sexo, Collections.emptyMap())
                        .getOrDefault(nombreSemana, 0L);

                totalFila += conteo;

                Cell conteoCell = dataRow.createCell(i + 1);
                conteoCell.setCellValue(conteo);
                conteoCell.setCellStyle(estiloNormal);
            }

            // Última Columna: Total de la Fila (Sexo)
            Cell totalCell = dataRow.createCell(totalColumns - 1);
            totalCell.setCellValue(totalFila);
            totalCell.setCellStyle(estiloNormal);
        }

        return rowNum;
    }

    public void generarReporteAnual(int year){
        LocalDate startOfYear = LocalDate.of(year,1,1);
        LocalDate endOfYear = startOfYear.with(TemporalAdjusters.lastDayOfYear());

        //transformar a date
        Date inicio = Date.from(startOfYear.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fin = Date.from(endOfYear.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        var consultas = consultaServicio.buscarPorFecha(inicio,fin);
        var diagnosticos = diagnosticoServicio.listarDiagnosticos();
        SimpleDateFormat sdp = new SimpleDateFormat("yyyy");
        String nombreArchivo = "EXPELEC reporte_anual-".concat(sdp.format(inicio));

        try {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar reporte de consultas");
            fileChooser.setSelectedFile(new File(nombreArchivo));

            // Filtro pa’ solo mostrar .xlsx
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));

            int userSelection = fileChooser.showSaveDialog(null);

            String rutaArchivo = "";
            Workbook workbook = null;
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = fileChooser.getSelectedFile();

                // Asegurar que termine con .xlsx
                rutaArchivo = archivoSeleccionado.getAbsolutePath();
                if (!rutaArchivo.toLowerCase().endsWith(".xlsx")) {
                    rutaArchivo += ".xlsx";
                }

                // Creamos el workbook (archivo Excel)
                workbook = new XSSFWorkbook();

                // Generar los reportes
                String titulo = sdp.format(inicio);
                generarReporteConsultas(consultas,diagnosticos,workbook,titulo);
                generarEstadisticasAnuales(workbook,consultas,diagnosticos,titulo);
            }

            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                assert workbook != null;
                workbook.write(fileOut);
            }
            workbook.close();
            JOptionPane.showMessageDialog(null, "Reporte guardado en:\n" + rutaArchivo);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }


    private Map<Integer, List<Consulta>> agruparConsultasPorMes(List<Consulta> consultas){

        Map<Integer, List<Consulta>> consultasMensuales = consultas.stream()
                .filter(c -> c.getFechaReg() != null)
                .collect(Collectors.groupingBy(consulta -> {
                    LocalDate fecha = consulta.getFechaReg().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int mesNum = fecha.getMonth().getValue();
                    return mesNum;
                }));


        // inyeccion de meses vacios
        for (int i = 1; i <= 12; i++) {
            // Si el mapa NO contiene la clave, significa que no hubo registros.
            // La añadimos con una lista vacía (conteo = 0).
            consultasMensuales.putIfAbsent(i, new ArrayList<>());
        }

        return consultasMensuales;
    }

    private void generarEstadisticasAnuales(Workbook workbook, List<Consulta> consultas, List<Diagnostico> diagnosticos, String titulo){
        Sheet sheet = workbook.createSheet("Estadisticas");

        // 1. ESTILO PARA EL TÍTULO PRINCIPAL (Fondo Oscuro, Letra Blanca, 24px)
        CellStyle estiloTituloPrincipal = crearEstiloTitulo(workbook);


        // 2. ESTILO PARA SUBTÍTULOS / ENCABEZADOS DE SECCIÓN (Negro, 16px, Fondo Blanco)
        CellStyle estiloSubtitulo = crearEstiloSubtitulo(workbook);


        // 3. ESTILO NORMAL (Letra 14px y Bordes en todas las direcciones)
        CellStyle estiloNormal = crearEstiloNormal(workbook);

        // estilo negro
        CellStyle estiloNegro = crearEstiloNegro(workbook);

        int rowNum = 0;
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue(titulo);
        tituloCell.setCellStyle(estiloTituloPrincipal);
        tituloRow.createCell(1).setCellStyle(estiloTituloPrincipal);
        tituloRow.setHeight(ALTURA_TITULO_PRINCIPAL);

        // Semanas
        Row mesesRow = sheet.createRow(rowNum++);
        mesesRow.setHeight(ALTURA_SUBTITULO);
        Cell subtituloCell = mesesRow.createCell(0);
        subtituloCell.setCellValue("MES");
        subtituloCell.setCellStyle(estiloSubtitulo);
        Cell totalConsultas = mesesRow.createCell(1);
        totalConsultas.setCellValue(consultas.size());
        totalConsultas.setCellStyle(estiloNormal);

        String[] meses = {"","Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Nomviembre","Diciembre"};
        var consultasMensuales = agruparConsultasPorMes(consultas);
        for(int i=1;i<=consultasMensuales.size();i++){
            Cell mes = mesesRow.createCell(i);
            Cell tituloNegro = tituloRow.createCell(i);
            mes.setCellValue(meses[i]);
            mes.setCellStyle(estiloSubtitulo);
            tituloNegro.setCellStyle(estiloNegro);
        }
        Cell totalTituloCell = mesesRow.createCell(consultasMensuales.size()+1);
        totalTituloCell.setCellValue("Total");
        totalTituloCell.setCellStyle(estiloSubtitulo);
        tituloRow.createCell(consultasMensuales.size()+1).setCellStyle(estiloNegro);

        // totales
        Row totalesRow = sheet.createRow(rowNum++);
        totalesRow.setHeight(ALTURA_SUBTITULO);
        Cell totalesCell = totalesRow.createCell(0);
        totalesCell.setCellValue("TOTAL");
        totalesCell.setCellStyle(estiloSubtitulo);

        for(int i=1;i<=consultasMensuales.size();i++){
            Cell semana = totalesRow.createCell(i);
            semana.setCellValue(consultasMensuales.get(i).size());
            semana.setCellStyle(estiloNormal);
        }
        Cell totalConsultasCell = totalesRow.createCell(consultasMensuales.size()+1);
        totalConsultasCell.setCellValue(consultas.size());
        totalConsultasCell.setCellStyle(estiloNormal);

        // relleno de datos
        rowNum = escribirDiagnosticoAnual(sheet,rowNum,diagnosticos,consultasMensuales,estiloSubtitulo,estiloNormal,estiloNegro);

        rowNum = escribirCarrerasAnual(sheet,rowNum,consultasMensuales,estiloSubtitulo,estiloNormal,estiloNegro);

        rowNum = escribirSexoAnual(sheet,rowNum,consultasMensuales,estiloSubtitulo,estiloNormal,estiloNegro);


        // 1. Autoajustar columnas
        for (int i = 0; i < 14; i++) {
            sheet.autoSizeColumn(i);
        }

        // 2. FORZAR ANCHO MÍNIMO para evitar que las columnas de números se vean demasiado estrechas.
        // Un valor de 3500 unidades (aproximadamente 13-14 caracteres de ancho) es un buen mínimo para legibilidad.
        final int ANCHO_MINIMO_UNIDADES = 3500;

        // Aplicar el mínimo a las columnas 0 (Categoría) y 1 (Conteo)
        for (int i = 0; i < 14; i++) {
            int anchoActual = sheet.getColumnWidth(i);

            // Si el ancho autoajustado es menor que el mínimo deseado, forzar el mínimo
            if (anchoActual < ANCHO_MINIMO_UNIDADES) {
                sheet.setColumnWidth(i, ANCHO_MINIMO_UNIDADES);
            }
        }
    }

    private int escribirDiagnosticoAnual(Sheet sheet, int rowNum, List<Diagnostico> diagnosticosMaestros, Map<Integer, List<Consulta>> consultasMenusuales, CellStyle estiloSubtitulo, CellStyle estiloNormal, CellStyle estiloNegro){
        // --- 2. TÍTULO DE LA SECCIÓN ---
        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight(ALTURA_SUBTITULO);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("CAUSAS DE CONSULTAS");
        tituloCell.setCellStyle(estiloSubtitulo);
        for(int i=1;i<=consultasMenusuales.size()+1;i++){
            tituloRow.createCell(i).setCellStyle(estiloNegro);
        }

        Map<String, Map<Integer, Long>> resumenPorDiagnostico = new HashMap<>();

        for (Map.Entry<Integer, List<Consulta>> entradaMes : consultasMenusuales.entrySet()) {
            Integer mes = entradaMes.getKey();

            // Agrupar las consultas de ESA semana por el diagnóstico
            Map<String, Long> conteoMes = entradaMes.getValue().stream()
                    .filter(c -> c.getDiagnosticoKey() != null)
                    .collect(Collectors.groupingBy(c -> c.getDiagnosticoKey().toString(), Collectors.counting()));

            // Integrar el conteo al mapa de resumen
            for (Map.Entry<String, Long> entradaConteo : conteoMes.entrySet()) {
                String categoria = entradaConteo.getKey();
                long conteo = entradaConteo.getValue();

                resumenPorDiagnostico.putIfAbsent(categoria, new HashMap<>());
                resumenPorDiagnostico.get(categoria).put(mes, conteo);
            }
        }

        for (Diagnostico diagnostico : diagnosticosMaestros) {
            String nombreDiagnostico = diagnostico.toString();
            Row dataRow = sheet.createRow(rowNum++);

            // Columna 0: Nombre del Diagnóstico
            Cell categoriaCell = dataRow.createCell(0);
            categoriaCell.setCellValue(nombreDiagnostico);
            categoriaCell.setCellStyle(estiloNormal);

            long totalFila = 0;

            // Columnas 1 hasta N: Conteo por Semana
            for (int i = 1; i <= 12; i++) {

                // Obtener el conteo: si no existe en el mapa de resumen, es 0
                long conteo = resumenPorDiagnostico
                        .getOrDefault(nombreDiagnostico, Collections.emptyMap())
                        .getOrDefault(i, 0L);

                totalFila += conteo;

                Cell conteoCell = dataRow.createCell(i);
                conteoCell.setCellValue(conteo);
                conteoCell.setCellStyle(estiloNormal);
            }

            // Última Columna: Total de la Fila (Diagnóstico)
            Cell totalCell = dataRow.createCell(13);
            totalCell.setCellValue(totalFila);
            totalCell.setCellStyle(estiloNormal);
        }
        return rowNum;

    }

    private int escribirCarrerasAnual(Sheet sheet, int rowNum, Map<Integer, List<Consulta>> consultasMensuales,CellStyle estiloSubtitulo, CellStyle estiloNormal, CellStyle estiloNegro){

        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight(ALTURA_SUBTITULO);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("PROGRAMAS ACADEMICOS");
        tituloCell.setCellStyle(estiloSubtitulo);
        for(int i=1;i<= 13 ;i++){
            tituloRow.createCell(i).setCellStyle(estiloNegro);
        }

        Map<String, Map<Integer, Long>> resumenPorPrograma = new HashMap<>();

        for (Map.Entry<Integer, List<Consulta>> entradaMes : consultasMensuales.entrySet()) {
            int mes = entradaMes.getKey();


            Map<String, Long> conteoSemana = entradaMes.getValue().stream()
                    .filter(c -> c.getPaciente() != null && c.getPaciente().getProgramaAcademico() != null)
                    .collect(Collectors.groupingBy(c -> c.getPaciente().getProgramaAcademico(), Collectors.counting()));

            // Integrar el conteo al mapa de resumen
            for (Map.Entry<String, Long> entradaConteo : conteoSemana.entrySet()) {
                String categoria = entradaConteo.getKey();
                long conteo = entradaConteo.getValue();

                resumenPorPrograma.putIfAbsent(categoria, new HashMap<>());
                resumenPorPrograma.get(categoria).put(mes, conteo);
            }
        }

        // --- 4. ESCRITURA DE DATOS FILA POR FILA ---

        // Iteramos sobre la lista maestra para asegurar el orden y las filas con conteo cero.
        for (String programa : programas) {
            Row dataRow = sheet.createRow(rowNum++);

            // Columna 0: Nombre del Programa
            Cell programaCell = dataRow.createCell(0);
            programaCell.setCellValue(programa);
            programaCell.setCellStyle(estiloNormal);

            long totalFila = 0;

            // Columnas 1 hasta N: Conteo por Semana
            for (int i = 1; i <= 12; i++) {

                // Obtener el conteo: si no existe en el mapa de resumen, es 0
                long conteo = resumenPorPrograma
                        .getOrDefault(programa, Collections.emptyMap())
                        .getOrDefault(i, 0L);

                totalFila += conteo;

                Cell conteoCell = dataRow.createCell(i);
                conteoCell.setCellValue(conteo);
                conteoCell.setCellStyle(estiloNormal);
            }

            // Última Columna: Total de la Fila (Programa)
            Cell totalCell = dataRow.createCell(13);
            totalCell.setCellValue(totalFila);
            totalCell.setCellStyle(estiloNormal);
        }

        return rowNum;
    }

    private int escribirSexoAnual(Sheet sheet, int rowNum, Map<Integer,List<Consulta>> consultasMensuales,CellStyle estiloSubtitulo, CellStyle estiloNormal, CellStyle estiloNegro){
        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight(ALTURA_SUBTITULO);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("GRUPOS (SEXO)");
        tituloCell.setCellStyle(estiloSubtitulo);
        for(int i=1;i<= 13 ;i++){
            tituloRow.createCell(i).setCellStyle(estiloNegro);
        }

        List<String> sexosMaestros = List.of("Hombre", "Mujer");
        Map<String, Map<Integer, Long>> resumenPorSexo = new HashMap<>();

        for (Map.Entry<Integer, List<Consulta>> entradaMes : consultasMensuales.entrySet()) {
            int mes = entradaMes.getKey();


            Map<String, Long> conteoSemana = entradaMes.getValue().stream()
                    .filter(c -> c.getPaciente() != null && c.getPaciente().getSexo() != null)
                    .collect(Collectors.groupingBy(c -> c.getPaciente().getSexo(), Collectors.counting()));

            // Integrar el conteo al mapa de resumen
            for (Map.Entry<String, Long> entradaConteo : conteoSemana.entrySet()) {
                String categoria = entradaConteo.getKey();
                long conteo = entradaConteo.getValue();

                resumenPorSexo.putIfAbsent(categoria, new HashMap<>());
                resumenPorSexo.get(categoria).put(mes, conteo);
            }
        }

        // --- 4. ESCRITURA DE DATOS FILA POR FILA ---

        // Iteramos sobre la lista maestra para asegurar el orden y las filas con conteo cero.
        for (String sexo : sexosMaestros) {
            Row dataRow = sheet.createRow(rowNum++);

            // Columna 0: Nombre del Sexo
            Cell sexoCell = dataRow.createCell(0);
            sexoCell.setCellValue(sexo);
            sexoCell.setCellStyle(estiloNormal);

            long totalFila = 0;

            // Columnas 1 hasta N: Conteo por Semana
            for (int i = 1; i <= 12; i++) {

                // Obtener el conteo: si no existe en el mapa de resumen, es 0
                long conteo = resumenPorSexo
                        .getOrDefault(sexo, Collections.emptyMap())
                        .getOrDefault(i, 0L);

                totalFila += conteo;

                Cell conteoCell = dataRow.createCell(i);
                conteoCell.setCellValue(conteo);
                conteoCell.setCellStyle(estiloNormal);
            }

            // Última Columna: Total de la Fila (Sexo)
            Cell totalCell = dataRow.createCell(13);
            totalCell.setCellValue(totalFila);
            totalCell.setCellStyle(estiloNormal);
        }

        return rowNum;
    }

    private CellStyle crearEstiloTitulo(Workbook workbook){
        CellStyle estiloTituloPrincipal = workbook.createCellStyle();
        Font fontTitulo = workbook.createFont();
        fontTitulo.setBold(true);
        // 24 puntos * 20 twips/punto = 480 (aproximadamente, POI usa 20 para 1pt)
        fontTitulo.setFontHeightInPoints((short) 24); // Usaremos 16pt (equivale a aprox. 24px en pantalla)
        fontTitulo.setColor(IndexedColors.WHITE.getIndex());
        estiloTituloPrincipal.setFont(fontTitulo);
        estiloTituloPrincipal.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        estiloTituloPrincipal.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return  estiloTituloPrincipal;
    }

    private CellStyle crearEstiloSubtitulo(Workbook workbook){
        CellStyle estiloSubtitulo = workbook.createCellStyle();
        Font fontSubtitulo = workbook.createFont();
        fontSubtitulo.setBold(true);
        fontSubtitulo.setFontHeightInPoints((short) 16); // Usaremos 12pt (equivale a aprox. 16px en pantalla)
        fontSubtitulo.setColor(IndexedColors.BLACK.getIndex());
        estiloSubtitulo.setFont(fontSubtitulo);
        // Aplicar bordes al subtítulo
        aplicarBordes(estiloSubtitulo);
        return estiloSubtitulo;
    }

    private CellStyle crearEstiloNormal(Workbook workbook){
        CellStyle estiloNormal = workbook.createCellStyle();
        Font fontNormal = workbook.createFont();
        fontNormal.setFontHeightInPoints((short) 14); // Usaremos 10pt (equivale a aprox. 14px en pantalla)
        estiloNormal.setFont(fontNormal);
        // Aplicar bordes al estilo normal
        aplicarBordes(estiloNormal);
        return  estiloNormal;
    }

    private CellStyle crearEstiloNegro(Workbook workbook){
        CellStyle estiloNegro = workbook.createCellStyle();
        Font fontSubtitulo = workbook.createFont();
        fontSubtitulo.setBold(true);
        fontSubtitulo.setFontHeightInPoints((short) 16); // Usaremos 12pt (equivale a aprox. 16px en pantalla)
        fontSubtitulo.setColor(IndexedColors.BLACK.getIndex());
        estiloNegro.setFont(fontSubtitulo);
        estiloNegro.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        estiloNegro.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return estiloNegro;
    }

    private void aplicarBordes(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    public void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(null, mensaje);
    }

    // Este código debe ir en tu método de inicialización o en el servicio
    public CellStyle crearEstiloNumeroDosDecimales(Workbook workbook, CellStyle estiloBase) {
        CellStyle estiloNumero = workbook.createCellStyle();

        // Copiar las propiedades del estilo base (bordes, fuente, etc.)
        estiloNumero.cloneStyleFrom(estiloBase);

        // Crear el formato de número: "0.00"
        DataFormat format = workbook.createDataFormat();
        estiloNumero.setDataFormat(format.getFormat("0.00"));

        return estiloNumero;
    }

    private void generarResumenPaciente(Workbook workbook, Paciente paciente, int totalConsultas) {
        Sheet sheet = workbook.createSheet("Resumen");

        CellStyle estiloTitulo = crearEstiloTitulo(workbook);
        CellStyle estiloSubtitulo = crearEstiloSubtitulo(workbook);
        CellStyle estiloNormal = crearEstiloNormal(workbook);

        Row tituloRow = sheet.createRow(0);
        tituloRow.setHeight(ALTURA_TITULO_PRINCIPAL);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellStyle(estiloTitulo);
        tituloCell.setCellValue("Histórico del paciente");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        int rowIdx = 2;
        rowIdx = escribirDatoResumen(sheet, rowIdx, "Nombre completo", paciente.toString(), estiloSubtitulo, estiloNormal);
        rowIdx = escribirDatoResumen(sheet, rowIdx, "Matrícula", paciente.getMatricula(), estiloSubtitulo, estiloNormal);
        rowIdx = escribirDatoResumen(sheet, rowIdx, "Programa", paciente.getProgramaAcademico(), estiloSubtitulo, estiloNormal);
        rowIdx = escribirDatoResumen(sheet, rowIdx, "Sexo", paciente.getSexo(), estiloSubtitulo, estiloNormal);

        String edad = paciente.getEdad() != null ? paciente.getEdad().toString() : "No registrado";
        rowIdx = escribirDatoResumen(sheet, rowIdx, "Edad", edad, estiloSubtitulo, estiloNormal);

        escribirDatoResumen(sheet, rowIdx, "Total de consultas registradas", String.valueOf(totalConsultas), estiloSubtitulo, estiloNormal);

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private int escribirDatoResumen(Sheet sheet, int rowIdx, String etiqueta, String valor, CellStyle estiloSubtitulo, CellStyle estiloNormal) {
        Row row = sheet.createRow(rowIdx++);
        row.setHeight(ALTURA_SUBTITULO);
        Cell etiquetaCell = row.createCell(0);
        etiquetaCell.setCellStyle(estiloSubtitulo);
        etiquetaCell.setCellValue(etiqueta);

        Cell valorCell = row.createCell(1);
        valorCell.setCellStyle(estiloNormal);
        valorCell.setCellValue(valor != null ? valor : "No registrado");
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));
        return rowIdx;
    }
}
