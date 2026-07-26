package mx.uam.ayd.proyecto.datos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Factura;

/**
 * Repositorio para la entidad Factura.
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
public interface FacturaRepository extends CrudRepository<Factura, Long> {

    /**
     * Busca una factura específica por su idFactura.
     */
    public Factura findByIdFactura(long idFactura);

    /**
     * Busca todas las facturas pertenecientes a un proveedor por su ID.
     */
    public List<Factura> findByIdProveedor(long idProveedor);

    /**
     * Busca las facturas de un proveedor por estado ignorando Mayúsculas/Minúsculas (ej. "PENDIENTE" o "Pendiente").
     */
    public List<Factura> findByIdProveedorAndEstadoIgnoreCase(long idProveedor, String estado);

    /**
     * Alternativa usando la relación JPA ignora mayúsculas/minúsculas.
     */
    public List<Factura> findByProveedorIdProveedorAndEstadoIgnoreCase(long idProveedor, String estado);
}