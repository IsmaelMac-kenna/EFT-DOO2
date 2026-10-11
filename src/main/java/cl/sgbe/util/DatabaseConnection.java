package cl.sgbe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static  DatabaseConnection instance;
    private Connection conexion;

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "Admin";

    private DatabaseConnection() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexion establecida!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar con la biblioteca." + e.getMessage());

        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    public Connection getConexcion() {

        return conexion;
    }

    //para probar la conexión
/*
    public class Main {
        public static void main(String[] args) {
            try (Connection conn = ConexionBD.obtenerConexion()) {
                System.out.println(" Conexión exitosa a la base de datos.");
            } catch (SQLException e) {
                System.err.println(" Error al conectar con la base de datos:");
                e.printStackTrace();
            }
        }*/
}

