package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import mx.uam.ayd.proyecto.presentacion.historialMovimientos.ControlHistorialMovimientos;

/**
 * @author Kevin Dydier López Flores
 */
@Entity
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idVenta;

    private Double total = 0.0;

    private LocalDateTime date;

    /*
     * Se conserva el identificador original para evitar
     * afectar servicios, controladores o pruebas existentes.
     */
    @Column(name = "id_vendedor")
    private int idVendedor;

    /*
     * Muchas ventas pueden ser registradas por un vendedor.
     *
     * Se utiliza la misma columna de idVendedor, pero la relación
     * no escribe directamente la columna para evitar que JPA
     * intente mapearla dos veces.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "id_vendedor",
            insertable = false,
            updatable = false)
    private Vendedor vendedor;

    private double montoRecibido;

    private double cambio;

    /*
     * Una venta puede contener varios detalles.
     *
     * mappedBy = "venta" hace referencia al atributo venta
     * existente en DescripcionVenta.
     */
    @OneToMany(
            mappedBy = "venta",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<DescripcionVenta> productos = new ArrayList<>();

    public Venta() {
    }

    /**
     * Agrega un producto y su cantidad a la venta actual.
     * Crea un nuevo objeto DescripcionVenta, lo asocia al producto
     * y actualiza el total de la venta.
     *
     * @param producto El objeto Producto que se va a agregar.
     * @param cantidad La cantidad de unidades del producto.
     * @return true si el producto se agregó correctamente;
     *         false si el producto es nulo o la cantidad no es válida.
     */
    public boolean agregaProducto(
            Producto producto,
            int cantidad) {

        if (producto == null || cantidad <= 0) {
            return false;
        }

        /*
         * El constructor parametrizado asigna el producto,
         * la cantidad y el precio unitario.
         */
        DescripcionVenta detalle =
                new DescripcionVenta(producto, cantidad);

        /*
         * Se establece el lado propietario de la relación.
         * DescripcionVenta contiene la llave foránea id_venta.
         */
        detalle.setVenta(this);

        this.productos.add(detalle);

        calculaTotal();

        return true;
    }

    public void calculaTotal() {

        double acumulado = 0.0;

        for (DescripcionVenta detalle : productos) {

            if (detalle.getProducto() != null) {

                acumulado +=
                        detalle.getProducto().getPrecio()
                        * detalle.getCantidad();
            }
        }

        // Asigna el valor final a total
        this.total = acumulado;
    }

    public void agregarDetalle(DescripcionVenta detalle) {

        if (detalle != null) {
            detalle.setVenta(this);
            this.productos.add(detalle);
        }
    }

    // Métodos que se ocupan en el servicio Venta

    public void addDetalle(DescripcionVenta detalle) {
        this.agregarDetalle(detalle);
    }

    public void setFecha(LocalDateTime fecha) {
        this.date = fecha;
    }

    public void setPago(double pago) {
        this.montoRecibido = pago;
    }

    public long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(long idVenta) {
        this.idVenta = idVenta;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    /*
     * Además de asignar el objeto Vendedor, se sincroniza
     * idVendedor para conservar el comportamiento anterior.
     */
    public void setVendedor(Vendedor vendedor) {

        this.vendedor = vendedor;

        if (vendedor != null) {
            this.idVendedor =
                    (int) vendedor.getIdEmpleado();
        }
    }

    public double getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

    public List<DescripcionVenta> getProductos() {
        return productos;
    }

    /*
     * Se mantiene la relación bidireccional al reemplazar
     * la lista completa de detalles.
     */
    public void setProductos(
            List<DescripcionVenta> productos) {

        this.productos = productos;

        if (this.productos != null) {

            for (DescripcionVenta detalle
                    : this.productos) {

                if (detalle != null) {
                    detalle.setVenta(this);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null
                || getClass() != obj.getClass()) {

            return false;
        }

        Venta other = (Venta) obj;

        return idVenta == other.idVenta;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(idVenta);
    }

    @Override
    public String toString() {
        return "Venta{"
                + "id=" + idVenta
                + ", total=" + total
                + ", pago=" + montoRecibido
                + ", cambio=" + cambio
                + ", date=" + date
                + ", idVendedor=" + idVendedor
                + '}';
    }

    public static void muestraMovimientos(
            List<MovimientoInventario> movimientos) {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'muestraMovimientos'");
    }

    public static void muestra() {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'muestra'");
    }

    public static void setControl(
            ControlHistorialMovimientos
                    controlHistorialMovimientos) {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'setControl'");
    }
}