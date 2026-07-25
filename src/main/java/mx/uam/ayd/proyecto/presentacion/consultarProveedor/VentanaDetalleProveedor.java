package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.Factura;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Ventana para ver el detalle de un proveedor y sus facturas pendientes (HU-06).
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Component
public class VentanaDetalleProveedor extends JFrame {

    private ControlProveedor control;
    private Proveedor proveedorActual;
    private JPanel contentPane;
    private JLabel lblNombreProveedor;
    private JLabel lblTelefono;
    private JLabel lblCorporativo;
    private JLabel lblSaldoTotal;
    private JTable tablaFacturas;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrarPago;
    private JButton btnRegresar;

    public VentanaDetalleProveedor() {
        setTitle("Detalle del Proveedor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 750, 480);

        contentPane = new JPanel(new BorderLayout(0, 15));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);

        // Panel Superior: Información General y Resumen Financiero
        JPanel panelInfo = new JPanel(new GridLayout(2, 2, 10, 10));
        lblNombreProveedor = new JLabel("Proveedor: ");
        lblNombreProveedor.setFont(new Font("Tahoma", Font.BOLD, 14));
        
        lblCorporativo = new JLabel("Corporativo: ");
        lblTelefono = new JLabel("Teléfono: ");
        
        lblSaldoTotal = new JLabel("Saldo Total Pendiente: $0.00");
        lblSaldoTotal.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblSaldoTotal.setForeground(Color.RED);

        panelInfo.add(lblNombreProveedor);
        panelInfo.add(lblSaldoTotal);
        panelInfo.add(lblCorporativo);
        panelInfo.add(lblTelefono);

        contentPane.add(panelInfo, BorderLayout.NORTH);

        // Tabla Facturas Pendientes
        String[] columnas = {"ID Factura", "Monto Total", "Saldo Pendiente", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaFacturas = new JTable(modeloTabla);
        tablaFacturas.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tablaFacturas.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tablaFacturas);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Botones inferiores
        JPanel panelBotones = new JPanel();
        btnRegistrarPago = new JButton("REGISTRAR PAGO");
        btnRegistrarPago.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnRegistrarPago.setBackground(new Color(34, 139, 34));
        btnRegistrarPago.setForeground(Color.WHITE);

        btnRegresar = new JButton("REGRESAR");
        btnRegresar.setFont(new Font("Tahoma", Font.PLAIN, 12));

        panelBotones.add(btnRegistrarPago);
        panelBotones.add(btnRegresar);
        contentPane.add(panelBotones, BorderLayout.SOUTH);

        // Eventos
        btnRegistrarPago.addActionListener(e -> {
            int fila = tablaFacturas.getSelectedRow();
            if (fila != -1) {
                long idFactura = (long) tablaFacturas.getValueAt(fila, 0);
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Desea registrar el pago de la factura seleccionada?", 
                    "Confirmar Pago", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    control.registrarPago(idFactura, proveedorActual.getIdProveedor());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una factura de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnRegresar.addActionListener(e -> {
            setVisible(false);
            control.regresar(); // Criterio 5: Regresar a la lista
        });
    }

    /**
     * Carga y muestra los datos del proveedor seleccionado[cite: 1].
     */
    public void muestra(ControlProveedor control, Proveedor proveedor, List<Factura> facturas, double saldoTotal) {
        this.control = control;
        this.proveedorActual = proveedor;

        lblNombreProveedor.setText("Proveedor: " + proveedor.getNombreCompleto());
        lblCorporativo.setText("Corporativo: " + (proveedor.getCorporativo() != null ? proveedor.getCorporativo() : "N/A"));
        lblTelefono.setText("Teléfono: " + (proveedor.getTelefono() != null ? proveedor.getTelefono() : "N/A"));
        lblSaldoTotal.setText(String.format("Saldo Total Pendiente: $%.2f", saldoTotal));

        modeloTabla.setRowCount(0);
        for (Factura f : facturas) {
            Object[] fila = {
                f.getIdFactura(),
                String.format("$%.2f", f.getMontoTotal()),
                String.format("$%.2f", f.getSaldoPendiente()),
                f.getEstado()
            };
            modeloTabla.addRow(fila);
        }

        setVisible(true);
    }
}