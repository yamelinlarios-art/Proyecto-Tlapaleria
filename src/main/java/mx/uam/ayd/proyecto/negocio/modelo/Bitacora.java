package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    // Atributos de la clase Bitacora según Diagrama de Dominio
    private double precioAnterior;
    private double precioNuevo;
    private long idDevolucion;
    private long idProducto; // <- ¡Agregado tal cual viene en tu diagrama!
    private LocalDateTime fechaHora;
    private int cantidad;
    private String motivo;
    private String descripcion;

    // Getters

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

    public long getIdProducto() {
        return idProducto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
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

    // Setters

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

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
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
                + precioNuevo + ", idDevolucion=" + idDevolucion + ", idProducto=" + idProducto + ", fechaHora=" 
                + fechaHora + ", cantidad=" + cantidad + ", motivo=" + motivo + ", descripcion=" + descripcion + "]";
    }
}