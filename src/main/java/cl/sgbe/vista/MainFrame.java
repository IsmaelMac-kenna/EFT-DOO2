package cl.sgbe.vista;

import cl.sgbe.controlador.LoginController;
import cl.sgbe.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainFrame extends JFrame {

    // atributos
    private Usuario usuarioActual;
    private String rolUsuario;
    private CardLayout cardLayout;
    private JPanel panelCentral;

    // modulo libros
    private JTable tablaLibros;
    private DefaultTableModel modeloTablaLibros;
    private JTextField txtBuscarLibro;
    private JButton btnNuevoLibro;
    private JButton btnEditarLibro;
    private JButton btnEliminarLibro;
    private JButton btnActualizarLibro;

    // modulo prestamos
    private JTable tablaPrestamos;
    private DefaultTableModel modeloTablaPrestamos;
    private JTextField txtIdEstudiantePrestamo;
    private JTextField txtIdLibroPrestamo;
    private JButton btnRegistrarPrestamo;
    private JButton btnDevolverPrestamo;
    private JButton btnActualizarPrestamos;

    // modulo estudiante
    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTablaEstudiantes;
    private JButton btnNuevoEstudiante;
    private JButton btnActualizarEstudiantes;

    // modulo reportes
    private JTextArea txtAreaReportes;
    private JButton btnReporteMasPrestados;
    private JButton btnReporteEnPrestamo;

    // modulo usuarios
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTablaUsuarios;
    private JButton btnNuevoUsuario;
    private JButton btnActualizarUsuarios;

    // autentificacion para que los campos del estudante sean visibles y no pueda acceder a la informacion completa del bibliotecario
    public MainFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        this.rolUsuario = (usuario != null) ? usuario.getRol() : "estudiante";

        String nombre = (usuario != null) ? usuario.getNombre() : "Usuario";
        setTitle("Sistema de Gestión de Biblioteca - " + nombre + " (" + rolUsuario.toUpperCase() + ")");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // sidebar lateral lo mas similar al ejemplo
        JPanel sidebar = crearSidebar();
        add(sidebar, BorderLayout.WEST);

        // panel central
        cardLayout = new CardLayout();
        panelCentral = new JPanel(cardLayout);

        panelCentral.add(crearPanelLibros(), "LIBROS");
        panelCentral.add(crearPanelPrestamos(), "PRESTAMOS");
        panelCentral.add(crearPanelEstudiantes(), "ESTUDIANTES");
        panelCentral.add(crearPanelReportes(), "REPORTES");
        panelCentral.add(crearPanelUsuarios(), "USUARIOS");

        add(panelCentral, BorderLayout.CENTER);

        // si es estudiante fijamos su ID en el campo de prestamos y bloqueamos su edicion
        if ("estudiante".equalsIgnoreCase(this.rolUsuario) && usuario != null) {
            txtIdEstudiantePrestamo.setText(String.valueOf(usuario.getId()));
            txtIdEstudiantePrestamo.setEditable(false);
        }
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(7, 1, 5, 5));
        sidebar.setBorder(BorderFactory.createTitledBorder("Menú"));

        JButton btnLibros = new JButton("Libros / Catálogo");
        JButton btnPrestamos = new JButton("Préstamos");
        JButton btnEstudiantes = new JButton("Estudiantes");
        JButton btnReportes = new JButton("Reportes");
        JButton btnUsuarios = new JButton("Usuarios");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");

        btnLibros.addActionListener(e -> cardLayout.show(panelCentral, "LIBROS"));
        btnPrestamos.addActionListener(e -> cardLayout.show(panelCentral, "PRESTAMOS"));
        btnEstudiantes.addActionListener(e -> cardLayout.show(panelCentral, "ESTUDIANTES"));
        btnReportes.addActionListener(e -> cardLayout.show(panelCentral, "REPORTES"));
        btnUsuarios.addActionListener(e -> cardLayout.show(panelCentral, "USUARIOS"));

        btnCerrarSesion.addActionListener(e -> {
            dispose();
            LoginFrame login = new LoginFrame();
            new LoginController(login);
            login.setVisible(true);
        });

        sidebar.add(btnLibros);
        sidebar.add(btnPrestamos);

        if ("bibliotecario".equalsIgnoreCase(rolUsuario)) {
            sidebar.add(btnEstudiantes);
            sidebar.add(btnReportes);
            sidebar.add(btnUsuarios);
        } else {
            btnEstudiantes.setEnabled(false);
            btnReportes.setEnabled(false);
            btnUsuarios.setEnabled(false);
        }

        sidebar.add(new JLabel(""));
        sidebar.add(btnCerrarSesion);

        return sidebar;
    }

    private JPanel crearPanelLibros() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorte.add(new JLabel("Buscar Libro: "));
        txtBuscarLibro = new JTextField(15);
        panelNorte.add(txtBuscarLibro);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevoLibro = new JButton("Nuevo");
        btnEditarLibro = new JButton("Editar");
        btnEliminarLibro = new JButton("Eliminar");
        btnActualizarLibro = new JButton("Actualizar");
        // filtro para que el estudiante no puedaeditac campos propios del bibliotecario
        if (!"bibliotecario".equalsIgnoreCase(rolUsuario)) {
            btnNuevoLibro.setEnabled(false);
            btnEditarLibro.setEnabled(false);
            btnEliminarLibro.setEnabled(false);
        }

        panelAcciones.add(btnNuevoLibro);
        panelAcciones.add(btnEditarLibro);
        panelAcciones.add(btnEliminarLibro);
        panelAcciones.add(btnActualizarLibro);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelNorte, BorderLayout.NORTH);
        panelSuperior.add(panelAcciones, BorderLayout.SOUTH);

        panel.add(panelSuperior, BorderLayout.NORTH);

        String[] columnas = {"ID", "Título", "Autor", "Categoría", "Stock"};
        modeloTablaLibros = new DefaultTableModel(columnas, 0);
        tablaLibros = new JTable(modeloTablaLibros);

        panel.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPrestamos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Gestión de Préstamo"));

        panelForm.add(new JLabel("ID Estudiante:"));
        txtIdEstudiantePrestamo = new JTextField(5);
        panelForm.add(txtIdEstudiantePrestamo);

        panelForm.add(new JLabel("ID Libro:"));
        txtIdLibroPrestamo = new JTextField(5);
        panelForm.add(txtIdLibroPrestamo);

        btnRegistrarPrestamo = new JButton("Registrar Préstamo");
        btnDevolverPrestamo = new JButton("Devolver Libro");
        btnActualizarPrestamos = new JButton("Actualizar Lista");

        panelForm.add(btnRegistrarPrestamo);
        panelForm.add(btnDevolverPrestamo);
        panelForm.add(btnActualizarPrestamos);

        panel.add(panelForm, BorderLayout.NORTH);

        String[] colPrestamos = {"ID Préstamo", "ID Estudiante", "ID Libro", "Fecha Préstamo", "Fecha Devolución", "Estado"};
        modeloTablaPrestamos = new DefaultTableModel(colPrestamos, 0);
        tablaPrestamos = new JTable(modeloTablaPrestamos);

        panel.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEstudiantes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevoEstudiante = new JButton("Nuevo Estudiante");
        btnActualizarEstudiantes = new JButton("Actualizar");

        panelAcciones.add(btnNuevoEstudiante);
        panelAcciones.add(btnActualizarEstudiantes);

        panel.add(panelAcciones, BorderLayout.NORTH);

        String[] cols = {"ID", "Nombre", "RUT", "Curso", "Correo"};
        modeloTablaEstudiantes = new DefaultTableModel(cols, 0);
        tablaEstudiantes = new JTable(modeloTablaEstudiantes);

        panel.add(new JScrollPane(tablaEstudiantes), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnReporteMasPrestados = new JButton("Libros más Prestados");
        btnReporteEnPrestamo = new JButton("Libros Actualmente en Préstamo");

        panelAcciones.add(btnReporteMasPrestados);
        panelAcciones.add(btnReporteEnPrestamo);

        panel.add(panelAcciones, BorderLayout.NORTH);

        txtAreaReportes = new JTextArea();
        txtAreaReportes.setEditable(false);
        txtAreaReportes.setFont(new Font("Monospaced", Font.PLAIN, 12));

        panel.add(new JScrollPane(txtAreaReportes), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuevoUsuario = new JButton("Registrar Nuevo Usuario");
        btnActualizarUsuarios = new JButton("Actualizar");

        panelAcciones.add(btnNuevoUsuario);
        panelAcciones.add(btnActualizarUsuarios);

        panel.add(panelAcciones, BorderLayout.NORTH);

        String[] cols = {"ID", "Nombre", "RUT", "Correo", "Rol"};
        modeloTablaUsuarios = new DefaultTableModel(cols, 0);
        tablaUsuarios = new JTable(modeloTablaUsuarios);

        panel.add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);
        return panel;
    }

    // aqui acomodamos todos los setters
    public JTable getTablaLibros() {
        return tablaLibros; }
    public DefaultTableModel getModeloTablaLibros() {
        return modeloTablaLibros; }
    public JTextField getTxtBuscarLibro() {
        return txtBuscarLibro; }
    public JButton getBtnNuevoLibro() {
        return btnNuevoLibro; }
    public JButton getBtnEditarLibro() {
        return btnEditarLibro; }
    public JButton getBtnEliminarLibro() {
        return btnEliminarLibro; }
    public JButton getBtnActualizarLibro() {
        return btnActualizarLibro; }

    public JTable getTablaPrestamos() {
        return tablaPrestamos; }
    public DefaultTableModel getModeloTablaPrestamos() {
        return modeloTablaPrestamos; }
    public JTextField getTxtIdEstudiantePrestamo() {
        return txtIdEstudiantePrestamo; }
    public JTextField getTxtIdLibroPrestamo() {
        return txtIdLibroPrestamo; }
    public JButton getBtnRegistrarPrestamo() {
        return btnRegistrarPrestamo; }
    public JButton getBtnDevolverPrestamo() {
        return btnDevolverPrestamo; }
    public JButton getBtnActualizarPrestamos() {
        return btnActualizarPrestamos; }

    public JTable getTablaEstudiantes() {
        return tablaEstudiantes; }
    public DefaultTableModel getModeloTablaEstudiantes() {
        return modeloTablaEstudiantes; }
    public JButton getBtnNuevoEstudiante() {
        return btnNuevoEstudiante; }
    public JButton getBtnActualizarEstudiantes() {
        return btnActualizarEstudiantes; }

    public JTextArea getTxtAreaReportes() {
        return txtAreaReportes; }
    public JButton getBtnReporteMasPrestados() {
        return btnReporteMasPrestados; }
    public JButton getBtnReporteEnPrestamo() {
        return btnReporteEnPrestamo; }

    public JTable getTablaUsuarios() {
        return tablaUsuarios; }
    public DefaultTableModel getModeloTablaUsuarios() {
        return modeloTablaUsuarios; }
    public JButton getBtnNuevoUsuario() {
        return btnNuevoUsuario; }
    public JButton getBtnActualizarUsuarios() {
        return btnActualizarUsuarios; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            new LoginController(login);
            login.setVisible(true);
        });
    }
}