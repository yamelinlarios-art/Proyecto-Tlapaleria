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
     * 
     * @param idFactura El identificador único de la factura
     * @return la factura o null si no existe
     */
    public Factura findByIdFactura(long idFactura);

    /**
     * Busca todas las facturas pertenecientes a un proveedor.
     * 
     * @param idProveedor El identificador del proveedor
     * @return lista de facturas asociadas al proveedor
     */
    public List<Factura> findByIdProveedor(int idProveedor);

    /**
     * Busca las facturas de un proveedor filtradas por su estado (ej. "Pendiente").
     * Indispensable para la consulta de saldos en la HU-06.
     * 
     * @param idProveedor El identificador del proveedor
     * @param estado El estado de la factura
     * @return lista de facturas que coinciden con el estado
     */
    public List<Factura> findByIdProveedorAndEstado(int idProveedor, String estado);
}