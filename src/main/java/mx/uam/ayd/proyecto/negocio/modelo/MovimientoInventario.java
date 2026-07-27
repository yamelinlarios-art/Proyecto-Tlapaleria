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
 * las existencias de un producto. Funciona como la bitácora o historial
 * que se alimenta tanto al cambiar precios (HU-09) como al registrar
 * devoluciones y perdidas (HU-10).
 *
 * @author Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Entity
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idMovimiento; // Identificador único generado automáticamente para cada registro en la bitácora

    private LocalDateTime fecha; // Fecha y hora exacta de cuándo ocurrió el cambio (indispensable para ordenar el historial en HU-09 y HU-10)

    private String tipoMovimiento; // Define la naturaleza del cambio: ej. "CAMBIO_PRECIO" (HU-09) o "DEVOLUCION_DANADO" (HU-10)

    private int cantidad; // Número de piezas involucradas en la acción (ej. cuántas piezas se devuelven en HU-10)

    private int existenciaAnterior; // Stock que tenía el producto antes de tocarlo (vital para auditoría en HU-10)

    private int existenciaActual; // Stock final que queda tras aplicar la modificación (calculado y guardado en HU-10)

    private String observacion; // Notas o razones explicativas que escribe el usuario (ej. "Caja aplastada" o "Ajuste por inflación")

    /**
     * Producto afectado por el movimiento.
     * Se configura FetchType.EAGER para asegurar que los datos del producto 
     * viajen completos y se evite el error 'LazyInitializationException' 
     * al momento de mostrarlos en las tablas de la interfaz de JavaFX.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    public MovimientoInventario() {
    }

    // GETTERS

    /** Obtiene el ID único del movimiento en la base de datos */
    public long getIdMovimiento() {
        return idMovimiento;
    }

    /** Obtiene la fecha y hora exacta del registro */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /** Obtiene el tipo de movimiento realizado (clave para distinguir HU-09 de HU-10) */
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    /** Obtiene la cantidad de piezas afectadas */
    public int getCantidad() {
        return cantidad;
    }

    /** Obtiene el stock que había antes del movimiento (clave para respaldar HU-10) */
    public int getExistenciaAnterior() {
        return existenciaAnterior;
    }

    /** Obtiene el stock resultante después de la operación */
    public int getExistenciaActual() {
        return existenciaActual;
    }

    /** Obtiene la nota u observación escrita por el usuario */
    public String getObservacion() {
        return observacion;
    }

    /** 
     * Obtiene el objeto Producto completo vinculado a este movimiento.
     * Esencial para consultar sus datos desde la vista de la HU-09 y HU-10.
     */
    public Producto getProducto() {
        return producto;
    }

    // SETTERS

    /** Asigna o modifica el ID del movimiento */
    public void setIdMovimiento(long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    /** Asigna la estampa de tiempo del movimiento */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /** Define el tipo de movimiento que se está guardando (HU-09 / HU-10) */
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    /** Establece la cantidad de piezas involucradas */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /** Guarda el stock previo a la modificación */
    public void setExistenciaAnterior(int existenciaAnterior) {
        this.existenciaAnterior = existenciaAnterior;
    }

    /** Guarda el nuevo stock calculado */
    public void setExistenciaActual(int existenciaActual) {
        this.existenciaActual = existenciaActual;
    }

    /** Permite adjuntar un comentario o justificación al movimiento */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /** Conecta este registro de bitácora con el Producto correspondiente */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /**
     * Valida si dos objetos del MovimientoInventario son idénticos.
     * Compara si apuntan al mismo espacio en memoria o si 
     * tienen exactamente el mismo 'idMovimiento', lo cual es clave para que Spring 
     * y JavaFX sepan si un elemento de la lista ya existe o se repite.
     */
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

    /**
     * Genera un código hash numérico basado exclusivamente en el 'idMovimiento'.
     * lo usamos internamente para organizar y buscar los movimientos de forma rápida.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(idMovimiento);
    }

    /**
     * Devuelve una cadena de texto formateada con todos los atributos principales.
     * Este método nos salva al hacer pruebas o imprimir en consola 
     * (System.out.println), permitiendo ver de un vistazo rápido la fecha, tipo, 
     * existencias y a qué ID de producto pertenece el movimiento.
     */
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