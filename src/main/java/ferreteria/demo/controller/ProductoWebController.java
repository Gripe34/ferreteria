package ferreteria.demo.controller;

import ferreteria.demo.dto.MovimientoInventarioDTO;
import ferreteria.demo.dto.PrecioHistoricoDTO;
import ferreteria.demo.dto.ProductoDTO;
import ferreteria.demo.entity.Rol;
import ferreteria.demo.service.CategoriaService;
import ferreteria.demo.service.MovimientoInventarioService;
import ferreteria.demo.service.ProductoService;
import ferreteria.demo.service.ProveedorService;
import ferreteria.demo.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; // 🔥 NECESARIO para obtener el contexto
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/gestion-productos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
public class ProductoWebController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final ProveedorService proveedorService;
    private final MovimientoInventarioService movimientoInventarioService;

    public ProductoWebController(
            ProductoService productoService,
            CategoriaService categoriaService,
            UsuarioService usuarioService,
            ProveedorService proveedorService,
            MovimientoInventarioService movimientoInventarioService) {

        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.proveedorService = proveedorService;
        this.movimientoInventarioService = movimientoInventarioService;
    }

    // 🔥 FUNCIÓN AUXILIAR: Obtiene el ID del usuario logueado
    private Long getUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String username = authentication.getName();
        return usuarioService.findIdByUsername(username);
    }

    // --- LISTAR PRODUCTOS (Tabla Principal) ---
    @GetMapping
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
public String listarProductos(Model model) {
    
    // 🚩 CAMBIO CLAVE: Ahora llamamos a findAllProductos() 
    // para listar TODOS los productos (activos y desactivados/bloqueados)
    List<ProductoDTO> productos = productoService.findAllProductos(); 
    
    boolean alertaMargenActivada = false;

    // La lógica del margen se ejecuta sobre la lista completa de productos
    for (ProductoDTO producto : productos) {
        BigDecimal costo = producto.getCosto();
        BigDecimal precio = producto.getPrecio();

        if (precio != null && costo != null && precio.compareTo(costo) <= 0) {
            alertaMargenActivada = true;
            break;
        }
    }

    if (alertaMargenActivada) {
        model.addAttribute("alertaMargen",
                "¡ATENCIÓN! Se han detectado productos con Margen de Ganancia nulo o negativo (Precio de Venta <= Costo). Revise los costos y precios inmediatamente.");
    }

    model.addAttribute("producto", new ProductoDTO());
    model.addAttribute("productos", productos);
    model.addAttribute("categorias", categoriaService.findAllCategorias());

    return "admin/gestion-productos";
}




@PostMapping("/eliminar/{id}")
public String desactivarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        productoService.desactivarProducto(id); // Implementar en el servicio
        redirectAttributes.addFlashAttribute("mensajeExito", "Producto desactivado (ocultado) correctamente.");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorCritico", "Error al desactivar el producto: " + e.getMessage());
    }
    return "redirect:/gestion-productos";
}

// --- 2. ACTIVAR (El "activar producto" del menú) ---
@PostMapping("/activar/{id}")
public String activarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        productoService.activarProducto(id); // Implementar en el servicio
        redirectAttributes.addFlashAttribute("mensajeExito", "Producto activado (visible) correctamente.");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorCritico", "Error al activar el producto: " + e.getMessage());
    }
    return "redirect:/gestion-productos";
}

// --- 3. BLOQUEAR (El "bloquear venta" del menú) ---
@PostMapping("/bloquear/{id}")
public String bloquearVenta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        // Llama a una lógica centralizada en el servicio: establecer bloqueado = true
        productoService.cambiarEstadoBloqueo(id, true); 
        redirectAttributes.addFlashAttribute("mensajeExito", "Venta bloqueada temporalmente.");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorCritico", "Error al bloquear la venta: " + e.getMessage());
    }
    return "redirect:/gestion-productos";
}

