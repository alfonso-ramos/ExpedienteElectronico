package rmp.expediente_electronico.servicio;

import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
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
import java.text.Normalizer;
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

    private DiagnosticoServicio diagnosticoServicio;

    private List<Diagnostico> diagnosticos;

    String MESES[] = {"Enero", "Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre","Total"};
    String SEMANAS[] = {"Semana 1","Semana 2","Semana 3","Semana 4","Total"};
    String TOTAL_ONLY[] = {"Total"};

    @Autowired
    public ReporteServicio(DiagnosticoServicio diagnosticoServicio){
        this.diagnosticoServicio = diagnosticoServicio;
        this.diagnosticos = diagnosticoServicio.listarDiagnosticos();
    }

    // Altura Titulo Principal (ej: 40 puntos = 800 twips)
    final short ALTURA_TITULO_PRINCIPAL = 800;

    // Altura Subtítulos (ej: 30 puntos = 600 twips)
    final short ALTURA_SUBTITULO = 600;

    public void generarReporte(String nombreArchivo, String titulo, List<Consulta> consultas, Map<String, List<Consulta>> consultasFiltradas, int tipo) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar reporte de consultas");
            fileChooser.setSelectedFile(new File(nombreArchivo));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));

            if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return; // El usuario canceló
            }

            File archivoSeleccionado = fileChooser.getSelectedFile();
            String rutaArchivo = archivoSeleccionado.getAbsolutePath();
            if (!rutaArchivo.toLowerCase().endsWith(".xlsx")) {
                rutaArchivo += ".xlsx";
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                // 1. Hoja de Datos Crudos
                generarReporteConsultas(consultas, workbook, titulo);

                // 2. Hoja de Estadísticas Dinámicas
                generarEstadisticasConsultas(workbook, consultasFiltradas, titulo, tipo);

                // 3. Guardar
                try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                    workbook.write(fileOut);
                }
                JOptionPane.showMessageDialog(null, "Reporte guardado en:\n" + rutaArchivo);
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarMensaje("Error al generar el reporte: " + e.getMessage());
        }
    }

    public void generarReporteAnual(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfYear());

        Date inicio = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fin = Date.from(end.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        var consultas = consultaServicio.buscarPorFecha(inicio, fin);
        var consultasFiltradas = agruparConsultasPorMes(consultas); // Tu función de meses

        String titulo = "Año " + year;
        String nombreArchivo = "EXPELEC_Reporte_Anual_" + year;

        generarReporte(nombreArchivo, titulo, consultas, consultasFiltradas, 1);
    }

    public void generarReporteMensual(int mes, int year) {
        LocalDate start = LocalDate.of(year, mes, 1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());

        Date inicio = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fin = Date.from(end.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        var consultas = consultaServicio.buscarPorFecha(inicio, fin);
        var consultasFiltradas = agruparConsultasPorSemana(consultas, start); // Tu función de semanas

        String titulo = new SimpleDateFormat("MMMM yyyy").format(inicio);
        String nombreArchivo = "EXPELEC_Reporte_Mensual_" + titulo;

        generarReporte(nombreArchivo, titulo, consultas, consultasFiltradas, 2);
    }

    public void generarReporteFecha(Date inicio, Date fin) {
        var consultas = consultaServicio.buscarPorFecha(inicio, fin);

        // Creamos un mapa simple con una sola llave "Total"
        Map<String, List<Consulta>> consultasFiltradas = new HashMap<>();
        consultasFiltradas.put("Total", consultas);

        SimpleDateFormat sdp = new SimpleDateFormat("dd-MM-yyyy");
        String titulo = sdp.format(inicio) + " a " + sdp.format(fin);
        String nombreArchivo = "EXPELEC_Reporte_Rango_" + sdp.format(inicio);

        generarReporte(nombreArchivo, titulo, consultas, consultasFiltradas, 3);
    }

    public void generarReportePaciente(Paciente paciente) {
        if (paciente == null){
            mostrarMensaje("Seleccione un paciente");
            return;
        }

        var consultas = consultaServicio.buscarPorPaciente(paciente);
        if (consultas.isEmpty()) {
            mostrarMensaje("El paciente no tiene consultas.");
            return;
        }

        String nombreArchivo = "Historial_" + paciente.getMatricula();
        String titulo = "Paciente: " + paciente.toString();

        // Si quieres estadísticas para UN solo paciente (ej: evolución por mes)
        Map<String,List<Consulta>> consultasFiltradas = new HashMap<>();
        consultasFiltradas.put("Total",consultas);

        generarReporte(nombreArchivo, titulo, consultas, consultasFiltradas, 4);
    }

    private Map<String, List<Consulta>> agruparConsultasPorMes(List<Consulta> consultas){



        Map<String, List<Consulta>> consultasMensuales = consultas.stream()
                .filter(c -> c.getFechaReg() != null)
                .collect(Collectors.groupingBy(consulta -> {
                    LocalDate fecha = consulta.getFechaReg().toLocalDate();
                    int mesNum = fecha.getMonth().getValue();
                    return MESES[mesNum-1];
                }));
        if(!consultas.isEmpty()){
            consultasMensuales.put(MESES[12],consultas);
        }

        // inyeccion de meses vacios
        for (int i = 0; i < 13; i++) {
            // Si el mapa NO contiene la clave, significa que no hubo registros.
            // La añadimos con una lista vacía (conteo = 0).
            consultasMensuales.putIfAbsent(MESES[i], new ArrayList<>());
        }

        return consultasMensuales;
    }

    private Map<String, List<Consulta>> agruparConsultasPorSemana(List<Consulta> consultas, LocalDate startOfMonth) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // Determinamos el número de semana del año para el primer día del mes
        int firstWeekNum = startOfMonth.get(weekFields.weekOfMonth());

        // 1. Agrupación inicial
        Map<String, List<Consulta>> consultasAgrupadas = consultas.stream()
                .filter(c -> c.getFechaReg() != null)
                .collect(Collectors.groupingBy(consulta -> {
                    LocalDate fecha = consulta.getFechaReg().toLocalDate();
                    // Calculamos la semana relativa al inicio del mes
                    int weekNum = fecha.get(weekFields.weekOfMonth());
                    int semanaAjustada = weekNum - firstWeekNum + 1;
                    return "Semana " + semanaAjustada;
                }));

        // 2. Determinar cuántas semanas tiene REALMENTE este mes
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
        int lastWeekNum = endOfMonth.get(weekFields.weekOfMonth());

        // El total de semanas puede ser 4, 5 o incluso 6
        int totalSemanasDelMes = lastWeekNum - firstWeekNum + 1;

        // 3. Inyectar semanas vacías (asegura que aparezcan en el reporte aunque no haya consultas)
        for (int i = 1; i <= totalSemanasDelMes; i++) {
            consultasAgrupadas.putIfAbsent("Semana " + i, new ArrayList<>());
        }

        // 4. IMPORTANTE: Añadir la entrada para el "Total"
        // Esto es lo que permite que la columna final de la tabla funcione
        consultasAgrupadas.put("Total", new ArrayList<>(consultas));

        return consultasAgrupadas;
    }


    public void generarReporteConsultas(List<Consulta> consultas, Workbook workbook, String titulo) throws IOException {

        Sheet sheet = workbook.createSheet("Consultas");
        EstiloHelper estilos = new EstiloHelper(workbook);

        // titulo
        Row tituloRow = sheet.createRow(0);
        tituloRow.setHeight(ALTURA_TITULO_PRINCIPAL);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellStyle(estilos.getTituloPrincipal());
        tituloCell.setCellValue("Consultas: ".concat(titulo));
        for (int i = 1; i<13;i++){
            tituloRow.createCell(i).setCellStyle(estilos.getNegro());
        }

        // Encabezados
        String[] columnas = {"Matricula", "Nombres", "Apellidos", "Edad", "Programa academico",
                "Diagnóstico", "Causa diagnóstico", "Medicamento", "Observaciones", "Fecha", "Altura", "Peso", "IMC", "Estado IMC"};
        Row headerRow = sheet.createRow(1);
        headerRow.setHeight(ALTURA_SUBTITULO);
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(estilos.getSubtitulo());
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
            c0.setCellStyle(estilos.getNormal());

            // Columna 1: Nombres
            Cell c1 = row.createCell(1);
            c1.setCellValue(paciente.getNombres());
            c1.setCellStyle(estilos.getNormal());

            // Columna 2: Apellidos
            Cell c2 = row.createCell(2);
            c2.setCellValue(paciente.getApellidos());
            c2.setCellStyle(estilos.getNormal());

            // Columna 3: Edad (es entero, usa estilos.getNormal())
            Cell c3 = row.createCell(3);
            c3.setCellValue(consulta.getEdad() != null ? consulta.getEdad() : 0);
            c3.setCellStyle(estilos.getNormal());

            // Columna 4: Programa academico
            Cell c4 = row.createCell(4);
            c4.setCellValue(paciente.getProgramaAcademico());
            c4.setCellStyle(estilos.getNormal());

            // Columna 5: Diagnóstico (texto libre)
            Cell c5 = row.createCell(5);
            c5.setCellValue(consulta.getDiagnostico());
            c5.setCellStyle(estilos.getNormal());

            // Columna 6: Causa del diagnóstico (catálogo)
            Cell c6 = row.createCell(6);
            String causaDiagnostico = consulta.getDiagnosticoKey() != null ? consulta.getDiagnosticoKey().toString() : "";
            c6.setCellValue(causaDiagnostico);
            c6.setCellStyle(estilos.getNormal());

            // Columna 7: Medicamento
            Cell c7 = row.createCell(7);
            c7.setCellValue(consulta.getMedicamento());
            c7.setCellStyle(estilos.getNormal());

            // Columna 8: Observaciones
            Cell c8 = row.createCell(8);
            c8.setCellValue(consulta.getObservaciones());
            c8.setCellStyle(estilos.getNormal());

            // Columna 9: Fecha (Texto/String de la Fecha)
            Cell c9 = row.createCell(9);
            c9.setCellValue(consulta.getFechaReg() != null ? consulta.getFechaReg().toString() : "");
            c9.setCellStyle(estilos.getNormal());
            // ----------------------------------------------------
            // APLICACIÓN DE ESTILO NÚMERO (Float)
            // ----------------------------------------------------

            // Columna 10: Altura
            Cell c10 = row.createCell(10);
            // Usamos setCellValue(double) para que aplique el formato de número
            c10.setCellValue(consulta.getAltura() != null ? consulta.getAltura() : 0f);
            c10.setCellStyle(estilos.getNumero());

            // Columna 11: Peso
            Cell c11 = row.createCell(11);
            c11.setCellValue(consulta.getPeso() != null ? consulta.getPeso() : 0f);
            c11.setCellStyle(estilos.getNumero());

            // Columna 12: IMC
            Cell c12 = row.createCell(12);
            c12.setCellValue(consulta.getImc() != null ? consulta.getImc() : 0f);
            c12.setCellStyle(estilos.getNumero());

            // Columna 13: Estado IMC (texto)
            Cell c13 = row.createCell(13);
            c13.setCellValue(consulta.getImc_estado());
            c13.setCellStyle(estilos.getNormal());
        }

        // Autoajustar columnas
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

    }

    public void generarEstadisticasConsultas(Workbook workbook, Map<String, List<Consulta>> consultas, String titulo, int tipo) {
        Sheet sheet = workbook.createSheet("Estadisticas");
        EstiloHelper estilos = new EstiloHelper(workbook);

        // 1. Obtener la configuración del reporte
        String[] secciones = getSecciones(tipo, consultas); // ["Enero", "Febrero"...] o ["Semana 1"...]
        String nombreTipoSeccion = getTipoSeccion(tipo); // "MES" o "SEMANA"

        int rowNum = 0;

        // --- TÍTULO PRINCIPAL ---
        Row tituloRow = sheet.createRow(rowNum++);
        tituloRow.setHeight((short) 800);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue(titulo);
        tituloCell.setCellStyle(estilos.getTituloPrincipal());

        // Aplicar estilo negro al resto de la fila del título para que se vea uniforme
        for (int i = 1; i <= secciones.length; i++) {
            tituloRow.createCell(i).setCellStyle(estilos.getNegro());
        }

        // --- FILA DE ENCABEZADOS (Semanas/Meses) ---
        Row agrupacionRow = sheet.createRow(rowNum++);
        agrupacionRow.setHeight((short) 600);
        Cell labelTipoCell = agrupacionRow.createCell(0);
        labelTipoCell.setCellValue(nombreTipoSeccion);
        labelTipoCell.setCellStyle(estilos.getSubtitulo());

        // --- FILA DE TOTALES GENERALES ---
        Row totalesRow = sheet.createRow(rowNum++);
        Cell labelTotalCell = totalesRow.createCell(0);
        labelTotalCell.setCellValue("TOTAL CONSULTAS");
        labelTotalCell.setCellStyle(estilos.getSubtitulo());

        // Llenado de columnas basado en el ORDEN de 'secciones'
        for (int i = 0; i < secciones.length; i++) {
            String nombreSeccion = secciones[i];
            List<Consulta> listaConsultas = consultas.getOrDefault(nombreSeccion, new ArrayList<>());

            // Celda de encabezado (ej: Enero)
            Cell grupoCell = agrupacionRow.createCell(i + 1);
            grupoCell.setCellValue(nombreSeccion);
            grupoCell.setCellStyle(estilos.getSubtitulo());

            // Celda de dato (ej: 45)
            Cell datoCell = totalesRow.createCell(i + 1);
            datoCell.setCellValue(listaConsultas.size());
            datoCell.setCellStyle(estilos.getNormal());
        }

        // --- SECCIÓN DIAGNÓSTICOS ---
        // Asegúrate de que 'diagnosticos' sea accesible (inyectado o pasado por parámetro)
        var diagnosticosList = diagnosticoServicio.listarDiagnosticos().stream()
                .map(Object::toString)
                .filter(diagnostico -> {
                    if (diagnostico == null) return false;
                    String norm = normalizarTexto(diagnostico);
                    return !norm.equals("PROGRAMAS ACADEMICOS") &&
                           !norm.equals("GRUPOS SEXO") &&
                           !norm.equals("TOTAL") &&
                           !norm.equals("HOMBRE") &&
                           !norm.equals("MUJER");
                })
                .toList();

        rowNum = escribirSeccionGenerica(sheet, rowNum, consultas, "CAUSAS DE CONSULTA",
                diagnosticosList, estilos,secciones, tipo,workbook,
                c -> {
                    String diagnostico = c.getDiagnosticoKey() != null ? c.getDiagnosticoKey().toString() : null;
                    if (diagnostico == null) {
                        return null;
                    }
                    String norm = normalizarTexto(diagnostico);
                    // Excluir categorías que no son diagnósticos médicos devolviendo null
                    if (norm.equals("PROGRAMAS ACADEMICOS") ||
                        norm.equals("GRUPOS SEXO") ||
                        norm.equals("HOMBRE") ||
                        norm.equals("MUJER") ||
                        norm.equals("TOTAL")) {
                        return null;
                    }
                    return diagnostico;
                });

        if(tipo == 4){
            // Autoajuste final de columnas
            for (int col = 0; col <= secciones.length; col++) {
                sheet.autoSizeColumn(col);
            }
        };
        // --- SECCIÓN CARRERAS ---
        rowNum = escribirSeccionGenerica(sheet, rowNum, consultas, "PROGRAMAS ACADÉMICOS",
                Arrays.asList(programas), estilos,secciones, tipo, workbook,
                c -> c.getPaciente() != null ? c.getPaciente().getProgramaAcademico() : "N/A");

        // --- SECCIÓN SEXO ---
        rowNum = escribirSeccionGenerica(sheet, rowNum, consultas, "GRUPOS (SEXO)",
                Arrays.asList("Hombre", "Mujer"), estilos,secciones, tipo, workbook,
                c -> c.getPaciente() != null ? c.getPaciente().getSexo() : "N/A");

        // Autoajuste final de columnas
        for (int col = 0; col <= secciones.length; col++) {
            sheet.autoSizeColumn(col);
        }
    }

    private int escribirSeccionGenerica(
            Sheet sheet,
            int rowNum,
            Map<String, List<Consulta>> consultasPorTiempo,
            String tituloSeccion,
            List<String> filasMaestras, // Aquí recibes diagnosticosList, programas o sexos
            EstiloHelper estilos,
            String[] headersColumnas,
            int tipoReporte,
            Workbook workbook,
            java.util.function.Function<Consulta, String> extractor) { // Aquí recibes la lambda (c -> ...)

        int filaInicioDatos = rowNum+1;

        // 1. Fila del Subtítulo de la Sección
        Row rowSeccion = sheet.createRow(rowNum++);
        rowSeccion.setHeight(ALTURA_SUBTITULO);
        Cell cellTitulo = rowSeccion.createCell(0);
        cellTitulo.setCellValue(tituloSeccion);
        cellTitulo.setCellStyle(estilos.getSubtitulo());

        // Pintar fondo negro en las celdas de los encabezados
        for (int i = 0; i < headersColumnas.length; i++) {
            rowSeccion.createCell(i + 1).setCellStyle(estilos.getNegro());
        }

        // 2. Agregar fila de totales al principio
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL");
        totalLabelCell.setCellStyle(estilos.getSubtitulo());
        
        // Calcular totales por columna
        for (int i = 0; i < headersColumnas.length; i++) {
            String columnaActual = headersColumnas[i];
            long totalColumna = consultasPorTiempo.getOrDefault(columnaActual, new ArrayList<>()).size();
            
            Cell totalCell = totalRow.createCell(i + 1);
            totalCell.setCellValue(totalColumna);
            totalCell.setCellStyle(estilos.getSubtitulo());
        }

        // 3. Escribir los datos fila por fila usando la lista maestra
        for (String nombreFila : filasMaestras) {
            Row row = sheet.createRow(rowNum++);

            // Primera columna: El nombre de la categoría (Gripe, Hombre, etc.)
            Cell cellCategoria = row.createCell(0);
            cellCategoria.setCellValue(nombreFila);
            cellCategoria.setCellStyle(estilos.getNormal());

            // Columnas dinámicas (Semanas o Meses)
            for (int i = 0; i < headersColumnas.length; i++) {
                String columnaActual = headersColumnas[i];

                // Filtramos al vuelo: buscamos en la lista de ese tiempo cuántos coinciden con la fila
                long conteo = consultasPorTiempo.getOrDefault(columnaActual, new ArrayList<>())
                        .stream()
                        .map(extractor)
                        .filter(Objects::nonNull)
                        .filter(nombreFila::equals)
                        .count();

                Cell cellDato = row.createCell(i + 1);
                cellDato.setCellValue(conteo);
                cellDato.setCellStyle(estilos.getNormal());
            }
        }
        
        int filaFinDatos = rowNum;
        if (tipoReporte != 4 && !filasMaestras.isEmpty()) {
            // Usamos siempre el título de sección para que añadirGraficoSeccion
            // pueda aplicar los rangos fijos A5:A16/B5:B16 y A18:A33/B18:B33
            // cuando se trate de CAUSAS DE CONSULTA o PROGRAMAS ACADÉMICOS.
            añadirGraficoSeccion(sheet, filaInicioDatos, filaFinDatos, headersColumnas.length, tituloSeccion, workbook);
        }

        return rowNum;
    }

    private void añadirGraficoSeccion(Sheet sheet, int filaInicio, int filaFin, int numColumnas, String titulo, Workbook workbook) {
        if (!(sheet instanceof XSSFSheet xssfSheet)) return;

        XSSFDrawing drawing = xssfSheet.createDrawingPatriarch();
        CreationHelper helper = workbook.getCreationHelper();
        int anchoGrafico = 10;
        int altoGrafico = 14;

        // Si el usuario pidió rangos fijos para estas secciones, los respetamos
        if ("CAUSAS DE CONSULTA".equalsIgnoreCase(titulo)) {
            // Rango fijo: A5:A16 y B5:B16 (índices 0-based: filas 4-15, col 0 y 1)
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(1);
            anchor.setCol2(1 + anchoGrafico);
            anchor.setRow1(3); // un poco arriba del rango de datos
            anchor.setRow2(3 + altoGrafico);

            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("CAUSAS DE CONSULTA");
            chart.setTitleOverlay(false);

            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("causas de consulta");
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("Total de consultas");
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            XDDFDataSource<String> categorias = XDDFDataSourcesFactory.fromStringCellRange(
                    xssfSheet,
                    new CellRangeAddress(4, 15, 0, 0) // A5:A16
            );

            XDDFNumericalDataSource<Double> valores = XDDFDataSourcesFactory.fromNumericCellRange(
                    xssfSheet,
                    new CellRangeAddress(4, 15, 1, 1) // B5:B16
            );

            XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            data.setBarDirection(BarDirection.COL);

            XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(categorias, valores);
            series.setTitle("Total", null);

            chart.plot(data);
            return;
        }

        if ("PROGRAMAS ACADÉMICOS".equalsIgnoreCase(titulo)) {
            // Rango fijo: A18:A33 y B18:B33 (índices 0-based: filas 17-32, col 0 y 1)
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(1);
            anchor.setCol2(1 + anchoGrafico);
            anchor.setRow1(16);
            anchor.setRow2(16 + altoGrafico);

            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("PROGRAMAS ACADÉMICOS - Total");
            chart.setTitleOverlay(false);

            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle("programas académicos");
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle("Total de consultas");
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            XDDFDataSource<String> categorias = XDDFDataSourcesFactory.fromStringCellRange(
                    xssfSheet,
                    new CellRangeAddress(17, 32, 0, 0) // A18:A33
            );

            XDDFNumericalDataSource<Double> valores = XDDFDataSourcesFactory.fromNumericCellRange(
                    xssfSheet,
                    new CellRangeAddress(17, 32, 1, 1) // B18:B33
            );

            XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            data.setBarDirection(BarDirection.COL);

            XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(categorias, valores);
            series.setTitle("Total", null);

            chart.plot(data);
            return;
        }

        // Comportamiento original para el resto de secciones
        int categoriaCol = 0;
        Cell seccionCell = sheet.getRow(filaInicio - 1).getCell(0);
        Row fechaRow = sheet.getRow(1);

        for (int i = 0; i < numColumnas; i++) {
            int weekCol = 1 + i;

            ClientAnchor anchorSemana = helper.createClientAnchor();
            int baseRow = (numColumnas + 2) + (i * (anchoGrafico + 1));

            anchorSemana.setCol1(baseRow);
            anchorSemana.setCol2(baseRow + anchoGrafico);

            anchorSemana.setRow1(filaInicio - 1);
            anchorSemana.setRow2(filaInicio - 1 + altoGrafico);

            XSSFChart chartSemana = drawing.createChart(anchorSemana);
            chartSemana.setTitleText(seccionCell.getStringCellValue() + " " + fechaRow.getCell(weekCol).getStringCellValue());
            chartSemana.setTitleOverlay(false);

            XDDFChartLegend legendSemana = chartSemana.getOrAddLegend();
            legendSemana.setPosition(LegendPosition.BOTTOM);

            XDDFCategoryAxis bottomAxisSemana = chartSemana.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxisSemana.setTitle(seccionCell.getStringCellValue().toLowerCase(Locale.ROOT));
            XDDFValueAxis leftAxisSemana = chartSemana.createValueAxis(AxisPosition.LEFT);
            leftAxisSemana.setTitle("Total de consultas");
            leftAxisSemana.setCrosses(AxisCrosses.AUTO_ZERO);

            XDDFDataSource<String> categoriasSemana = XDDFDataSourcesFactory.fromStringCellRange(
                    xssfSheet,
                    new CellRangeAddress(filaInicio, filaFin, categoriaCol, categoriaCol)
            );

            XDDFNumericalDataSource<Double> valoresSemana = XDDFDataSourcesFactory.fromNumericCellRange(
                    xssfSheet,
                    new CellRangeAddress(filaInicio, filaFin, weekCol, weekCol)
            );

            XDDFBarChartData dataSemana = (XDDFBarChartData) chartSemana.createData(ChartTypes.BAR, bottomAxisSemana, leftAxisSemana);
            dataSemana.setBarDirection(BarDirection.COL);

            XDDFBarChartData.Series seriesSemana = (XDDFBarChartData.Series) dataSemana.addSeries(categoriasSemana, valoresSemana);
            seriesSemana.setTitle(seccionCell.getStringCellValue() + " " + fechaRow.getCell(weekCol).getStringCellValue(), null);

            chartSemana.plot(dataSemana);
        }
    }

    public String[] getSecciones(int tipo, Map<String, List<Consulta>> consultasFiltradas) {
        return switch (tipo) {
            case 1 -> MESES; // El de 12 meses + Total
            case 2 -> {
                // Generamos los headers dinámicamente según lo que tenga el mapa
                // Filtramos "Total" para ordenarlas numéricamente y luego lo ponemos al final
                List<String> keys = consultasFiltradas.keySet().stream()
                        .filter(k -> k.startsWith("Semana"))
                        .sorted(Comparator.comparingInt(s -> Integer.parseInt(s.replaceAll("\\D", ""))))
                        .collect(Collectors.toList());
                keys.add("Total");
                yield keys.toArray(new String[0]);
            }
            case 3 -> TOTAL_ONLY; // Solo "Total"
            case 4 -> TOTAL_ONLY;
            default -> TOTAL_ONLY;
        };
    }

    public String getTipoSeccion(int tipo){
        return switch (tipo){
            case 1 -> "MESES";
            case 2 -> "SEMANAS";
            case 3 -> "";
            case 4 -> "";
            default -> "";
        };
    }
    
    private String normalizarTexto(String texto) {
        if (texto == null) return "";
        String normalized = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toUpperCase(Locale.ROOT).trim();
    }
    public void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(null,mensaje);
    } 
}
