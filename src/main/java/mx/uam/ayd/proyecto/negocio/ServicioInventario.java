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
 * Servicio para la logica de negocio del inventario y las alertas de stock bajo.
 *
 * @author Kevin Dydier y Yael Mora Simón
 */
@Service
public class ServicioInventario {

    private static final Logger log = LoggerFactory.getLogger(ServicioInventario.class);

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    /**
     * Constructor para inyectar los repos de la bd
     * 
     * @param inventarioRepository repo de inventarios
     * @param productoRepository repo de productos
     */
    @Autowired
    public ServicioInventario(InventarioRepository inventarioRepository, ProductoRepository productoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Revisa la bd y busca todos los productos que tienen stock bajo o igual al minimo
     * para ponerlos en la lista de alertas que se pinta en rojo en la tabla.
     *
     * @return lista de productos que necesitan reabastecerse
     */
    public List<Producto> consultarAlertas() {
        log.info("Iniciando consulta de alertas de inventario...");
        List<Producto> productosConAlerta = new ArrayList<>();

        // recuperamos todos los registros de inventario de la bd
        Iterable<Inventario> todosLosInventarios = inventarioRepository.findAll();

        for (Inventario item : todosLosInventarios) {
            // checamos si la existencia actual sobrepasa o es igual al stock minimo
            if (item.getExistenciaActual() <= item.getStockMinimo()) {
                
                // buscamos el producto por su id
                Producto producto = productoRepository.findByIdProducto(item.getIdProducto());

                // por si no lo encuentra con el personalizado usamos el de spring
                if (producto == null) {
                    producto = productoRepository.findById(item.getIdProducto()).orElse(null);
                }

                // si de plano no esta en la tabla producto creamos uno ficticio para que no truene
                if (producto == null) {
                    producto = new Producto();
                    producto.setIdProducto(item.getIdProducto());
                    producto.setClave("PROD-0" + item.getIdProducto());
                    producto.setNombre("Producto ID " + item.getIdProducto());
                }

                // le pasamos el stock del inventario al objeto producto para mostrarlo en la tabla
                producto.setExistenciaActual(item.getExistenciaActual());

                productosConAlerta.add(producto);
            }
        }

        return productosConAlerta;
    }
    
    /**
     * Regresa la info del producto buscando en la tabla de productos y le pega su stock actual
     *
     * @param idProducto id del producto que queremos
     * @return el producto encontrado con su existencia
     */
    public Producto obtenerDetalleProducto(long idProducto) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto != null) {
            // le pegamos la existencia real desde el inventario
            Inventario inventario = inventarioRepository.findById(idProducto).orElse(null);
            if (inventario != null) {
                producto.setExistenciaActual(inventario.getExistenciaActual());
            }
        }
        return producto;
    }

    /**
     * Trae el stock minimo que esta guardado en el inventario para un producto
     *
     * @param idProducto id del producto
     * @return el limite minimo o cero si no hay registro
     */
    public int obtenerStockMinimo(long idProducto) {
        Inventario inventario = inventarioRepository.findById(idProducto).orElse(null);
        return (inventario != null) ? inventario.getStockMinimo() : 0;
    }

    /**
     * Imprime en consola los productos con bajo stock, sirve mas para probar que funcione
     *
     * @return lista con los inventarios que estan en minimo
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