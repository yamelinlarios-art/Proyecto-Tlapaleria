package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uam.ayd.proyecto.datos.InventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Inventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Servicio encargado de la lógica de negocio del inventario.
 *
 * @author Kevin Dydier y Yael Mora Simón
 */
@Service
public class ServicioInventario {

    private static final Logger log = LoggerFactory.getLogger(ServicioInventario.class);

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public ServicioInventario(InventarioRepository inventarioRepository, ProductoRepository productoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
    }

    /**
     *
     * @return Lista de productos que requieren reabastecimiento (resaltados en rojo).
     */
    public List<Producto> consultarAlertas() {
        log.info("Iniciando consulta de alertas de inventario...");
        List<Producto> productosConAlerta = new ArrayList<>();

        // Recuperar todos los registros de inventario
        Iterable<Inventario> todosLosInventarios = inventarioRepository.findAll();

        for (Inventario item : todosLosInventarios) {
            // Validar Stock Mínimo
            if (item.getExistenciaActual() <= item.getStockMinimo()) {
                
                // Buscamos el producto utilizando el id del inventario
                Producto producto = productoRepository.findByIdProducto(item.getIdProducto());

                // Si no lo encuentra por método personalizado, intenta por findById
                if (producto == null) {
                    producto = productoRepository.findById(item.getIdProducto()).orElse(null);
                }

                // Si aún sigue sin existir en la tabla PRODUCTO, creamos un objeto para que la vista lo muestre
                if (producto == null) {
                    producto = new Producto();
                    producto.setIdProducto(item.getIdProducto());
                    producto.setClave("PROD-0" + item.getIdProducto());
                    producto.setNombre("Producto ID " + item.getIdProducto());
                }

                // Sincronizar el stock del inventario hacia el objeto Producto para la UI
                producto.setExistenciaActual(item.getExistenciaActual());

                productosConAlerta.add(producto);
            }
        }

        return productosConAlerta;
    }
    
    /**
     * Recupera el detalle de un producto específico para la vista.
     *
     * @param idProducto
     * @return El producto solicitado
     */
    public Producto obtenerDetalleProducto(long idProducto) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto != null) {
            // Buscar el inventario correspondiente para asignarle su existencia real
            Inventario inventario = inventarioRepository.findById(idProducto).orElse(null);
            if (inventario != null) {
                producto.setExistenciaActual(inventario.getExistenciaActual());
            }
        }
        return producto;
    }

    /**
     * Recupera el stock mínimo configurado para un producto en el inventario.
     *
     * @param idProducto
     * @return El límite mínimo de stock
     */
    public int obtenerStockMinimo(long idProducto) {
        Inventario inventario = inventarioRepository.findById(idProducto).orElse(null);
        return (inventario != null) ? inventario.getStockMinimo() : 0;
    }
      
    /**
     * Recupera todos los registros del inventario.
     *
     * @return Lista de inventarios.
     */
    public List<Inventario> obtenerProductosBajoStock() {

        System.out.println("===== INVENTARIO =====");

        List<Inventario> productosBajoStock = new ArrayList<>();

        for (Inventario inventario : inventarioRepository.findAll()) {

            System.out.println(inventario);

            if (inventario.getExistenciaActual() <= inventario.getStockMinimo()) {
                productosBajoStock.add(inventario);
            }
        }

        System.out.println("Cantidad de productos encontrados: "
                + productosBajoStock.size());

        return productosBajoStock;
    }
}