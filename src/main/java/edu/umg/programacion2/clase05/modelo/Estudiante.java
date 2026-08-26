package edu.umg.programacion2.clase05.modelo;

/**
 * Representa un estudiante tal como se guarda en la tabla `estudiantes`.
 *
 * IMPORTANTE: esta es una clase de dominio simple: solo datos + encapsulamiento
 * (atributos privados + getters/setters). No sabe nada de SQL ni de conexiones a
 * base de datos - esa responsabilidad es de EstudianteDAO. Separar "que es un
 * estudiante" de "como se guarda un estudiante" es una idea que van a ver una y
 * otra vez en el curso.
 */
public class Estudiante {

    private int id;
    private String nombre;
    private String carnet;
    private int activo;
    private Tipo tipo;

    public enum Tipo {
        Pregrado,
        Posgrado
    }
    

    // Constructor de conveniencia para cuando todavia no existe en la base de
    // datos (por eso id = 0: MySQL le va a asignar el id real al insertarlo).
	public Estudiante(int id, String nombre, String carnet, int activo, Tipo tipo) {
    this.id = id;
    this.nombre = nombre;
    this.carnet = carnet;
    this.activo = activo;
    this.tipo = tipo;
}

	  public Estudiante(String nombre, String carnet) {
	        this(0, nombre, carnet, 1, Tipo.Pregrado);
	    }

	    public int getId() {
	        return id;
	    }

	    public String getNombre() {
	        return nombre;
	    }

	    public void setNombre(String nombre) {
	        this.nombre = nombre;
	    }

	    public String getCarnet() {
	        return carnet;
	    }

	    public int getActivo() {
	        return activo;
	    }

	    public void setActivo(int activo) {
	        this.activo = activo;
	    }

	    public Tipo getTipo() {
	        return tipo;
	    }

	    public void setTipo(Tipo tipo) {
	        this.tipo = tipo;
	    }

	    @Override
	    public String toString() {
	        return String.format(
	            "[%d] %s - carnet %s - activo %d - tipo %s",
	            id, nombre, carnet, activo, tipo
	        );
	    }
	}