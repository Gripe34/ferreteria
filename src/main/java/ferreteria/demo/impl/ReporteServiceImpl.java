package ferreteria.demo.impl;

import ferreteria.demo.dto.*;
import ferreteria.demo.service.ReporteService;
import ferreteria.demo.entity.DetalleVenta;
import ferreteria.demo.entity.Producto;
import ferreteria.demo.entity.Venta;
import ferreteria.demo.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.time.format.TextStyle;
import java.util.stream.Collectors;


@Service
public class ReporteServiceImpl implements ReporteService {

    private final VentaRepository ventaRepository;

    public ReporteServiceImpl(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    // =======================================================
    // --- 1. TOP PRODUCTOS POR GANANCIA (MÉTODO REQUERIDO) ---
    // =======================================================

    @Override
    @Transactional(readOnly = true)
    public List<GraficaVentaDTO> getTopProductosPorGanancia(int limite) {

        List<Venta> todasLasVentas = ventaRepository.findAll();
        Map<Long, GraficaVentaDTO> resumenGanancia = new HashMap<>();

        for (Venta venta : todasLasVentas) {
            if (venta.getDetalles() == null) continue;

            for (DetalleVenta detalle : venta.getDetalles()) {

                Producto producto = detalle.getProducto();
                if (producto == null || producto.getCosto() == null || detalle.getPrecioUnitario() == null) {
                    continue;
                }

                BigDecimal margenUnitario = detalle.getPrecioUnitario().subtract(producto.getCosto());
                BigDecimal gananciaTotalItem = margenUnitario.multiply(new BigDecimal(detalle.getCantidad()));
                gananciaTotalItem = gananciaTotalItem.setScale(2, RoundingMode.HALF_UP);

                resumenGanancia.computeIfAbsent(producto.getId(), k -> {
                    GraficaVentaDTO dto = new GraficaVentaDTO();
                    dto.setNombreProducto(producto.getNombre());
                    dto.setGananciaTotal(BigDecimal.ZERO);
                    dto.setCantidadVendida(0L);
                    return dto;
                });

                GraficaVentaDTO resumen = resumenGanancia.get(producto.getId());

                BigDecimal gananciaActual = resumen.getGananciaTotal().add(gananciaTotalItem);
                resumen.setGananciaTotal(gananciaActual);

                Long cantidadActualizada = resumen.getCantidadVendida() + (long) detalle.getCantidad();
                resumen.setCantidadVendida(cantidadActualizada);
            }
        }

        return resumenGanancia.values().stream()
                .sorted((a, b) -> b.getGananciaTotal().compareTo(a.getGananciaTotal()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    // ===============================================
    // --- 2. RENTABILIDAD POR CATEGORÍA ---
    // ===============================================

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaRentabilidadDTO> getRentabilidadPorCategoria() {
        List<Venta> todasLasVentas = ventaRepository.findAll();
        Map<String, BigDecimal> gananciaPorCategoria = new HashMap<>();

        for (Venta venta : todasLasVentas) {
            if (venta.getDetalles() == null) continue;

            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = detalle.getProducto();
                if (producto == null || producto.getCosto() == null || producto.getCategoria() == null) continue;

                BigDecimal margenUnitario = detalle.getPrecioUnitario().subtract(producto.getCosto());
                BigDecimal gananciaTotalItem = margenUnitario.multiply(new BigDecimal(detalle.getCantidad()));

                String nombreCategoria = producto.getCategoria().getNombre();

                gananciaPorCategoria.merge(
                        nombreCategoria,
                        gananciaTotalItem,
                        BigDecimal::add
                );
            }
        }

        return gananciaPorCategoria.entrySet().stream()
                .map(entry -> {
                    CategoriaRentabilidadDTO dto = new CategoriaRentabilidadDTO();
                    dto.setNombreCategoria(entry.getKey());
                    dto.setGananciaTotal(entry.getValue().setScale(2, RoundingMode.HALF_UP));
                    return dto;
                })
                .collect(Collectors.toList());
    }


    // ==================================================
    // --- 3. TENDENCIA DE VENTAS MENSUALES ---
    // ==================================================

    @Override
    @Transactional(readOnly = true)
    public List<TendenciaVentaDTO> getTendenciaVentasMensuales() {
        List<Venta> todasLasVentas = ventaRepository.findAll();
        Map<String, BigDecimal> ventasPorMes = new HashMap<>();

        for (Venta venta : todasLasVentas) {
            if (venta.getFecha() == null) continue;

            String periodo = venta.getFecha().getMonth().getDisplayName(TextStyle.SHORT, Locale.ROOT) +
                    " " + venta.getFecha().getYear();

            ventasPorMes.merge(
                    periodo,
                    venta.getTotal(),
                    BigDecimal::add
            );
        }

        return ventasPorMes.entrySet().stream()
                .map(entry -> {
                    TendenciaVentaDTO dto = new TendenciaVentaDTO();
                    dto.setPeriodo(entry.getKey());
                    dto.setTotalVenta(entry.getValue().setScale(2, RoundingMode.HALF_UP));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ==================================================
    // --- 4. EXPORTACIÓN A EXCEL Y TABLA DETALLE ---
    // ==================================================

    @Override
    @Transactional(readOnly = true)
    public List<VentaDTO> getAllVentasForExport() {
        // 1. Obtener todas las Entidades
        List<Venta> ventasEntity = ventaRepository.findAll();

        // 2. Mapear la lista de entidades a DTOs usando el método privado
        return ventasEntity.stream()
                .map(this::convertToDTO) // ✅ YA NO HAY ERROR: El método está abajo
                .collect(Collectors.toList());
    }

    // ==================================================
    // --- MÉTODOS PRIVADOS DE MAPEO (Mapper) ---
    // ==================================================

    // ✅ MÉTODO PRIVADO 1: Mapea Venta (Entidad) a VentaDTO
    private VentaDTO convertToDTO(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());
        dto.setClienteUsername(venta.getCliente() != null ? venta.getCliente().getUsername() : "N/A");

        // Mapear la lista de DetalleVenta (Entidad) a List<DetalleVentaDTO>
        if (venta.getDetalles() != null) {
            List<DetalleVentaDTO> detallesDTO = venta.getDetalles().stream()
                    .map(this::mapDetalleToDTO)
                    .collect(Collectors.toList());

            dto.setDetalles(detallesDTO);
        } else {
            dto.setDetalles(Collections.emptyList());
        }

        return dto;
    }


    // ✅ MÉTODO PRIVADO 2: Mapea DetalleVenta (Entidad) a DetalleVentaDTO
    private DetalleVentaDTO mapDetalleToDTO(DetalleVenta detalle) {
        DetalleVentaDTO detalleDTO = new DetalleVentaDTO();

        // Mapeo básico de campos del detalle
        detalleDTO.setCantidad(detalle.getCantidad());
        detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());

        // NOTA: Si DetalleVentaDTO necesita más campos (ej: nombre del producto), añádelos aquí.

        return detalleDTO;
    }
}