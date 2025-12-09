package ferreteria.demo.impl;

import ferreteria.demo.dto.VentaDTO;
import ferreteria.demo.service.ExcelExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter; // Para formatear fechas
import java.util.List;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public byte[] exportVentasToExcel(List<VentaDTO> ventas) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte Ventas Detallado");

            // --- ESTILOS PERSONALIZADOS ---
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle totalCurrencyStyle = createTotalCurrencyStyle(workbook); // Estilo para el total final

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // --- CABECERAS (Fila 0) ---
            String[] headers = {"ID Venta", "Fecha", "Cliente", "N° Productos", "Total (Monto)"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- DATOS (Fila 1 en adelante) ---
            int rowNum = 1;
            BigDecimal granTotal = BigDecimal.ZERO;

            for (VentaDTO venta : ventas) {
                Row row = sheet.createRow(rowNum++);

                // Columna 1: ID
                row.createCell(0).setCellValue(venta.getId());

                // Columna 2: Fecha (Aplicando estilo de fecha)
                Cell dateCell = row.createCell(1);
                if (venta.getFecha() != null) {
                    // Usamos la fecha como String, pero podrías usar un Date de Excel si quieres.
                    dateCell.setCellValue(venta.getFecha().format(dateFormatter));
                } else {
                    dateCell.setCellValue("N/A");
                }

                // Columna 3: Cliente
                row.createCell(2).setCellValue(venta.getClienteUsername());

                // Columna 4: N° Productos
                row.createCell(3).setCellValue(venta.getDetalles() != null ? venta.getDetalles().size() : 0);

                // Columna 5: Total (Aplicando estilo de moneda)
                Cell totalCell = row.createCell(4);
                if (venta.getTotal() != null) {
                    totalCell.setCellValue(venta.getTotal().doubleValue());
                    totalCell.setCellStyle(currencyStyle);
                    granTotal = granTotal.add(venta.getTotal());
                } else {
                    totalCell.setCellValue(0.0);
                    totalCell.setCellStyle(currencyStyle);
                }
            }

            // --- PIE DE TABLA (Gran Total) ---
            Row totalRow = sheet.createRow(rowNum);
            Cell totalLabelCell = totalRow.createCell(3);
            totalLabelCell.setCellValue("GRAN TOTAL:");
            totalLabelCell.setCellStyle(headerStyle); // Usamos el estilo de cabecera para resaltar

            Cell granTotalCell = totalRow.createCell(4);
            granTotalCell.setCellValue(granTotal.doubleValue());
            granTotalCell.setCellStyle(totalCurrencyStyle); // Nuevo estilo para el total

            // --- AJUSTES FINALES ---
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir el Workbook a un array de bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // =======================================================================
    // --- MÉTODOS AUXILIARES DE ESTILO ---
    // =======================================================================

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        // Color oscuro corporativo
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // Puedes usar un formato de fecha de Excel si quieres el tipo de dato nativo
        // style.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // Formato de moneda. Adapta el código de moneda si no usas EUR (ej: "$"#.##0,00)
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        return style;
    }

    private CellStyle createTotalCurrencyStyle(Workbook workbook) {
        CellStyle style = createCurrencyStyle(workbook);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.GREEN.getIndex()); // Total en verde
        style.setFont(font);
        style.setBorderTop(BorderStyle.DOUBLE); // Doble línea superior para el total
        return style;
    }
}