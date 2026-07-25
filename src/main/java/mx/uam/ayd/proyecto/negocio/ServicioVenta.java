package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.InventarioRepository;
import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Inventario;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@Service
public class ServicioVenta {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    /**
     * Registra una nueva venta, actualiza el stock en Producto e Inventario,
     * guarda el historial de movimiento y calcula el cambio.
     */
    @Transactional
    public Venta registrarVenta(List<DescripcionVenta> detalles, double montoRecibido) {
        
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(LocalDateTime.now());
        nuevaVenta.setPago(montoRecibido);

        if (detalles != null) {
            for (DescripcionVenta detalle : detalles) {
                Producto productoBD = detalle.getProducto();
                
                if (productoBD != null) {
                    int existenciaAnterior = productoBD.getExistenciaActual();
                    int cantidadVendida = detalle.getCantidad();
                    int nuevaExistencia = existenciaAnterior - cantidadVendida;
                    
                    // 1. Actualizar el stock en Producto
                    productoBD.setExistenciaActual(nuevaExistencia);
                    productoRepository.save(productoBD);

                    // 2. 🚨 ACTUALIZAR EN INVENTARIO (Para que se dispare la Alerta de Stock)
                    Inventario inventario = inventarioRepository.findById(productoBD.getIdProducto()).orElse(null);
                    if (inventario != null) {
                        inventario.setExistenciaActual(nuevaExistencia);
                        inventarioRepository.save(inventario);
                    }
                    
                    // 3. Registrar salida en Historial de Movimientos
                    MovimientoInventario movimiento = new MovimientoInventario();
                    movimiento.setFecha(LocalDateTime.now());
                    movimiento.setTipoMovimiento("SALIDA");
                    movimiento.setCantidad(cantidadVendida);
                    movimiento.setExistenciaAnterior(existenciaAnterior);
                    movimiento.setExistenciaActual(nuevaExistencia);
                    movimiento.setObservacion("Venta realizada");
                    movimiento.setProducto(productoBD);

                    movimientoRepository.save(movimiento);
                    
                    // 4. Agregar a la venta
                    nuevaVenta.agregaProducto(productoBD, cantidadVendida);
                }
            }
        }

        double totalCalculado = nuevaVenta.getTotal() != null ? nuevaVenta.getTotal() : 0.0;
        nuevaVenta.setCambio(montoRecibido - totalCalculado);

        return ventaRepository.save(nuevaVenta);
    }

    public Venta iniciarVenta() {
        return new Venta();
    }

    public Venta agregarProducto(Producto producto, int cantidad, Venta venta) {
        if (venta == null) {
            venta = iniciarVenta();
        }
        venta.agregaProducto(producto, cantidad);
        return venta;
    }

    public boolean actualizarVenta(Venta venta) {
        if (venta == null) {
            return false;
        }
        ventaRepository.save(venta);
        return true;
    }
}