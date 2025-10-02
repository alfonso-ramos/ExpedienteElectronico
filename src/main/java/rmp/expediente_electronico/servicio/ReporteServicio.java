package rmp.expediente_electronico.servicio;

import com.toedter.calendar.JDateChooser;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rmp.expediente_electronico.modelo.Consulta;
import rmp.expediente_electronico.modelo.Diagnostico;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
        String nombreArchivo = "reporte ".concat(sdp.format(inicio).concat(" ".concat(sdp.format(fin))));

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
                generarReporteConsultas(consultas, diagosticos, workbook,sdp.format(inicio),sdp.format(fin));
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
            JOptionPane.showMessageDialog(null,"Error al generar el reporte: " + e.getMessage());
        }
    }


    public void generarReporteConsultas(List<Consulta> consultas, List<Diagnostico> diagnosticos, Workbook workbook, String fechaInicio, String fechaFin) throws IOException {

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
        tituloCell.setCellValue("Consultas: ".concat(fechaInicio).concat(" / ".concat(fechaFin)));
        for (int i = 1; i<13;i++){
            tituloRow.createCell(i).setCellStyle(estiloNegro);
        }

        // Encabezados
        String[] columnas = {"Matricula", "Nombres", "Apellidos", "Edad", "Programa academico",
                "Diagnóstico", "Medicamento", "Observaciones", "Fecha", "Altura", "Peso", "IMC", "Estado IMC"};
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

            // Columna 5: Diagnóstico
            Cell c5 = row.createCell(5);
            c5.setCellValue(consulta.getDiagnostico());
            c5.setCellStyle(estiloNormal);

            // Columna 6: Medicamento
            Cell c6 = row.createCell(6);
            c6.setCellValue(consulta.getMedicamento());
            c6.setCellStyle(estiloNormal);

            // Columna 7: Observaciones
            Cell c7 = row.createCell(7);
            c7.setCellValue(consulta.getObservaciones());
            c7.setCellStyle(estiloNormal);

            // Columna 8: Fecha (Texto/String de la Fecha)
            Cell c8 = row.createCell(8);
            c8.setCellValue(consulta.getFechaReg() != null ? consulta.getFechaReg().toString() : "");
            c8.setCellStyle(estiloNormal);


            // ----------------------------------------------------
            // APLICACIÓN DE ESTILO NÚMERO (Float)
            // ----------------------------------------------------

            // Columna 9: Altura
            Cell c9 = row.createCell(9);
            // Usamos setCellValue(double) para que aplique el formato de número
            c9.setCellValue(consulta.getAltura() != null ? consulta.getAltura() : 0f);
            c9.setCellStyle(estiloNumero);

            // Columna 10: Peso
            Cell c10 = row.createCell(10);
            c10.setCellValue(consulta.getPeso() != null ? consulta.getPeso() : 0f);
            c10.setCellStyle(estiloNumero);

            // Columna 11: IMC
            Cell c11 = row.createCell(11);
            c11.setCellValue(consulta.getImc() != null ? consulta.getImc() : 0f);
            c11.setCellStyle(estiloNumero);

            // Columna 12: Estado IMC (texto)
            Cell c12 = row.createCell(12);
            c12.setCellValue(consulta.getImc_estado());
            c12.setCellStyle(estiloNormal);
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


        // 1. Conteo por Diagnóstico
        Map<Diagnostico, Long> conteoPorDiagnostico = consultas.stream()
                .collect(Collectors.groupingBy(Consulta::getDiagnosticoKey, Collectors.counting()));

        rowNum = escribirSeccionDiagnostico(sheet, rowNum, conteoPorDiagnostico, diagnosticos, estiloSubtitulo, estiloNormal, estiloTituloPrincipal);

        // 2. Conteo por Programa Académico
        Map<String, Long> conteoPorPrograma = consultas.stream()
                .collect(Collectors.groupingBy(c -> c.getPaciente().getProgramaAcademico(), Collectors.counting()));

        rowNum = escribirSeccionCarreras(sheet, rowNum, conteoPorPrograma, estiloSubtitulo, estiloNormal, estiloTituloPrincipal);

//        // 3. Conteo por Sexo
        Map<String, Long> conteoPorSexo = consultas.stream()
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
        tituloCell.setCellValue("Causa de consulta");
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
        tituloCell.setCellValue("Programas academicos");
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
        tituloCell.setCellValue("Grupos(sexo)");
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
}
