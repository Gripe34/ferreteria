package ferreteria.demo.impl;

import ferreteria.demo.dto.VentaDTO;
import ferreteria.demo.dto.DetalleVentaDTO;
import ferreteria.demo.service.FacturaService;
import ferreteria.demo.service.VentaService;
import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FacturaServiceImpl implements FacturaService {

    private final VentaService ventaService;

    public FacturaServiceImpl(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @Override
    public byte[] generarFacturaPdf(Long ventaId) {

        VentaDTO venta = ventaService.findById(ventaId);

        if (venta == null) {
            throw new RuntimeException("Venta no encontrada con ID: " + ventaId);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // 2. Definir fuentes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.WHITE);
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.RED);

            // 3. Encabezado y Datos de la Transacción
            Paragraph title = new Paragraph("FERRETERÍA EL YUNQUE DE HIERRO", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("RUC: 123456789-0 | Tel: (01) 555-444", dataFont));
            document.add(new Paragraph("Factura de Venta No. " + venta.getId().toString(), titleFont));
            document.add(new Paragraph(" ", dataFont));


            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            document.add(new Paragraph("Fecha y Hora: " + venta.getFechaRegistro().format(dateTimeFormatter), dataFont));


            document.add(new Paragraph("Cliente: " + (venta.getClienteNombre() != null ? venta.getClienteNombre() : "Venta al Público"), dataFont));


            document.add(new Paragraph("Vendedor: " + (venta.getVendedorNombre() != null ? venta.getVendedorNombre() : "[No Registrado]"), dataFont));
            document.add(new Paragraph(" ", dataFont));

            // 4. Tabla de Productos
            document.add(createProductsTable(venta.getDetalles(), headerFont, dataFont));
            document.add(new Paragraph(" ", dataFont));

            // 5. Totales Finales
            document.add(new Paragraph("TOTAL A PAGAR: ", totalFont));
            document.add(new Paragraph("$" + venta.getTotal().setScale(2).toString(), totalFont));

            document.close();

        } catch (Exception e) {
            // Se asume que este código ya tiene la importación de DetalleVentaDTO
            throw new RuntimeException("Error al generar el PDF para la venta " + ventaId + ": " + e.getMessage(), e);
        }

        return outputStream.toByteArray();
    }

    // --- FUNCIÓN AUXILIAR PARA CREAR LA TABLA ---
    // La firma es correcta para List<DetalleVentaDTO>
    private PdfPTable createProductsTable(List<DetalleVentaDTO> detalles, Font headerFont, Font dataFont) {

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Encabezados
        String[] headers = {"Producto", "Cant.", "Precio Unitario", "Subtotal"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Filas de Detalles
        for (DetalleVentaDTO detalle : detalles) {
            // Producto
            table.addCell(new PdfPCell(new Phrase(detalle.getProductoNombre(), dataFont)));

            // Cantidad
            PdfPCell cantCell = new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()), dataFont));
            cantCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cantCell);

            // Precio Unitario
            PdfPCell priceCell = new PdfPCell(new Phrase("$" + detalle.getPrecioUnitario().setScale(2).toString(), dataFont));
            priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(priceCell);

            // Subtotal
            BigDecimal subtotal = detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
            PdfPCell subtotalCell = new PdfPCell(new Phrase("$" + subtotal.setScale(2).toString(), dataFont));
            subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(subtotalCell);
        }

        return table;
    }
}