// --- 4. DESBLOQUEAR (El "desbloquear venta" del menú) ---
@PostMapping("/desbloquear/{id}")
public String desbloquearVenta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        // Llama a una lógica centralizada en el servicio: establecer bloqueado = false
        productoService.cambiarEstadoBloqueo(id, false); 
        redirectAttributes.addFlashAttribute("mensajeExito", "Venta desbloqueada. Producto disponible.");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorCritico", "Error al desbloquear la venta: " + e.getMessage());
    }
    return "redirect:/gestion-productos";
}





    @GetMapping("/nuevo")
    public String mostrarFormularioDeCreacion(Model model) {
        // ...
        model.addAttribute("proveedoresActivos", proveedorService.findAllProveedoresActivos());
        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("categorias", categoriaService.findAllCategorias());
        return "admin/crear-producto";
    }

    @PostMapping("/nuevo")
    public String crearProducto(
            @ModelAttribute("producto") ProductoDTO productoDTO,
            @RequestParam("imagenArchivo") MultipartFile imagenArchivo,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            Long adminId = usuarioService.findIdByUsername(username);

            if (adminId == null) { throw new RuntimeException("Error: ID de administrador no pudo ser recuperado."); }

            productoService.crearProducto(productoDTO, adminId, imagenArchivo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto " + productoDTO.getNombre() + " creado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCritico", "Error al crear producto o subir imagen: " + e.getMessage());
            return "redirect:/gestion-productos/nuevo";
        }
        return "redirect:/gestion-productos";
    }


    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEdicion(@PathVariable Long id, Model model) {
        // ...
        ProductoDTO producto = productoService.findById(id);
        List<PrecioHistoricoDTO> historial = productoService.getHistorialPrecioPorProducto(id);
        model.addAttribute("proveedoresActivos", proveedorService.findAllProveedores());
        model.addAttribute("historialPrecios", historial);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.findAllCategorias());
        return "admin/editar-producto";
    }


    @PostMapping("/editar/{id}")
    public String procesarFormularioDeEdicion(
            @PathVariable Long id,
            @ModelAttribute("producto") ProductoDTO productoDTO,
            @RequestParam(value = "imagenArchivo", required = false) MultipartFile imagenArchivo,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            Long userId = usuarioService.findIdByUsername(username);
            if (userId == null) { throw new RuntimeException("Error: ID de administrador no pudo ser recuperado."); }

            productoService.editarProducto(id, productoDTO, userId, imagenArchivo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto " + productoDTO.getNombre() + " actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorCritico", "Error al actualizar: " + e.getMessage());
            return "redirect:/gestion-productos/editar/" + id;
        }
        return "redirect:/gestion-productos/editar/" + id;
    }

    // --- ELIMINAR PRODUCTO ---
  




    @PostMapping("/agregar-stock")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public String agregarStock(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            @RequestParam String justificacion,
            RedirectAttributes ra) {

        Long usuarioId = getUserIdFromSecurityContext(); // Obtener el ID del usuario logueado

        if (usuarioId == null) {
            ra.addFlashAttribute("errorMessage", "Error: Sesión de usuario no válida.");
            return "redirect:/login"; // Redirigir al login si no se encuentra el usuario
        }

        try {
            productoService.agregarStock(productoId, cantidad, justificacion, usuarioId);
            ra.addFlashAttribute("mensajeExito", "Stock añadido exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorCritico", "Error al añadir stock: " + e.getMessage());
        }

        // Redirige de vuelta a la lista principal (donde se verán los modales)
        return "redirect:/gestion-productos";
    }

    @PostMapping("/eliminar-stock")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public String eliminarStock(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            @RequestParam String justificacion,
            RedirectAttributes ra) {

        Long usuarioId = getUserIdFromSecurityContext();

        if (usuarioId == null) {
            ra.addFlashAttribute("errorCritico", "Error: Sesión de usuario no válida.");
            return "redirect:/login";
        }

        try {
            productoService.eliminarStock(productoId, cantidad, justificacion, usuarioId);
            ra.addFlashAttribute("mensajeExito", "Stock eliminado exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorCritico", "Error al eliminar stock: " + e.getMessage());
        }

        return "redirect:/gestion-productos";
    }

    @GetMapping("/historial-stock/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENDEDOR')")
    public String mostrarHistorialStock(@PathVariable Long id, Model model) {

        try {
            // 1. Obtener los datos del producto
            ProductoDTO producto = productoService.findById(id);

            // 2. Obtener el historial de movimientos (¡Llamamos al servicio responsable!)
            List<MovimientoInventarioDTO> historial = movimientoInventarioService.getHistorialMovimientosPorProducto(id); // <-- CAMBIO CLAVE

            // 3. Pasar los datos a la vista
            model.addAttribute("producto", producto);
            model.addAttribute("historialMovimientos", historial); // Aquí va la lista de DTOs con stockAnterior

            // 4. Retornar el fragmento HTML
            return "admin/modal-historial-stock :: modalContent"; // Este es el nombre que usaste en tu código
            
        } catch (Exception e) {
            // ... manejo de errores ...
            model.addAttribute("errorCarga", "Error al cargar el historial de movimientos: " + e.getMessage());
            return "admin/modal-error-fragment";
        }
    }
}