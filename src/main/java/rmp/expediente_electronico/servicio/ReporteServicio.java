package rmp.expediente_electronico.servicio;

import com.toedter.calendar.JDateChooser;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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
                    LocalDate fecha = consulta.getFechaReg().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
                    LocalDate fecha = consulta.getFechaReg().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
                .toList();

        rowNum = escribirSeccionGenerica(sheet, rowNum, consultas, "CAUSAS DE CONSULTA",
                diagnosticosList, estilos,secciones, tipo,
                c -> c.getDiagnosticoKey() != null ? c.getDiagnosticoKey().toString() : "Sin diagnóstico");

        if(tipo == 4) return;
        // --- SECCIÓN CARRERAS ---
        rowNum = escribirSeccionGenerica(sheet, rowNum, consultas, "PROGRAMAS ACADÉMICOS",
                Arrays.asList(programas), estilos,secciones, tipo,
                c -> c.getPaciente() != null ? c.getPaciente().getProgramaAcademico() : "N/A");

        // --- SECCIÓN SEXO ---
        rowNum = escribirSeccionGenerica(sheet, rowNum, consultas, "GRUPOS (SEXO)",
                Arrays.asList("Hombre", "Mujer"), estilos,secciones, tipo,
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
            java.util.function.Function<Consulta, String> extractor) { // Aquí recibes la lambda (c -> ...)

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

        // 2. Escribir los datos fila por fila usando la lista maestra
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
                        .filter(c -> nombreFila.equals(extractor.apply(c)))
                        .count();

                Cell cellDato = row.createCell(i + 1);
                cellDato.setCellValue(conteo);
                cellDato.setCellStyle(estilos.getNormal());
            }
        }

        return rowNum; // Espacio en blanco para la siguiente sección
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


    /*
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
*/
    public void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(null,mensaje);
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
