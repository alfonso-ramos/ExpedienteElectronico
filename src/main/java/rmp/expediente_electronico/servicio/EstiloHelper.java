package rmp.expediente_electronico.servicio;

import org.apache.poi.ss.usermodel.*;
import lombok.Getter;

public class EstiloHelper {
    private final Workbook workbook;
    @Getter private final CellStyle tituloPrincipal;
    @Getter private final CellStyle subtitulo;
    @Getter private final CellStyle normal;
    @Getter private final CellStyle numero;
    @Getter private final CellStyle negro;

    public EstiloHelper(Workbook workbook) {
        this.workbook = workbook;
        this.tituloPrincipal = crearEstilo(IndexedColors.WHITE, IndexedColors.BLACK, (short) 24, true, false);
        this.subtitulo = crearEstilo(IndexedColors.BLACK, IndexedColors.WHITE, (short) 16, true, true);
        this.normal = crearEstilo(IndexedColors.BLACK, IndexedColors.WHITE, (short) 14, false, true);
        this.negro = crearEstilo(IndexedColors.BLACK, IndexedColors.BLACK, (short) 16, true, false);

        this.numero = workbook.createCellStyle();
        this.numero.cloneStyleFrom(this.normal);
        this.numero.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
    }

    private CellStyle crearEstilo(IndexedColors colorFont, IndexedColors colorFondo, short size, boolean bold, boolean conBordes) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints(size);
        font.setBold(bold);
        font.setColor(colorFont.getIndex());
        style.setFont(font);

        if (colorFondo != IndexedColors.WHITE) {
            style.setFillForegroundColor(colorFondo.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (conBordes) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }
        return style;
    }
}