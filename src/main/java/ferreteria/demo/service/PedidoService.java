package ferreteria.demo.service;

import ferreteria.demo.config.Carrito;
import ferreteria.demo.dto.DireccionDTO;
import ferreteria.demo.dto.PedidoDTO;
import java.util.List;

public interface PedidoService {

    PedidoDTO procesarCheckout(Long clienteId, Carrito carrito, DireccionDTO direccion);
    PedidoDTO findById(Long id);
}