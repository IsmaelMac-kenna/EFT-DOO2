package cl.sgbe.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    // geston de interfaz grafica para la autentificacion

    public LoginFrame() {
        setTitle("LOGIN SISTEMA BIBLIOTECA");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panel.add(txtUsuario);

        panel.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        btnIngresar = new JButton("Ingresar");
        panel.add(btnIngresar);

        add(panel);
    }

    public String getUsuario() {
        return txtUsuario.getText();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarBienvenida(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
    }

    public void agregarListenerIngresar(ActionListener listener) {
        btnIngresar.addActionListener(listener);
    }
}
