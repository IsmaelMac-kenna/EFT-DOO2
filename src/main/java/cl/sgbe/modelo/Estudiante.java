package cl.sgbe.modelo;

public class Estudiante extends Usuario{

    //ATRIBUTO
    private String curso;

    // CONSTRUCTOR
    public Estudiante(int id, String nombre, String rut, String correo, String contraseña, String rol, String curso) {
        super(id, nombre, rut, correo, contraseña, rol);
        this.curso = curso;
    }
    // GETTERS & SETTERS

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
}

