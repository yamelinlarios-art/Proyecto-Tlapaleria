package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Repositorio para la entidad Proveedor.
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
public interface ProveedorRepository extends CrudRepository<Proveedor, Long> {

    /**
     * Busca un proveedor específico por su idProveedor.
     * Indispensable para la consulta de datos en la HU-06.
     * 
     * @param idProveedor El identificador único del proveedor
     * @return el proveedor o null si no existe
     */
    public Proveedor findByIdProveedor(long idProveedor);

    /**
     * Busca un proveedor por su nombre completo.
     * 
     * @param nombreCompleto El nombre del proveedor
     * @return el proveedor o null si no existe
     */
    public Proveedor findByNombreCompleto(String nombreCompleto);
}