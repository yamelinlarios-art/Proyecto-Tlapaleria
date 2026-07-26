package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    /**
     * Recupera todos los proveedores registrados en el sistema.
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
     * Recupera la lista de facturas pendientes de un proveedor.
     * 
     * @param idProveedor ID del proveedor
     * @return Lista de facturas en estado "Pendiente"
     */
    public List<Factura> recuperarFacturasPendientes(long idProveedor) {
        return facturaRepository.findByIdProveedorAndEstado(idProveedor, "Pendiente");
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
     * Registra el pago de una factura, actualiza su saldo a 0.0 y cambia su estado a "Pagado".
     * RN-02 y RN-05: Se actualiza en la base de datos automáticamente.
     * 
     * @param idFactura ID de la factura a pagar
     * @return La factura actualizada o null si no existe
     */
    public Factura registrarPago(long idFactura) {
        Factura factura = facturaRepository.findByIdFactura(idFactura);

        if (factura != null && "Pendiente".equals(factura.getEstado())) {
            factura.setSaldoPendiente(0.0);
            factura.setEstado("Pagado");
            return facturaRepository.save(factura);
        }

        return null;
    }
}