package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
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
    private MovimientoInventarioRepository movimientoRepository;

    /**
     * Registra una nueva venta, calcula el cambio, actualiza el stock
     * y genera el movimiento de salida en el historial de inventario.
     *
     * @param detalles Lista de productos y cantidades procesadas en la HU-05.
     * @param montoRecibido Cantidad con la que pagó el cliente.
     * @return El objeto Venta persistido.
     */
    @Transactional // Asegura que si algo falla, se reviertan los cambios
    public Venta registrarVenta(List<DescripcionVenta> detalles, double montoRecibido) {
        
        // 1. Instanciar la nueva venta
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(LocalDateTime.now());
        nuevaVenta.setPago(montoRecibido);

        // 2. Procesar cada detalle
        if (detalles != null) {
            for (DescripcionVenta detalle : detalles) {
                Producto productoBD = detalle.getProducto();
                
                if (productoBD != null) {
                    int existenciaAnterior = productoBD.getExistenciaActual();
                    int cantidadVendida = detalle.getCantidad();
                    int nuevaExistencia = existenciaAnterior - cantidadVendida;
                    
                    // Actualizar el stock del producto
                    productoBD.setExistenciaActual(nuevaExistencia);
                    productoRepository.save(productoBD);
                    
                    // 📝 CREAR Y GUARDAR EL MOVIMIENTO EN EL HISTORIAL
                    MovimientoInventario movimiento = new MovimientoInventario();
                    movimiento.setFecha(LocalDateTime.now());
                    movimiento.setTipoMovimiento("SALIDA");
                    movimiento.setCantidad(cantidadVendida);
                    movimiento.setExistenciaAnterior(existenciaAnterior);
                    movimiento.setExistenciaActual(nuevaExistencia);
                    movimiento.setObservacion("Venta realizada");
                    movimiento.setProducto(productoBD);

                    movimientoRepository.save(movimiento);
                    
                    // Agregar el producto a la venta de forma limpia
                    nuevaVenta.agregaProducto(productoBD, cantidadVendida);
                }
            }
        }

        // 3. Asignar cambio basándose en el total calculado automáticamente por Venta
        double totalCalculado = nuevaVenta.getTotal() != null ? nuevaVenta.getTotal() : 0.0;
        nuevaVenta.setCambio(montoRecibido - totalCalculado);

        // 4. Persistir la venta
        return ventaRepository.save(nuevaVenta);
    }

    /**
     * Inicia una nueva instancia de Venta en memoria.
     */
    public Venta iniciarVenta() {
        return new Venta();
    }

    /**
     * Agrega un producto a la venta recibida.
     */
    public Venta agregarProducto(Producto producto, int cantidad, Venta venta) {
        if (venta == null) {
            venta = iniciarVenta();
        }

        venta.agregaProducto(producto, cantidad);

        return venta;
    }

    /**
     * Actualiza venta en la base de datos.
     */
    public boolean actualizarVenta(Venta venta) {
        if (venta == null) {
            return false;
        }

        ventaRepository.save(venta);
        return true;
    }
}