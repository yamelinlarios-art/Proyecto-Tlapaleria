package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entidad de negocio Bitacora
 *
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

    // Atributos para la HU-10

    /*
     * Se cambió de long (primitivo) a Long (objeto wrapper) 
     * para permitir valores NULL cuando un registro de bitácora
     * no pertenece a una devolución (ej. actualización de precio HU-09).
     */
    @Column(name = "id_devolucion", nullable = true)
    private Long idDevolucion;

    /*
     * Muchas entradas de la bitácora pueden pertenecer
     * a una misma devolución.
     *
     * insertable = false y updatable = false evitan que
     * JPA intente administrar dos veces la misma columna,
     * porque idDevolucion ya utiliza id_devolucion.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(
            name = "id_devolucion",
            insertable = false,
            updatable = false,
            nullable = true)
    private Devolucion devolucion;

    private int cantidad;

    private String motivo;

    private String descripcion;

    // Atributos compartidos (HU-09 y HU-10)
    private long idProducto;

    private LocalDateTime fechaHora;

    // Constructor por defecto requerido por JPA
    public Bitacora() {
    }

    // ==========================================
    //            GETTERS Y SETTERS
    // ==========================================

    public long getIdBitacora() {
        return idBitacora;
    }

    public void setIdBitacora(long idBitacora) {
        this.idBitacora = idBitacora;
    }

    public double getPrecioAnterior() {
        return precioAnterior;
    }

    public void setPrecioAnterior(double precioAnterior) {
        this.precioAnterior = precioAnterior;
    }

    public double getPrecioNuevo() {
        return precioNuevo;
    }

    public void setPrecioNuevo(double precioNuevo) {
        this.precioNuevo = precioNuevo;
    }

    /*
     * Retorna Long para permitir nulos
     */
    public Long getIdDevolucion() {
        return idDevolucion;
    }

    /*
     * Acepta Long para poder recibir null
     */
    public void setIdDevolucion(Long idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public Devolucion getDevolucion() {
        return devolucion;
    }

    /*
     * Si devolucion es null, asigna null a idDevolucion en lugar de 0
     */
    public void setDevolucion(Devolucion devolucion) {
        this.devolucion = devolucion;

        if (devolucion != null) {
            this.idDevolucion = devolucion.getIdDevolucion();
        } else {
            this.idDevolucion = null; // 👈 CORREGIDO: Ahora asigna null
        }
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    // ==========================================
    //          MÉTODOS SOBREESCRITOS
    // ==========================================

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
        return "Bitacora [idBitacora=" + idBitacora
                + ", precioAnterior=" + precioAnterior
                + ", precioNuevo=" + precioNuevo
                + ", idDevolucion=" + idDevolucion
                + ", cantidad=" + cantidad
                + ", motivo=" + motivo
                + ", descripcion=" + descripcion
                + ", idProducto=" + idProducto
                + ", fechaHora=" + fechaHora + "]";
    }
}