package ferreteria.demo.service;

import ferreteria.demo.entity.Compra;
import ferreteria.demo.entity.DetalleCompra;
import ferreteria.demo.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface CompraService {



    Optional<Compra> encontrarCompraPorId(Long id);
    List<Compra> encontrarComprasPorUsuario(Long usuarioId);
    Optional<Compra> encontrarCompraPorIdYUsuario(Long compraId, Long usuarioId);
    Optional<Compra> encontrarCompraPorIdYUsuarioConDetalles(Long compraId, Long usuarioId);
    Compra procesarNuevaCompra(Usuario cliente, List<DetalleCompra> detalles);


}
