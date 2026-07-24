package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

/*
 * Entidad de negocio Bitacora
 * @author Yamelin Larios Nepomuseno
 */

@Entity
public class Bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idBitacora;

    // Atributos para HU-09
    private double precioAnterior;
    private double precioNuevo;

    // Atributos/Relaciones para la HU-10
    private long idDevolucion;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_devolucion", nullable = true)
    private Devolucion devolucion;

    private int cantidad;
    private String motivo;
    private String descripcion;

    // Atributos y relaciones compartidos (se ocupan tanto para HU-09 como para HU-10)
    private long idProducto;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = true)
    private Producto producto;

    private LocalDateTime fechaHora;

    // Métodos de acceso: getters

    public long getIdBitacora() {
        return idBitacora;
    }

    public double getPrecioAnterior() {
        return precioAnterior;
    }

    public double getPrecioNuevo() {
        return precioNuevo;
    }

    public long getIdDevolucion() {
        return idDevolucion;
    }

    public Devolucion getDevolucion() {
        return devolucion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public long getIdProducto() {
        return idProducto;
    }

    public Producto getProducto() {
        return producto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    // Métodos de modificación: setters

    public void setIdBitacora(long idBitacora) {
        this.idBitacora = idBitacora;
    }

    public void setPrecioAnterior(double precioAnterior) {
        this.precioAnterior = precioAnterior;
    }

    public void setPrecioNuevo(double precioNuevo) {
        this.precioNuevo = precioNuevo;
    }

    public void setIdDevolucion(long idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public void setDevolucion(Devolucion devolucion) {
        this.devolucion = devolucion;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    // Métodos sobreescritos (@Override)

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Bitacora other = (Bitacora) obj;
        return idBitacora == other.idBitacora;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idBitacora);
    }

    @Override
    public String toString() {
        return "Bitacora [idBitacora=" + idBitacora + ", precioAnterior=" + precioAnterior + ", precioNuevo="
                + precioNuevo + ", idDevolucion=" + idDevolucion + ", devolucion=" + devolucion + ", cantidad=" 
                + cantidad + ", motivo=" + motivo + ", descripcion=" + descripcion + ", idProducto=" + idProducto 
                + ", producto=" + producto + ", fechaHora=" + fechaHora + "]";
    }
}