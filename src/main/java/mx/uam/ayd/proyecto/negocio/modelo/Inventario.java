package mx.uam.ayd.proyecto.negocio.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entidad de negocio Inventario
 *
 * @author Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Entity
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idProducto;

    private int existenciaActual;

    private int existenciaDisponible;

    private int stockMinimo;

    /*
     * Mantenemos la lista pero aseguramos propagación de cambios (Cascade)
     */
    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();

    public Inventario() {
    }

    public long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public int getExistenciaActual() {
        return existenciaActual;
    }

    public void setExistenciaActual(int existenciaActual) {
        this.existenciaActual = existenciaActual;
        this.existenciaDisponible = existenciaActual; // Sincroniza existencia disponible automáticamente
    }

    public int getExistenciaDisponible() {
        return existenciaDisponible;
    }

    public void setExistenciaDisponible(int existenciaDisponible) {
        this.existenciaDisponible = existenciaDisponible;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    // ==========================================
    // Métodos de negocio requeridos
    // ==========================================

    public int obtenerExistenciaActual() {
        return this.existenciaActual;
    }

    /**
     * Actualiza tanto la existencia actual como la disponible al realizar una venta/salida.
     */
    public void actualizarExistencia(int cantidad) {
        this.existenciaActual = cantidad;
        this.existenciaDisponible = cantidad;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Inventario other = (Inventario) obj;
        return idProducto == other.idProducto;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idProducto);
    }

    @Override
    public String toString() {
        return "Inventario [idProducto=" + idProducto
                + ", existenciaActual=" + existenciaActual
                + ", existenciaDisponible=" + existenciaDisponible
                + ", stockMinimo=" + stockMinimo + "]";
    }
}