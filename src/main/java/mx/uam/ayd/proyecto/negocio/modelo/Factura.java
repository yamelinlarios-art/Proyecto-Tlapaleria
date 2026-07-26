package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDate;

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

    private String numeroFactura; // Requisito HU-06 (ej: "FAC-2026-089")

    private LocalDate fechaEmision; // Requisito HU-06

    private LocalDate fechaVencimiento; // Requisito HU-06

    @Column(name = "ID_PROVEEDOR")
    private long idProveedor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "ID_PROVEEDOR",
            insertable = false,
            updatable = false)
    private Proveedor proveedor;

    private double montoTotal;

    private double saldoPendiente;

    private String estado; // ej. "PENDIENTE", "PAGADA"

    public Factura() {
    }

    // --- GETTERS Y SETTERS ---

    public long getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(long idFactura) {
        this.idFactura = idFactura;
    }

    public String getNumeroFactura() {
        return numeroFactura != null ? numeroFactura : "FAC-" + idFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public long getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(long idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;

        if (proveedor != null) {
            this.idProveedor = proveedor.getIdProveedor();
        }
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Helper para la vista: indica si la factura aún tiene saldo adeudado
     */
    public boolean esPendiente() {
        return "PENDIENTE".equalsIgnoreCase(this.estado) || this.saldoPendiente > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
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
                + ", numeroFactura=" + numeroFactura
                + ", idProveedor=" + idProveedor
                + ", montoTotal=" + montoTotal
                + ", saldoPendiente=" + saldoPendiente
                + ", estado=" + estado + "]";
    }
}