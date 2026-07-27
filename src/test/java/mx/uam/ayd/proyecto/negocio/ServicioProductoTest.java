package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Pruebas unitarias para el ServicioProducto (HU-09).
 * Valida la lógica de negocio relacionada con la actualización de precios 
 * y las reglas de validación de datos.
 */
@ExtendWith(MockitoExtension.class)
class ServicioProductoTest {

    // Simulamos el repositorio de productos
    @Mock
    private ProductoRepository productoRepository;

    // Simulamos el repositorio de movimientos para registrar la bitácora del cambio de precio
    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    // Inyectamos los mocks en el servicio de productos que vamos a probar
    @InjectMocks
    private ServicioProducto servicioProducto;

    /**
     * Prueba 1: Valida el flujo exitoso de actualización de precio de un producto.
     * Comprueba que se modifique el precio, se guarde en el repositorio de productos 
     * y se genere su respectivo registro en la bitácora de movimientos.
     */
    @Test
    void testActualizarPrecioProductoExitoso() {
        long idProducto = 1L;
        double nuevoPrecio = 55.50;

        // Preparamos un producto simulado con un precio anterior
        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        productoExistente.setNombre("Martillo");
        productoExistente.setPrecio(40.00);

        // Configuramos el comportamiento de los mocks
        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenReturn(new MovimientoInventario());

        // Ejecutamos el método a probar
        Producto resultado = servicioProducto.actualizarPrecioProducto(idProducto, nuevoPrecio);

        // Verificaciones
        assertNotNull(resultado, "El producto actualizado no debe ser nulo");
        assertEquals(nuevoPrecio, resultado.getPrecio(), "El precio del producto debió actualizarse al nuevo valor");
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class));
    }

    /**
     * Prueba 2: Valida que el sistema rechace la operación lanzando una excepción 
     * cuando se intenta actualizar el precio de un producto que no existe en la base de datos.
     */
    @Test
    void testActualizarPrecioProductoInexistente() {
        long idInexistente = 99L;
        when(productoRepository.findByIdProducto(idInexistente)).thenReturn(null);

        // Se espera una excepción al no encontrar el producto
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idInexistente, 50.0);
        });
    }

    /**
     * Prueba 3: Valida las reglas de negocio sobre los valores monetarios permitidos.
     * Comprueba que se rechacen precios iguales a cero ($0.0$) o negativos ($\le 0$).
     */
    @Test
    void testActualizarPrecioValoresInvalidos() {
        long idProducto = 1L;
        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        
        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);

        // Caso A: Intentar actualizar con precio cero debe lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idProducto, 0.0);
        });

        // Caso B: Intentar actualizar con precio negativo debe lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idProducto, -5.0);
        });
    }
}