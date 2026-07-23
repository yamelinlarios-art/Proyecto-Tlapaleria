package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

/**
 * Entidad de negocio Devolucion (HU10 - Devolución de productos dañados)
 *
 * @author Yamelin
 *
 */
@Entity
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDevolucion;

    private long idProducto;
    private int cantidad;
    private String motivo;
    private String tipoDevolucion;
    private LocalDateTime fecha;

    // Atributos colaborativos opcionales (para auditoría o relación con personal/proveedor)
    private int idProveedor;
    private String numeroEmpleado;

    // Constructor por defecto requerido por JPA
    public Devolucion() {
    }

    //          MÉTODOS DE ACCESO: GETTERS

    public long getIdDevolucion() {
        return idDevolucion;
    }

    public long getIdProducto() {
        return idProducto;
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

    public int getIdProveedor() {
        return idProveedor;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    //          MÉTODOS DE ACCESO: SETTERS

    public void setIdDevolucion(long idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
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

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    //          MÉTODOS SOBREESCRITOS (@Override)

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Devolucion other = (Devolucion) obj;
        return idDevolucion == other.idDevolucion;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(idDevolucion);
    }

    @Override
    public String toString() {
        return "Devolucion{" +
                "idDevolucion=" + idDevolucion +
                ", idProducto=" + idProducto +
                ", cantidad=" + cantidad +
                ", motivo='" + motivo + '\'' +
                ", tipoDevolucion='" + tipoDevolucion + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}