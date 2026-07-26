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
 * Entidad de negocio MovimientoInventario.
 *
 * Representa una entrada, salida o modificación realizada sobre
 * las existencias de un producto.
 *
 * @author Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Entity
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idMovimiento;

    private LocalDateTime fecha;

    private String tipoMovimiento;

    private int cantidad;

    private int existenciaAnterior;

    private int existenciaActual;

    private String observacion;

    /**
     * Producto afectado por el movimiento.
     * Se configura FetchType.EAGER para evitar LazyInitializationException 
     * al renderizar en la vista de JavaFX.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    public MovimientoInventario() {
    }

    public long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getExistenciaAnterior() {
        return existenciaAnterior;
    }

    public void setExistenciaAnterior(int existenciaAnterior) {
        this.existenciaAnterior = existenciaAnterior;
    }

    public int getExistenciaActual() {
        return existenciaActual;
    }

    public void setExistenciaActual(int existenciaActual) {
        this.existenciaActual = existenciaActual;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MovimientoInventario other = (MovimientoInventario) obj;
        return idMovimiento == other.idMovimiento;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(idMovimiento);
    }

    @Override
    public String toString() {
        return "MovimientoInventario [idMovimiento=" + idMovimiento
                + ", fecha=" + fecha
                + ", tipoMovimiento=" + tipoMovimiento
                + ", cantidad=" + cantidad
                + ", existenciaAnterior=" + existenciaAnterior
                + ", existenciaActual=" + existenciaActual
                + ", observacion=" + observacion
                + ", idProducto=" + (producto != null ? producto.getIdProducto() : null)
                + "]";
    }
}