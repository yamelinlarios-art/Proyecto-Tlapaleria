package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.FacturaRepository;
import mx.uam.ayd.proyecto.datos.ProveedorRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Factura;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Servicio de negocio para gestionar los proveedores, facturas y saldos pendientes (HU-06).
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Service
public class ServicioProveedor {

    private final ProveedorRepository proveedorRepository;
    private final FacturaRepository facturaRepository;

    @Autowired
    public ServicioProveedor(ProveedorRepository proveedorRepository, FacturaRepository facturaRepository) {
        this.proveedorRepository = proveedorRepository;
        this.facturaRepository = facturaRepository;
    }

    /**
     * Recupera todos los proveedores registrados en el sistema.
     * Convierte el Iterable de CrudRepository a List.
     * 
     * @return Lista de proveedores
     */

    public List<Proveedor> recuperarProveedores() {
        List<Proveedor> proveedores = new ArrayList<>();
        proveedorRepository.findAll().forEach(proveedores::add);
        return proveedores;
    }

    /**
     * Recupera un proveedor específico por su identificador único.
     * 
     * @param idProveedor ID del proveedor
     * @return El proveedor o null si no se encuentra
     */

    public Proveedor recuperarProveedor(long idProveedor) {
        return proveedorRepository.findByIdProveedor(idProveedor);
    }

    /**
     * Recupera la lista de facturas pendientes de un proveedor de forma tolerante a Mayúsculas/Minúsculas.
     * 
     * @param idProveedor ID del proveedor
     * @return Lista de facturas pendientes (nunca null)
     */
  
    public List<Factura> recuperarFacturasPendientes(long idProveedor) {
        // Se buscan las facturas usando IgnoreCase para que no falle por "PENDIENTE" vs "Pendiente"
        List<Factura> pendientes = facturaRepository.findByIdProveedorAndEstadoIgnoreCase(idProveedor, "PENDIENTE");
        
        // Si no regresa nada con el idPrimitivo, intenta por la relación JPA de respaldo
        if (pendientes == null || pendientes.isEmpty()) {
            pendientes = facturaRepository.findByProveedorIdProveedorAndEstadoIgnoreCase(idProveedor, "PENDIENTE");
        }
        
        return pendientes != null ? pendientes : new ArrayList<>();
    }

    /**
     * Calcula la suma total del saldo pendiente de un proveedor.
     * 
     * @param idProveedor ID del proveedor
     * @return Saldo pendiente total
     */
  
    public double calcularSaldoPendienteProveedor(long idProveedor) {
        List<Factura> facturasPendientes = recuperarFacturasPendientes(idProveedor);
        double saldoTotal = 0.0;
        
        for (Factura f : facturasPendientes) {
            saldoTotal += f.getSaldoPendiente();
        }
        
        return saldoTotal;
    }

    /**
     * Registra el pago de una factura, actualiza su saldo a 0.0 y cambia su estado a "PAGADA".
     * RN-02 y RN-05: Se actualiza en la base de datos automáticamente.
     * 
     * @param idFactura ID de la factura a pagar
     * @return La factura actualizada o null si no existe o ya estaba pagada
     */
    
    public Factura registrarPago(long idFactura) {
        Factura factura = facturaRepository.findByIdFactura(idFactura);

        if (factura != null && "PENDIENTE".equalsIgnoreCase(factura.getEstado())) {
            factura.setSaldoPendiente(0.0);
            factura.setEstado("PAGADA");
            return facturaRepository.save(factura);
        }

        return null;
    }
}