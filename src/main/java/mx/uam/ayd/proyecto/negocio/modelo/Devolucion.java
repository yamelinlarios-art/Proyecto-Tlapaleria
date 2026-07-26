package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entidad de negocio Devolucion
 * HU10 - Devolución de productos dañados
 *
 * @author Yamelin Larios Nepomuseno
 */
@Entity
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDevolucion;

    /**
     * Muchas devoluciones pueden corresponder al mismo producto.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    private int cantidad;

    private String motivo;

    private String tipoDevolucion;

    private LocalDateTime fecha;

    /**
     * Se conserva este atributo por compatibilidad con el sistema base.
     */
    private String numeroEmpleado;

    /**
     * Muchas devoluciones pueden ser registradas por el mismo vendedor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor")
    private Vendedor vendedor;

    /**
     * Muchas devoluciones pueden asociarse a un mismo proveedor.
     * Mapeado para resolver el 'mappedBy' de la entidad Proveedor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    // Constructor por defecto requerido por JPA
    public Devolucion() {
    }

    // MÉTODOS DE ACCESO: GETTERS

    public long getIdDevolucion() {
        return idDevolucion;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getTipoDevolucion() {
        return tipoDevolucion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    // MÉTODOS DE ACCESO: SETTERS

    public void setIdDevolucion(long idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setTipoDevolucion(String tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    // MÉTODOS SOBREESCRITOS (@Override)

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Devolucion other = (Devolucion) obj;
        return idDevolucion == other.idDevolucion;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(idDevolucion);
    }

    @Override
    public String toString() {
        return "Devolucion [idDevolucion=" + idDevolucion
                + ", idProducto=" + (producto != null ? producto.getIdProducto() : null)
                + ", cantidad=" + cantidad
                + ", motivo=" + motivo
                + ", tipoDevolucion=" + tipoDevolucion
                + ", fecha=" + fecha
                + ", numeroEmpleado=" + numeroEmpleado + "]";
    }
}