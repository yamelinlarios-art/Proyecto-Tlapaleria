package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Inventario;

/**
 * Repositorio para el control de Inventario
 *
 * @author KEVIN DYDIER, yael Mora Simón
 */

public interface InventarioRepository extends CrudRepository<Inventario, Long> {

    /**
     * Recupera el registro de inventario de un producto específico.
     *
     * @param idProducto
     * @return el registro de inventario
     */
    public Inventario findByIdProducto(long idProducto);

}

// Este repositorio me ayuda a HU-03 en que  me da la información numérica del stock (cuántos hay y cuál es el mínimo).

