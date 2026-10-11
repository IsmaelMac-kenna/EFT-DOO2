package cl.sgbe.controlador;

import cl.sgbe.dao.*;
import cl.sgbe.modelo.*;
import cl.sgbe.vista.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MainController {

    // atributos
    private MainFrame vista;
    private Usuario usuarioActual;
    private LibroDAO libroDAO;
    private PrestamoDAO prestamoDAO;
    private EstudianteDAO estudianteDAO;
    private UsuarioDAO usuarioDAO;

    // CONSTRUCTOR
    public MainController(MainFrame vista, Usuario usuarioActual) {
        this.vista = vista;
        this.usuarioActual = usuarioActual;
        this.libroDAO = new LibroDAO();
        this.prestamoDAO = new PrestamoDAO();
        this.estudianteDAO = new EstudianteDAO();
        this.usuarioDAO = new UsuarioDAO();

        // Cargar tablas segun el rol
        if ("bibliotecario".equalsIgnoreCase(usuarioActual.getRol())) {
            cargarTablaEstudiantes();
            cargarTablaUsuarios();
        }
        // listernes de
        // libros
        this.vista.getBtnActualizarLibro().addActionListener(e -> cargarTablaLibros());
        this.vista.getBtnNuevoLibro().addActionListener(e -> agregarLibro());
        this.vista.getBtnEliminarLibro().addActionListener(e -> eliminarLibro());

        this.vista.getTxtBuscarLibro().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarLibro();
            }
        });

        // prestamos
        this.vista.getBtnRegistrarPrestamo().addActionListener(e -> registrarPrestamo());
        this.vista.getBtnActualizarPrestamos().addActionListener(e -> cargarTablaPrestamos());
        this.vista.getBtnDevolverPrestamo().addActionListener(e -> devolverPrestamoSeleccionado());

        // estudiantes
        this.vista.getBtnActualizarEstudiantes().addActionListener(e -> cargarTablaEstudiantes());
        this.vista.getBtnNuevoEstudiante().addActionListener(e -> agregarEstudiante());

        // reportes
        this.vista.getBtnReporteMasPrestados().addActionListener(e -> {
            List<String> rep = libroDAO.reporteLibrosMasPrestados();
            if (rep.isEmpty()) {
                vista.getTxtAreaReportes().setText("--- LIBROS MÁS PRESTADOS ---\n\nNo hay préstamos registrados.");
            } else {
                vista.getTxtAreaReportes().setText("--- LIBROS MÁS PRESTADOS ---\n\n" + String.join("\n", rep));
            }
        });

        this.vista.getBtnReporteEnPrestamo().addActionListener(e -> {
            List<String> rep = libroDAO.reporteLibrosEnPrestamo();
            if (rep.isEmpty()) {
                vista.getTxtAreaReportes().setText("--- LIBROS ACTUALMENTE EN PRÉSTAMO ---\n\nNo hay préstamos pendientes.");
            } else {
                vista.getTxtAreaReportes().setText("--- LIBROS ACTUALMENTE EN PRÉSTAMO ---\n\n" + String.join("\n", rep));
            }
        });

        // usuarios
        this.vista.getBtnActualizarUsuarios().addActionListener(e -> cargarTablaUsuarios());
        this.vista.getBtnNuevoUsuario().addActionListener(e -> agregarUsuario());
    }

    private void cargarTablaLibros() {
        DefaultTableModel m = vista.getModeloTablaLibros();
        m.setRowCount(0);
        for (Libro l : libroDAO.listar()) {
            m.addRow(new Object[]{l.getId(), l.getTitulo(), l.getAutor(), l.getIdCategoria(), l.getStock()});
        }
    }

    private void buscarLibro() {
        DefaultTableModel m = vista.getModeloTablaLibros();
        m.setRowCount(0);
        for (Libro l : libroDAO.buscarPorTitulo(vista.getTxtBuscarLibro().getText().trim())) {
            m.addRow(new Object[]{l.getId(), l.getTitulo(), l.getAutor(), l.getIdCategoria(), l.getStock()});
        }
    }
    // elementos de GUI o interfaz grafica
    private void agregarLibro() {
        try {
            String t = JOptionPane.showInputDialog(vista, "Título:");
            if (t == null || t.isEmpty()) return;
            String a = JOptionPane.showInputDialog(vista, "Autor:");
            String isbn = JOptionPane.showInputDialog(vista, "ISBN:");
            String ed = JOptionPane.showInputDialog(vista, "Editorial:");
            int st = Integer.parseInt(JOptionPane.showInputDialog(vista, "Stock:"));
            int cat = Integer.parseInt(JOptionPane.showInputDialog(vista, "ID Categoría:"));

            libroDAO.crear(new Libro(0, t, a, isbn, ed, st, cat));
            cargarTablaLibros();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Datos incorrectos.");
        }
    }

    private void eliminarLibro() {
        int fila = vista.getTablaLibros().getSelectedRow();
        if (fila >= 0) {
            int id = (int) vista.getModeloTablaLibros().getValueAt(fila, 0);
            libroDAO.eliminar(id);
            cargarTablaLibros();
        }
    }

    private void cargarTablaPrestamos() {
        DefaultTableModel m = vista.getModeloTablaPrestamos();
        m.setRowCount(0);
        // filtro visualizacion de prestamos
        List<Prestamo> lista;
        if ("bibliotecario".equalsIgnoreCase(usuarioActual.getRol())) {
            lista = prestamoDAO.listar(); // lo que ve el blibliotecario
        } else {
            lista = prestamoDAO.listarPorEstudiante(usuarioActual.getId()); // lo que ve el estudiante
        }
        // estado del prestamo
        for (Prestamo p : lista) {
            String estado = p.isDevuelto() ? "Devuelto" : "En préstamo";
            m.addRow(new Object[]{
                    p.getId(),
                    p.getIdEstudiante(),
                    p.getIdLibro(),
                    p.getFechaPrestamo(),
                    p.getFechaDevolucion(),
                    estado
            });
        }
    }
    // metodos que implementa el hilo
    private void registrarPrestamo() {
        try {
            int idEst = Integer.parseInt(vista.getTxtIdEstudiantePrestamo().getText().trim());
            int idLib = Integer.parseInt(vista.getTxtIdLibroPrestamo().getText().trim());

            new Thread(() -> {
                try {
                    boolean ok = prestamoDAO.registrarPrestamo(idEst, idLib);
                    SwingUtilities.invokeLater(() -> {
                        if (ok) {
                            JOptionPane.showMessageDialog(vista, "Préstamo realizado con éxito.");
                            cargarTablaLibros();
                            cargarTablaPrestamos();
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Ingresa IDs numéricos válidos.");
        }
    }

    private void devolverPrestamoSeleccionado() {
        int fila = vista.getTablaPrestamos().getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Selecciona una fila de la tabla de préstamos.");
            return;
        }

        int idPrestamo = (int) vista.getModeloTablaPrestamos().getValueAt(fila, 0);

        new Thread(() -> {
            try {
                boolean ok = prestamoDAO.devolverPrestamo(idPrestamo);
                SwingUtilities.invokeLater(() -> {
                    if (ok) {
                        JOptionPane.showMessageDialog(vista, "El libro ha sido devuelto.");
                        cargarTablaLibros();
                        cargarTablaPrestamos();
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void cargarTablaEstudiantes() {
        DefaultTableModel m = vista.getModeloTablaEstudiantes();
        m.setRowCount(0);
        for (Estudiante e : estudianteDAO.listar()) {
            m.addRow(new Object[]{e.getId(), e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()});
        }
    }

    private void agregarEstudiante() {
        try {
            String nom = JOptionPane.showInputDialog(vista, "Nombre Estudiante:");
            if (nom == null || nom.isEmpty()) return;
            String rut = JOptionPane.showInputDialog(vista, "RUT:");
            String curso = JOptionPane.showInputDialog(vista, "Curso:");
            String correo = JOptionPane.showInputDialog(vista, "Correo:");

            estudianteDAO.crear(new Estudiante(0, nom, rut, correo, "", "estudiante", curso));
            cargarTablaEstudiantes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al crear estudiante.");
        }
    }

    private void cargarTablaUsuarios() {
        DefaultTableModel m = vista.getModeloTablaUsuarios();
        m.setRowCount(0);
        for (Usuario u : usuarioDAO.listar()) {
            m.addRow(new Object[]{u.getId(), u.getNombre(), u.getRut(), u.getCorreo(), u.getRol()});
        }
    }

    private void agregarUsuario() {
        try {
            String nom = JOptionPane.showInputDialog(vista, "Nombre Usuario:");
            if (nom == null || nom.isEmpty()) return;
            String rut = JOptionPane.showInputDialog(vista, "RUT:");
            String correo = JOptionPane.showInputDialog(vista, "Correo:");
            String pass = JOptionPane.showInputDialog(vista, "Contraseña:");
            String rol = JOptionPane.showInputDialog(vista, "Rol (bibliotecario / estudiante):");

            usuarioDAO.crear(new Usuario(0, nom, rut, correo, pass, rol));
            cargarTablaUsuarios();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al registrar usuario.");
        }
    }
}