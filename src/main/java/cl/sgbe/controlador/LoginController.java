package cl.sgbe.controlador;

import cl.sgbe.dao.UsuarioDAO;
import cl.sgbe.modelo.Usuario;
import cl.sgbe.vista.LoginFrame;
import cl.sgbe.vista.MainFrame;

import javax.swing.*;

public class LoginController {

    private LoginFrame vista;
    private UsuarioDAO dao;

    public LoginController(LoginFrame vista) {
        this.vista = vista;
        this.dao = new UsuarioDAO();

        vista.agregarListenerIngresar(e -> {
            String usuario = vista.getUsuario();
            String password = vista.getPassword();

            Usuario user = dao.buscarPorCredenciales(usuario, password);
            if (user != null) {
                vista.mostrarBienvenida("Bienvenido(a) " + user.getNombre());
                vista.dispose(); // Cierra ventana de login

                try {
                    // abrimos el sistema usando autentificacion
                    MainFrame mainFrame = new MainFrame(user);
                    new MainController(mainFrame, user);
                    mainFrame.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Error al iniciar la ventana principal:\n" + ex.getMessage(),
                            "Error Crítico",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                vista.mostrarError("Usuario o contraseña incorrectos.");
            }
        });
    }
}