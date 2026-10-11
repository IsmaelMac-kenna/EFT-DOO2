package cl.sgbe.modelo;

public class Categorias {
    // atributos
    private int id;
    private String nombre;

    // constructor

    public Categorias(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // getters & setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
