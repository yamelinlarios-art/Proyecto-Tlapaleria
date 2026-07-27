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
 * Servicio encargado de manejar la lógica del inventario
 * y las alertas de productos con stock bajo.
 *
 * @author Kevin Dydier y Yael Mora Simón
 */
@Service
public class ServicioInventario {

    private static final Logger log =
            LoggerFactory.getLogger(ServicioInventario.class);

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    /**
     * Crea el servicio e inyecta los repositorios necesarios.
     *
     * @param inventarioRepository repositorio de inventarios
     * @param productoRepository repositorio de productos
     */
    @Autowired
    public ServicioInventario(
            InventarioRepository inventarioRepository,
            ProductoRepository productoRepository) {

        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Busca los productos cuya existencia es menor o igual
     * al stock mínimo y los agrega a la lista de alertas.
     *
     * @return lista de productos que necesitan reabastecerse
     */
    public List<Producto> consultarAlertas() {

        log.info("Iniciando consulta de alertas de inventario...");

        List<Producto> productosConAlerta = new ArrayList<>();

        // Recupera todos los registros del inventario.
        Iterable<Inventario> todosLosInventarios =
                inventarioRepository.findAll();

        for (Inventario item : todosLosInventarios) {

            // Revisa si la existencia llegó o bajó del stock mínimo.
            if (item.getExistenciaActual()
                    <= item.getStockMinimo()) {

                // Busca el producto usando su identificador.
                Producto producto =
                        productoRepository.findByIdProducto(
                                item.getIdProducto());

                // Si no se encuentra con el método personalizado,
                // se intenta buscar con el método de Spring.
                if (producto == null) {
                    producto =
                            productoRepository
                                    .findById(item.getIdProducto())
                                    .orElse(null);
                }

                // Si el producto no existe, se crea uno temporal
                // para evitar errores al mostrar la información.
                if (producto == null) {
                    producto = new Producto();
                    producto.setIdProducto(item.getIdProducto());
                    producto.setClave(
                            "PROD-0" + item.getIdProducto());
                    producto.setNombre(
                            "Producto ID " + item.getIdProducto());
                }

                // Agrega al producto la existencia actual del inventario.
                producto.setExistenciaActual(
                        item.getExistenciaActual());

                productosConAlerta.add(producto);
            }
        }

        return productosConAlerta;
    }

    /**
     * Busca un producto por su identificador y agrega
     * la existencia actual registrada en el inventario.
     *
     * @param idProducto identificador del producto
     * @return producto encontrado con su existencia actual,
     *         o null si no existe
     */
    public Producto obtenerDetalleProducto(long idProducto) {

        Producto producto =
                productoRepository.findById(idProducto).orElse(null);

        if (producto != null) {

            // Recupera la existencia real del producto.
            Inventario inventario =
                    inventarioRepository
                            .findById(idProducto)
                            .orElse(null);

            if (inventario != null) {
                producto.setExistenciaActual(
                        inventario.getExistenciaActual());
            }
        }

        return producto;
    }

    /**
     * Obtiene el stock mínimo registrado para un producto.
     *
     * @param idProducto identificador del producto
     * @return stock mínimo del producto o cero si no existe
     */
    public int obtenerStockMinimo(long idProducto) {

        Inventario inventario =
                inventarioRepository
                        .findById(idProducto)
                        .orElse(null);

        return (inventario != null)
                ? inventario.getStockMinimo()
                : 0;
    }

    /**
     * Busca los registros de inventario cuya existencia
     * es menor o igual al stock mínimo.
     *
     * También imprime los resultados en consola para revisar
     * que la consulta esté funcionando.
     *
     * @return lista de inventarios con stock bajo
     */
    public List<Inventario> obtenerProductosBajoStock() {

        System.out.println("===== INVENTARIO =====");

        List<Inventario> productosBajoStock =
                new ArrayList<>();

        for (Inventario inventario :
                inventarioRepository.findAll()) {

            System.out.println(inventario);

            // Agrega el inventario cuando ya llegó
            // o bajó del límite mínimo.
            if (inventario.getExistenciaActual()
                    <= inventario.getStockMinimo()) {

                productosBajoStock.add(inventario);
            }
        }

        System.out.println(
                "Cantidad de productos encontrados: "
                        + productosBajoStock.size());

        return productosBajoStock;
    }
}