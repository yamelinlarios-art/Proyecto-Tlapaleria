package mx.uam.ayd.proyecto.presentacion.consultarProveedor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Ventana para visualizar el directorio de proveedores y sus saldos (HU-06).
 * 
 * @author JAVITOS, Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Component
public class VentanaProveedor extends JFrame {

    private ControlProveedor control;
    private JPanel contentPane;
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    private JLabel lblSaldoTotalGeneral;

    public VentanaProveedor() {
        setTitle("Directorio de Proveedores - La Nueva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 850, 500);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(0, 15));
        setContentPane(contentPane);

        // Encabezado
        JPanel panelHeader = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Directorio de Proveedores");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 18));
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        lblSaldoTotalGeneral = new JLabel("SALDO TOTAL PENDIENTE: $0.00");
        lblSaldoTotalGeneral.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblSaldoTotalGeneral.setForeground(Color.RED);
        panelHeader.add(lblSaldoTotalGeneral, BorderLayout.EAST);

        contentPane.add(panelHeader, BorderLayout.NORTH);

        // Tabla de Proveedores
        String[] columnas = {"ID", "Proveedor / Corporativo", "Teléfono", "Tipo", "Saldo Pendiente", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };

        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tablaProveedores.setRowHeight(30);

        // Evento de clic en una fila (Escenario 3: Consultar información)
        tablaProveedores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaProveedores.getSelectedRow();
                if (fila != -1) {
                    long idProveedor = (long) tablaProveedores.getValueAt(fila, 0);
                    control.consultarProveedor(idProveedor);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Muestra la ventana y llena la tabla con la lista de proveedores y sus saldos[cite: 1].
     */
    public void muestra(ControlProveedor control, List<Proveedor> proveedores) {
        this.control = control;
        modeloTabla.setRowCount(0); // Limpiar tabla

        double saldoGeneral = 0.0;

        for (Proveedor p : proveedores) {
            double saldoPendiente = control.obtenerSaldoPendiente((int) p.getIdProveedor());
            saldoGeneral += saldoPendiente;

            String etiquetaSaldo = saldoPendiente > 0 ? String.format("$%.2f", saldoPendiente) : "$0.00";
            String estatusLeyenda = saldoPendiente > 0 ? "SALDO PENDIENTE" : "AL DÍA";

            Object[] fila = {
                p.getIdProveedor(),
                p.getNombreCompleto() + " (" + (p.getCorporativo() != null ? p.getCorporativo() : "N/A") + ")",
                p.getTelefono(),
                p.getTipoProveedor(),
                etiquetaSaldo,
                estatusLeyenda
            };
            modeloTabla.addRow(fila);
        }

        // Mostrar saldo total en rojo
        lblSaldoTotalGeneral.setText(String.format("SALDO TOTAL PENDIENTE: $%.2f", saldoGeneral));

        // Personalizar colores de celdas (Rojo si hay deuda, Gris si no)[cite: 1]
        tablaProveedores.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
                if ("SALDO PENDIENTE".equals(value)) {
                    lbl.setForeground(Color.RED);
                } else {
                    lbl.setForeground(Color.GRAY);
                }
                return lbl;
            }
        });

        setVisible(true);
    }
}