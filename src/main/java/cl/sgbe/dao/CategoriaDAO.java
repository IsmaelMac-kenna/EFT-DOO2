package cl.sgbe.dao;

import cl.sgbe.modelo.Categorias;
import cl.sgbe.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO implements CRUD<Categorias> {
    private Connection con;

    public CategoriaDAO() {
        this.con = DatabaseConnection.getInstance().getConexcion(); // Conexión Singleton
    }

    // sobre escritura para el uso de contrato en la interfaz CRUD

    @Override
    public boolean crear(Categorias categorias) {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categorias.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Categorias> listar() {
        List<Categorias> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Categorias(
                        rs.getInt("id"),
                        rs.getString("nombre")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Categorias buscarPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Categorias(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar categoría: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean actualizar(Categorias categorias) {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categorias.getNombre());
            ps.setInt(2, categorias.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
}