package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Repositorio para la entidad Producto.
 * 
 * @author JAVITOS, Yamelin Larios Nepomuseno
 */
public interface ProductoRepository extends CrudRepository<Producto, Long> {

    /**
     * Busca un producto específico por su nombre ignorando mayúsculas y minúsculas (HU-05).
     * 
     * @param nombre El nombre del producto (ej. "martillo" encontrará "Martillo")
     * @return El producto encontrado o null si no existe
     */
    Producto findByNombreIgnoreCase(String nombre);

    /**
     * Busca un producto específico por su nombre exacto.
     * 
     * @param nombre El nombre del producto
     * @return El producto encontrado o null si no existe
     */
    Producto findByNombre(String nombre);

    /**
     * Busca un producto por su clave única de negocio (RN-08).
     * Indispensable para la identificación en la HU-03.
     * 
     * @param clave La clave única del producto
     * @return El producto encontrado o null si no existe
     */
    Producto findByClave(String clave);

    /**
     * Busca un producto específico por su idProducto.
     * Indispensable para la actualización de precios en la HU-09.
     * 
     * @param idProducto El identificador único del producto
     * @return El producto encontrado o null si no existe
     */
    Producto findByIdProducto(long idProducto);
}