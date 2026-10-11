package cl.sgbe.dao;

import cl.sgbe.modelo.Libro;
import cl.sgbe.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO implements CRUD<Libro> {
    private Connection con;

    public LibroDAO() {
        this.con = DatabaseConnection.getInstance().getConexcion(); // Instancia Singleton
    }

    // sobre escritura para el uso de contrato en la interfaz CRUD

    @Override
    public boolean crear(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear libro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Libro> listar() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getString("editorial"),
                        rs.getInt("stock"),
                        rs.getInt("id_categoria")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar libros: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Libro buscarPorId(int id) {
        String sql = "SELECT * FROM libros WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getString("editorial"),
                        rs.getInt("stock"),
                        rs.getInt("id_categoria")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar libro: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            ps.setInt(7, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    // Búsqueda por título en tiempo real
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + titulo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getString("editorial"),
                        rs.getInt("stock"),
                        rs.getInt("id_categoria")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por título: " + e.getMessage());
        }
        return lista;
    }

    // --- MÉTODOS DE REPORTES REQUERIDOS EN MAINCONTROLLER ---

    public List<String> reporteLibrosMasPrestados() {
        List<String> reporte = new ArrayList<>();
        String sql = """
            SELECT l.titulo, COUNT(p.id) as total_prestamos
            FROM libros l
            JOIN prestamos p ON l.id = p.id_libro
            GROUP BY l.id, l.titulo
            ORDER BY total_prestamos DESC
            LIMIT 10
            """;
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                reporte.add(String.format("• %s → %d préstamo(s)", rs.getString("titulo"), rs.getInt("total_prestamos")));
            }
        } catch (SQLException e) {
            System.err.println("Error en reporte: " + e.getMessage());
        }
        return reporte;
    }

    public List<String> reporteLibrosEnPrestamo() {
        List<String> reporte = new ArrayList<>();
        String sql = """
            SELECT l.titulo, e.nombre as estudiante, p.fecha_prestamo, p.fecha_devolucion
            FROM prestamos p
            JOIN libros l ON p.id_libro = l.id
            JOIN estudiantes e ON p.id_estudiante = e.id
            WHERE p.devuelto = FALSE
            ORDER BY p.fecha_devolucion ASC
            """;
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                reporte.add(String.format("• Libro: %s | Solicitante: %s | Desde: %s | Vence: %s",
                        rs.getString("titulo"),
                        rs.getString("estudiante"),
                        rs.getDate("fecha_prestamo").toString(),
                        rs.getDate("fecha_devolucion").toString()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error en reporte: " + e.getMessage());
        }
        return reporte;
    }
}