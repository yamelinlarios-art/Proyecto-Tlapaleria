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
 * Entidad de negocio Factura
 *
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 *
 */
@Entity
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idFactura;

    /*
     * Se conserva este atributo para no afectar
     * el funcionamiento actual del proyecto.
     */
    @Column(name = "id_proveedor")
    private int idProveedor;

    /*
     * Muchas facturas pueden pertenecer
     * al mismo proveedor.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "id_proveedor",
            insertable = false,
            updatable = false)
    private Proveedor proveedor;

    private double montoTotal;

    private double saldoPendiente;

    private String estado;

    public Factura() {
    }

    /**
     * @return the idFactura
     */
    public long getIdFactura() {
        return idFactura;
    }

    /**
     * @param idFactura the idFactura to set
     */
    public void setIdFactura(long idFactura) {
        this.idFactura = idFactura;
    }

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * @return proveedor asociado a la factura
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor proveedor asociado a la factura
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;

        if (proveedor != null) {
            this.idProveedor = (int) proveedor.getIdProveedor();
        }
    }

    /**
     * @return the montoTotal
     */
    public double getMontoTotal() {
        return montoTotal;
    }

    /**
     * @param montoTotal the montoTotal to set
     */
    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    /**
     * @return the saldoPendiente
     */
    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    /**
     * @param saldoPendiente the saldoPendiente to set
     */
    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
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

        Factura other = (Factura) obj;

        return idFactura == other.idFactura;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idFactura);
    }

    @Override
    public String toString() {
        return "Factura [idFactura=" + idFactura
                + ", idProveedor=" + idProveedor
                + ", montoTotal=" + montoTotal
                + ", saldoPendiente=" + saldoPendiente
                + ", estado=" + estado + "]";
    }
}