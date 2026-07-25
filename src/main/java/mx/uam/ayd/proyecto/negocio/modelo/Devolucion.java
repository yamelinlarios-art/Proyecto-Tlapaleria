package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/*
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

    /*
     * Muchas devoluciones pueden corresponder al mismo producto.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    private int cantidad;

    private String motivo;

    private String tipoDevolucion;

    private LocalDateTime fecha;

    /*
     * Muchas devoluciones pueden enviarse al mismo proveedor.
     *
     * La relación es opcional porque no todas las devoluciones
     * necesariamente tienen un proveedor.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proveedor", nullable = true)
    private Proveedor proveedor;

    /*
     * Se conserva este atributo porque ya forma parte
     * del programa original.
     */
    private String numeroEmpleado;

    /*
     * Muchas devoluciones pueden ser registradas
     * por el mismo vendedor.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vendedor")
    private Vendedor vendedor;

    /*
     * Una devolución puede producir varios registros
     * dentro de la bitácora.
     *
     * mappedBy hace referencia al atributo "devolucion"
     * que se agregará en la clase Bitacora.
     */
    @OneToMany(mappedBy = "devolucion")
    private List<Bitacora> registrosBitacora = new ArrayList<>();

    // Constructor por defecto requerido por JPA
    public Devolucion() {
    }

    //          MÉTODOS DE ACCESO: GETTERS

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

    public Proveedor getProveedor() {
        return proveedor;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public List<Bitacora> getRegistrosBitacora() {
        return registrosBitacora;
    }

    //          MÉTODOS DE ACCESO: SETTERS

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

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public void setRegistrosBitacora(
            List<Bitacora> registrosBitacora) {

        this.registrosBitacora = registrosBitacora;
    }

    //          MÉTODOS SOBREESCRITOS (@Override)

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
        return (int) (31 * idDevolucion);
    }

    @Override
    public String toString() {
        return "Devolucion [idDevolucion=" + idDevolucion
                + ", producto=" + producto
                + ", cantidad=" + cantidad
                + ", motivo=" + motivo
                + ", tipoDevolucion=" + tipoDevolucion
                + ", fecha=" + fecha
                + ", proveedor=" + proveedor
                + ", numeroEmpleado=" + numeroEmpleado + "]";
    }
}