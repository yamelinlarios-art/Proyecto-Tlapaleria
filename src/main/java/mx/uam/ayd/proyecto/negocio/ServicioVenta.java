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

/**
 * Servicio principal para procesar la logica de las ventas del carrito,
 * descuenta el stock de las tablas y guarda los movimientos en la bd.
 *
 * @author dydier
 */
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
     * Hace todo el proceso de la venta: descuenta existencias en producto e inventario,
     * genera la salida en el historial de movimientos y calcula el cambio del cliente.
     * 
     * @param detalles lista de productos y cantidades compradas
     * @param montoRecibido dinero en efectivo que entrego el cliente
     * @return la venta guardada en la base de datos
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
                    
                    //Actualizar el stock en Producto
                    productoBD.setExistenciaActual(nuevaExistencia);
                    productoRepository.save(productoBD);

                    //ACTUALIZAR EN INVENTARIO (Para que se dispare la Alerta de Stock)
                    Inventario inventario = inventarioRepository.findById(productoBD.getIdProducto()).orElse(null);
                    if (inventario != null) {
                        inventario.setExistenciaActual(nuevaExistencia);
                        inventarioRepository.save(inventario);
                    }
                    
                    //Registrar salida en Historial de Movimientos
                    MovimientoInventario movimiento = new MovimientoInventario();
                    movimiento.setFecha(LocalDateTime.now());
                    movimiento.setTipoMovimiento("SALIDA");
                    movimiento.setCantidad(cantidadVendida);
                    movimiento.setExistenciaAnterior(existenciaAnterior);
                    movimiento.setExistenciaActual(nuevaExistencia);
                    movimiento.setObservacion("Venta realizada");
                    movimiento.setProducto(productoBD);

                    movimientoRepository.save(movimiento);
                    
                    //Agregar a la venta
                    nuevaVenta.agregaProducto(productoBD, cantidadVendida);
                }
            }
        }

        double totalCalculado = nuevaVenta.getTotal() != null ? nuevaVenta.getTotal() : 0.0;
        nuevaVenta.setCambio(montoRecibido - totalCalculado);

        return ventaRepository.save(nuevaVenta);
    }

    /**
     * Crea un objeto de venta nuevo para iniciar la transaccion
     * 
     * @return una nueva venta vacia
     */
    public Venta iniciarVenta() {
        return new Venta();
    }

    /**
     * Agrega un producto y la cantidad deseada al objeto de venta
     * 
     * @param producto producto a vender
     * @param cantidad piezas que se lleva el cliente
     * @param venta la venta actual a la que se le suma el producto
     * @return la venta actualizada
     */
    public Venta agregarProducto(Producto producto, int cantidad, Venta venta) {
        if (venta == null) {
            venta = iniciarVenta();
        }
        venta.agregaProducto(producto, cantidad);
        return venta;
    }

    /**
     * Guarda los cambios de una venta en la base de datos
     * 
     * @param venta objeto venta con los cambios
     * @return true si lo guardo bien o false si venia nula
     */
    public boolean actualizarVenta(Venta venta) {
        if (venta == null) {
            return false;
        }
        ventaRepository.save(venta);
        return true;
    }
}