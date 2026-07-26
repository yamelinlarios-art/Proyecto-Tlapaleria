package mx.uam.ayd.proyecto.negocio.modelo;

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

/**
 * Entidad de negocio Producto
 *
 * @author Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idProducto;

    private String clave;

    private String nombre;

    private String tipoProducto;

    /* Usado para HU09 */
    private Double precio;

    private String marca;

    private String categoria;

    private Double precioCompra;

    private int existenciaActual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inventario")
    private Inventario inventario;

    @OneToMany(mappedBy = "producto")
    private List<MovimientoInventario> movimientosInventario = new ArrayList<>();

    @OneToMany(mappedBy = "producto")
    private List<DescripcionVenta> descripcionesVenta = new ArrayList<>();

    @OneToMany(mappedBy = "producto")
    private List<Devolucion> devoluciones = new ArrayList<>();

    @OneToMany(mappedBy = "producto")
    private List<Pedido> pedidos = new ArrayList<>();

    public Producto() {
    }

    // GETTERS

    public long getIdProducto() {
        return idProducto;
    }

    public String getClave() {
        return clave;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    public Double getPrecio() {
        return precio;
    }

    public String getMarca() {
        return marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public int getExistenciaActual() {
        return existenciaActual;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public List<MovimientoInventario> getMovimientosInventario() {
        return movimientosInventario;
    }

    public List<DescripcionVenta> getDescripcionesVenta() {
        return descripcionesVenta;
    }

    public List<Devolucion> getDevoluciones() {
        return devoluciones;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    // SETTERS

    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public void setExistenciaActual(int existenciaActual) {
        this.existenciaActual = existenciaActual;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public void setMovimientosInventario(List<MovimientoInventario> movimientosInventario) {
        this.movimientosInventario = movimientosInventario;
    }

    public void setDescripcionesVenta(List<DescripcionVenta> descripcionesVenta) {
        this.descripcionesVenta = descripcionesVenta;
    }

    public void setDevoluciones(List<Devolucion> devoluciones) {
        this.devoluciones = devoluciones;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Producto other = (Producto) obj;
        return idProducto == other.idProducto;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(idProducto);
    }

    @Override
    public String toString() {
        return "Producto [idProducto=" + idProducto
                + ", clave='" + clave + '\''
                + ", nombre='" + nombre + '\''
                + ", precio=" + precio
                + ", stock=" + existenciaActual + "]";
    }
}