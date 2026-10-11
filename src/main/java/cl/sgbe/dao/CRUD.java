package cl.sgbe.dao;

import java.util.List;

public interface CRUD<T> {
    boolean crear(T objeto);
    List<T> listar();
    T buscarPorId(int id);
    boolean actualizar(T objeto);
    boolean eliminar(int id);
}