package cl.sgbe.dao;


import cl.sgbe.modelo.Prestamo;
import cl.sgbe.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    private Connection con;

    public PrestamoDAO() {
        this.con = DatabaseConnection.getInstance().getConexcion(); // conexión Singleton
    }

    // metodo para mecanismo de sincronizacion
    public synchronized boolean registrarPrestamo(int idEstudiante, int idLibro) {
        String sqlStock = "SELECT stock FROM libros WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlStock)) {
            ps.setInt(1, idLibro);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt("stock") > 0) {
                LocalDate hoy = LocalDate.now(); // usa la fecha actual
                LocalDate vence = hoy.plusDays(7); // Fecha de vencimiento automática

                // insertamos un prestamo
                String sqlPrestamo = "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, FALSE)";
                try (PreparedStatement psPrestamo = con.prepareStatement(sqlPrestamo)) {
                    psPrestamo.setInt(1, idEstudiante);
                    psPrestamo.setInt(2, idLibro);
                    psPrestamo.setDate(3, Date.valueOf(hoy));
                    psPrestamo.setDate(4, Date.valueOf(vence));
                    psPrestamo.executeUpdate();
                }

                // disminuir el stock en la tabla de libros
                String sqlUpdate = "UPDATE libros SET stock = stock - 1 WHERE id = ?";
                try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                    psUpdate.setInt(1, idLibro);
                    psUpdate.executeUpdate();
                }

                return true;
            } else {
                throw new RuntimeException("Stock insuficiente para realizar el préstamo.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage());
        }
    }

    // mostrar todos los prestamos
    public List<Prestamo> listar() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamos ORDER BY id DESC";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_estudiante"),
                        rs.getInt("id_libro"),
                        rs.getDate("fecha_prestamo"),
                        rs.getDate("fecha_devolucion"),
                        rs.getBoolean("devuelto")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar préstamos: " + e.getMessage());
        }
        return lista;
    }
    // metodo que filtra para que el estudiante solo vea sus prestamos
    public List<Prestamo> listarPorEstudiante(int idEstudiante) {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE id_estudiante = ? ORDER BY id DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_estudiante"),
                        rs.getInt("id_libro"),
                        rs.getDate("fecha_prestamo"),
                        rs.getDate("fecha_devolucion"),
                        rs.getBoolean("devuelto")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar préstamos del estudiante: " + e.getMessage());
        }
        return lista;
    }
    // marca el presa como devuelto y actualiza el stock

    public synchronized boolean devolverPrestamo(int idPrestamo) {
        String sqlBuscar = "SELECT id_libro, devuelto FROM prestamos WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
            ps.setInt(1, idPrestamo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getBoolean("devuelto")) {
                    throw new RuntimeException("Este libro ya fue devuelto previamente.");
                }
                int idLibro = rs.getInt("id_libro");

                // marca como devuelto
                String sqlUpdateP = "UPDATE prestamos SET devuelto = TRUE WHERE id = ?";
                try (PreparedStatement psUpP = con.prepareStatement(sqlUpdateP)) {
                    psUpP.setInt(1, idPrestamo);
                    psUpP.executeUpdate();
                }

                // actualiza el stock
                String sqlUpdateL = "UPDATE libros SET stock = stock + 1 WHERE id = ?";
                try (PreparedStatement psUpL = con.prepareStatement(sqlUpdateL)) {
                    psUpL.setInt(1, idLibro);
                    psUpL.executeUpdate();
                }

                return true;
            } else {
                throw new RuntimeException("No se encontró el registro de préstamo con ID " + idPrestamo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en base de datos: " + e.getMessage());
        }
    }
}
