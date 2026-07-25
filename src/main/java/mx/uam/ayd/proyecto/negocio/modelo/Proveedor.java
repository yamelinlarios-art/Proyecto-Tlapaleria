package mx.uam.ayd.proyecto.negocio.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entidad de negocio Proveedor
 *
 * @author Yamelin, Guillermo, Dydier, Yael, Sheyla
 *
 */
@Entity // Esto le dice a Spring que esta es una entidad persistente
public class Proveedor {

    @Id // Esto le dice a Spring que este es el identificador
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Le dice a Spring que genere el id
    private long idProveedor;

    private String nombreCompleto;

    private String corporativo;

    /*
     * Se conserva este atributo original para no afectar
     * los servicios o controladores que ya lo utilicen.
     */
    private int idDevolucion;

    private String telefono;

    private String tipoProveedor;

    /*
     * Un proveedor puede estar relacionado con muchos pedidos.
     * mappedBy = "proveedor" hace referencia al atributo proveedor de Pedido.
     */
    @OneToMany(mappedBy = "proveedor")
    private List<Pedido> pedidos = new ArrayList<>();

    /*
     * Un proveedor puede tener muchas facturas.
     * mappedBy = "proveedor" hace referencia al atributo proveedor de Factura.
     */
    @OneToMany(mappedBy = "proveedor")
    private List<Factura> facturas = new ArrayList<>();

    /*
     * Un proveedor puede recibir muchas devoluciones.
     * mappedBy = "proveedor" hace referencia al atributo proveedor de Devolucion.
     */
    @OneToMany(mappedBy = "proveedor")
    private List<Devolucion> devoluciones = new ArrayList<>();

    /**
     * Constructor vacío requerido por JPA.
     */
    public Proveedor() {
    }

    // --- GETTERS Y SETTERS ---

    public long getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(long idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorporativo() {
        return corporativo;
    }

    public void setCorporativo(String corporativo) {
        this.corporativo = corporativo;
    }

    public int getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoProveedor() {
        return tipoProveedor;
    }

    public void setTipoProveedor(String tipoProveedor) {
        this.tipoProveedor = tipoProveedor;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public List<Factura> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<Factura> facturas) {
        this.facturas = facturas;
    }

    public List<Devolucion> getDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(List<Devolucion> devoluciones) {
        this.devoluciones = devoluciones;
    }

    // --- EQUALS, HASHCODE Y TOSTRING ---

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Proveedor other = (Proveedor) obj;
        return idProveedor == other.idProveedor;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idProveedor);
    }

    @Override
    public String toString() {
        return "Proveedor [idProveedor=" + idProveedor
                + ", nombreCompleto=" + nombreCompleto
                + ", corporativo=" + corporativo
                + ", idDevolucion=" + idDevolucion
                + ", telefono=" + telefono
                + ", tipoProveedor=" + tipoProveedor + "]";
    }
}