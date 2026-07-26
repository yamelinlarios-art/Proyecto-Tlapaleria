package mx.uam.ayd.proyecto.datos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;

/**
 * Repositorio para la gestión persistente de los movimientos de inventario.
 *
 * @author Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
public interface MovimientoInventarioRepository extends CrudRepository<MovimientoInventario, Long> {

    /**
     * Recupera todos los movimientos de inventario ordenados por fecha descendente,
     * trayendo la relación con Producto en la misma consulta para evitar excepciones perezosas.
     *
     * @return Lista de movimientos
     */
    @Query("SELECT m FROM MovimientoInventario m LEFT JOIN FETCH m.producto ORDER BY m.fecha DESC")
    List<MovimientoInventario> findAllByOrderByFechaDesc();

    /**
     * Busca movimientos por su tipo ignorando mayúsculas/minúsculas y los ordena por fecha.
     *
     * @param tipoMovimiento Cadena o patrón a buscar
     * @return Lista de movimientos coincidentes
     */
    @Query("SELECT m FROM MovimientoInventario m LEFT JOIN FETCH m.producto WHERE LOWER(m.tipoMovimiento) LIKE LOWER(CONCAT('%', :tipoMovimiento, '%')) ORDER BY m.fecha DESC")
    List<MovimientoInventario> findByTipoMovimientoContainingIgnoreCaseOrderByFechaDesc(@Param("tipoMovimiento") String tipoMovimiento);
}