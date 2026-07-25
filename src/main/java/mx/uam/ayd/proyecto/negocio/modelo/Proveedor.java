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
     *
     * mappedBy = "proveedor" hace referencia al atributo
     * proveedor de la clase Pedido.
     */
    @OneToMany(mappedBy = "proveedor")
    private List<Pedido> pedidos = new ArrayList<>();

    /*
     * Un proveedor puede tener muchas facturas.
     *
     * mappedBy = "proveedor" hará referencia al atributo
     * proveedor que agregaremos en la clase Factura.
     */
    @OneToMany(mappedBy = "proveedor")
    private List<Factura> facturas = new ArrayList<>();

    /*
     * Un proveedor puede recibir muchas devoluciones.
     *
     * mappedBy = "proveedor" hace referencia al atributo
     * proveedor que ya existe en Devolucion.
     */
    @OneToMany(mappedBy = "proveedor")
    private List<Devolucion> devoluciones = new ArrayList<>();

    /**
     * Constructor vacío requerido por JPA.
     */
    public Proveedor() {
    }

    /**
     * @return the idProveedor
     */
    public long getIdProveedor() {
        return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(long idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * @return the nombreCompleto
     */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /**
     * @param nombreCompleto the nombreCompleto to set
     */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    /**
     * @return the corporativo
     */
    public String getCorporativo() {
        return corporativo;
    }

    /**
     * @param corporativo the corporativo to set
     */
    public void setCorporativo(String corporativo) {
        this.corporativo = corporativo;
    }

    /**
     * @return the idDevolucion
     */
    public int getIdDevolucion() {
        return idDevolucion;
    }

    /**
     * @param idDevolucion the idDevolucion to set
     */
    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return the tipoProveedor
     */
    public String getTipoProveedor() {
        return tipoProveedor;
    }

    /**
     * @param tipoProveedor the tipoProveedor to set
     */
    public void setTipoProveedor(String tipoProveedor) {
        this.tipoProveedor = tipoProveedor;
    }

    /**
     * @return pedidos relacionados con el proveedor
     */
    public List<Pedido> getPedidos() {
        return pedidos;
    }

    /**
     * @param pedidos pedidos relacionados con el proveedor
     */
    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    /**
     * @return facturas relacionadas con el proveedor
     */
    public List<Factura> getFacturas() {
        return facturas;
    }

    /**
     * @param facturas facturas relacionadas con el proveedor
     */
    public void setFacturas(List<Factura> facturas) {
        this.facturas = facturas;
    }

    /**
     * @return devoluciones relacionadas con el proveedor
     */
    public List<Devolucion> getDevoluciones() {
        return devoluciones;
    }

    /**
     * @param devoluciones devoluciones relacionadas con el proveedor
     */
    public void setDevoluciones(List<Devolucion> devoluciones) {
        this.devoluciones = devoluciones;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
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