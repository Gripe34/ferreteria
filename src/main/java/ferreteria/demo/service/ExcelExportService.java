package ferreteria.demo.service;

import ferreteria.demo.dto.VentaDTO;
import java.io.IOException;
import java.util.List;

public interface ExcelExportService {

    byte[] exportVentasToExcel(List<VentaDTO> ventas) throws IOException;

}