package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entidad de negocio Pedido
 *
 * @author Yamelin, Guillermo, Dydier, Yael, Sheyla
 *
 */
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idPedido;

    private String numeroPedido;

    /*
     * Se conserva para no afectar el código existente.
     */
    @Column(name = "id_producto")
    private int idProducto;

    private String fecha;

    private int cantidad;

    private double total;

    /*
     * Se conserva para no afectar el código existente.
     */
    @Column(name = "id_proveedor")
    private int idProveedor;

    /*
     * Muchos pedidos pueden pertenecer al mismo producto.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "id_producto",
            insertable = false,
            updatable = false)
    private Producto producto;

    /*
     * Muchos pedidos pueden pertenecer al mismo proveedor.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "id_proveedor",
            insertable = false,
            updatable = false)
    private Proveedor proveedor;

    public Pedido() {
    }

    public long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(long idPedido) {
        this.idPedido = idPedido;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;

        if (producto != null) {
            this.idProducto = (int) producto.getIdProducto();
        }
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;

        if (proveedor != null) {
            this.idProveedor = (int) proveedor.getIdProveedor();
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Pedido other = (Pedido) obj;

        return idPedido == other.idPedido;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idPedido);
    }

    @Override
    public String toString() {
        return "Pedido [idPedido=" + idPedido
                + ", numeroPedido=" + numeroPedido
                + ", idProducto=" + idProducto
                + ", fecha=" + fecha
                + ", cantidad=" + cantidad
                + ", total=" + total
                + ", idProveedor=" + idProveedor + "]";
    }
}