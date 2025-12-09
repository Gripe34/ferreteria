package ferreteria.demo.service;

import ferreteria.demo.dto.TopProductosDTO; // Usaremos TopProductosDTO para Top Ganancias
import ferreteria.demo.dto.CategoriaRentabilidadDTO;
import ferreteria.demo.dto.TendenciaVentaDTO; // Usaremos TendenciaVentaDTO
import ferreteria.demo.dto.VentaDTO;

import java.util.List;


public interface ReporteService {


    List<ferreteria.demo.dto.GraficaVentaDTO> getTopProductosPorGanancia(int limite);


    List<CategoriaRentabilidadDTO> getRentabilidadPorCategoria();


    List<TendenciaVentaDTO> getTendenciaVentasMensuales();

    List<VentaDTO> getAllVentasForExport();


}