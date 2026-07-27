package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.FacturaRepository;
import mx.uam.ayd.proyecto.datos.ProveedorRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Factura;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;

/**
 * Pruebas unitarias para la clase ServicioProveedor (HU-06).
 * Valida la lógica de negocio relativa a la consulta de proveedores,
 * cálculo de saldos pendientes y el procesamiento de pagos de facturas.
 * 
 * Estructurado bajo la metodología Gherkin (Given - When - Then).
 * 
 * @author JAVITOS
 */
@ExtendWith(MockitoExtension.class)
class ServicioProveedorTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private FacturaRepository facturaRepository;

    @InjectMocks
    private ServicioProveedor servicioProveedor;

    /**
     * Prueba el Caso 1: Recuperación de la entidad Proveedor junto con su
     * lista de facturas pendientes asociadas desde los repositorios.
     */
    @Test
    @DisplayName("Caso 1: Debería recuperar la información del proveedor y sus facturas pendientes")
    void testRecuperarProveedorYFacturasPendientes() {
        // GIVEN: Existe el proveedor 1L con facturas en estado "PENDIENTE" guardadas en el repositorio
        long idProveedor = 1L;
        
        // Se prepara el objeto Proveedor
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(idProveedor);
        proveedor.setNombreCompleto("Proveedor de Prueba");

        // Se prepara la lista de facturas adeudadas
        Factura factura1 = new Factura();
        factura1.setIdFactura(101L);
        factura1.setEstado("PENDIENTE");

        List<Factura> facturasPendientes = new ArrayList<>();
        facturasPendientes.add(factura1);

        // Definición del comportamiento de los Mocks
        when(proveedorRepository.findByIdProveedor(idProveedor)).thenReturn(proveedor);
        when(facturaRepository.findByIdProveedorAndEstadoIgnoreCase(idProveedor, "PENDIENTE"))
                .thenReturn(facturasPendientes);

        // WHEN: Se invocan los métodos recuperarProveedor(1L) y recuperarFacturasPendientes(1L)
        Proveedor proveedorResultado = servicioProveedor.recuperarProveedor(idProveedor);
        List<Factura> facturasResultado = servicioProveedor.recuperarFacturasPendientes(idProveedor);

        // THEN: Retorna el objeto Proveedor no nulo correspondiente y la lista con sus objetos Factura pendientes
        assertNotNull(proveedorResultado, "El proveedor no debe ser nulo");
        assertEquals(idProveedor, proveedorResultado.getIdProveedor(), "El ID del proveedor debe coincidir");
        assertNotNull(facturasResultado, "La lista de facturas no debe ser nula");
        assertEquals(1, facturasResultado.size(), "Debe retornar exactamente 1 factura pendiente");
        assertEquals("PENDIENTE", facturasResultado.get(0).getEstado(), "El estado de la factura debe ser PENDIENTE");

        // Verificación de llamadas a los métodos del repositorio
        verify(proveedorRepository, times(1)).findByIdProveedor(idProveedor);
        verify(facturaRepository, times(1)).findByIdProveedorAndEstadoIgnoreCase(idProveedor, "PENDIENTE");
    }

    /**
     * Prueba el Caso 2: Suma correcta de saldos individuales para obtener
     * el saldo total pendiente del proveedor.
     */
    @Test
    @DisplayName("Caso 2: Debería calcular correctamente el saldo pendiente total de un proveedor")
    void testCalcularSaldoPendienteProveedor() {
        // GIVEN: El proveedor 1L tiene facturas pendientes por $1,250.50 y $950.20
        long idProveedor = 1L;

        Factura factura1 = new Factura();
        factura1.setSaldoPendiente(1250.50);

        Factura factura2 = new Factura();
        factura2.setSaldoPendiente(950.20);

        List<Factura> facturasPendientes = new ArrayList<>();
        facturasPendientes.add(factura1);
        facturasPendientes.add(factura2);

        // Configuración de respuesta simulada del repositorio de facturas
        when(facturaRepository.findByIdProveedorAndEstadoIgnoreCase(idProveedor, "PENDIENTE"))
                .thenReturn(facturasPendientes);

        // WHEN: Se invoca el método calcularSaldoPendienteProveedor(1L)
        double saldoCalculado = servicioProveedor.calcularSaldoPendienteProveedor(idProveedor);

        // THEN: Regresa el valor flotante/double 2200.70
        assertEquals(2200.70, saldoCalculado, 0.001, "El saldo calculado debe ser igual a 2200.70");
        verify(facturaRepository, times(1)).findByIdProveedorAndEstadoIgnoreCase(idProveedor, "PENDIENTE");
    }

    /**
     * Prueba el Caso 3: Modificación y persistencia del estado de una factura
     * a "PAGADA" con un saldo restante de 0.0.
     */
    @Test
    @DisplayName("Caso 3: Debería actualizar saldo a 0.0 y estado a PAGADA al registrar el pago de una factura")
    void testRegistrarPago() {
        // GIVEN: La factura 10L existe en el repositorio con estado "PENDIENTE"
        long idFactura = 10L;

        Factura facturaPendiente = new Factura();
        facturaPendiente.setIdFactura(idFactura);
        facturaPendiente.setSaldoPendiente(1500.00);
        facturaPendiente.setEstado("PENDIENTE");

        // Simulación de búsqueda y persistencia de la entidad
        when(facturaRepository.findByIdFactura(idFactura)).thenReturn(facturaPendiente);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN: Se invoca el método registrarPago(10L)
        Factura facturaPagada = servicioProveedor.registrarPago(idFactura);

        // THEN: Retorna la entidad Factura actualizada con saldoPendiente = 0.0 y estado "PAGADA"
        assertNotNull(facturaPagada, "La factura procesada no debe ser nula");
        assertEquals(0.0, facturaPagada.getSaldoPendiente(), "El saldo pendiente debe cambiar a 0.0");
        assertEquals("PAGADA", facturaPagada.getEstado(), "El estado de la factura debe cambiar a PAGADA");

        // Verificación de interacción con la capa de datos
        verify(facturaRepository, times(1)).findByIdFactura(idFactura);
        verify(facturaRepository, times(1)).save(facturaPendiente);
    }
}