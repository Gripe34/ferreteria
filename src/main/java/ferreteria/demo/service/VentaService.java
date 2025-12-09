package ferreteria.demo.service;

import ferreteria.demo.dto.VentaDTO;
import ferreteria.demo.dto.VentasDashboardDTO;
import ferreteria.demo.entity.Pedido;

import java.util.List;

public interface VentaService {

    List<VentaDTO> findAllVentas();

    VentaDTO crearVenta(VentaDTO ventaDTO, Long vendedorId);

    VentaDTO findById(Long id);

    VentaDTO crearVentaInternaDesdePedido(Pedido pedido, Long vendedorId);

    List<VentaDTO> findVentasByClienteUsername(String username);

    VentasDashboardDTO getVentasClasificadas();

    VentaDTO getVentaDetalleById(Long ventaId);

    List<VentaDTO> findAllVentasConDetalle();

    List<VentaDTO> findVentasByClienteId(Long clienteId);
}