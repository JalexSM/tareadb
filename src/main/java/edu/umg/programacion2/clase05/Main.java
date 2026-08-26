package edu.umg.programacion2.clase05;

import edu.umg.programacion2.clase05.dao.EstudianteDAO;
import edu.umg.programacion2.clase05.modelo.Estudiante;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Clase 5 - CRUD completo de estudiantes contra MySQL, CON Maven.
 *
 * IMPORTANTE: el driver de MySQL llega al proyecto como dependencia declarada
 * en pom.xml; Maven lo descarga solo. La logica de negocio (Estudiante,
 * EstudianteDAO, este menu) es identica a la del proyecto
 * clase05-jdbc-sin-maven: lo unico que cambia entre ambos proyectos es como
 * llega el driver al classpath.
 *
 * Esta clase Main SOLO se encarga de mostrar el menu y leer lo que escribe el
 * usuario. Toda la logica de base de datos vive en EstudianteDAO. Esta
 * separacion (interfaz de consola vs. acceso a datos) es la misma idea que
 * usaran despues con interfaces y con la app de Android.
 */
public class Main {

	private static final Scanner teclado = new Scanner(System.in);
	private static final EstudianteDAO estudianteDAO = new EstudianteDAO();

	public static void main(String[] args) {
		int opcion;

		do {
			mostrarMenu();
			opcion = leerOpcion();

			switch (opcion) {
			case 1:
				agregarEstudiante();
				break;

			case 2:
				listarEstudiantes();
				break;

			case 3:
				buscarEstudiante();
				break;

			case 4:
				actualizarEstudiante();
				break;

			case 5:
				eliminarEstudiante();
				break;

			case 6:
				listarEstudiantesInactivos();
				break;

			case 7:
				System.out.println("Hasta luego.");
				break;

			default:
				System.out.println("Opcion invalida. Intenta de nuevo.");
			}
			System.out.println();
		} while (opcion != 7);

		teclado.close();
	}

	private static void mostrarMenu() {
		System.out.println("=== CRUD de Estudiantes (MySQL) ===");
		System.out.println("1. Agregar estudiante");
		System.out.println("2. Listar estudiantes activos");
		System.out.println("3. Buscar estudiante por carnet");
		System.out.println("4. Actualizar estudiante");
		System.out.println("5. Eliminar estudiante");
		System.out.println("6. Listar estudiantes inactivos");
		System.out.println("7. Salir");
		System.out.print("Elige una opcion: ");
	}

	// Cuidado: Scanner.nextInt() no consume el "Enter" (\n) que el usuario
	// presiona. Si despues llamas nextLine() sin este truco, esa lectura se
	// "salta" porque encuentra el \n que quedo pendiente. Por eso aqui se
	// valida con hasNextInt() y se consume la linea con nextLine() al final.
	private static int leerOpcion() {
		while (!teclado.hasNextInt()) {
			System.out.print("Escribe un numero valido: ");
			teclado.next();
		}
		int opcion = teclado.nextInt();
		teclado.nextLine();
		return opcion;
	}

	private static void agregarEstudiante() {

		System.out.print("Nombre: ");
		String nombre = teclado.nextLine();

		System.out.print("Carnet: ");
		String carnet = teclado.nextLine();

		// se agregan activo con su respectiva validacion para que no agregen otro valor
		int activo;
		do {
			System.out.print("Activo (1=activo, 0=inactivo): ");
			activo = teclado.nextInt();
			if (activo != 0 && activo != 1) {
				System.out.println("Debe ingresar solamente 1 o 0.");
			}
		} while (activo != 0 && activo != 1);
		teclado.nextLine(); // Limpiar el Enter

		// se agrega el tipo a seleccionar
		System.out.print("Tipo (Pregrado/Posgrado): ");
		String tipoTexto = teclado.nextLine();
		try {
			Estudiante.Tipo tipo = Estudiante.Tipo.valueOf(tipoTexto);
			Estudiante estudiante = new Estudiante(10, nombre, carnet, activo, tipo);
			int id = estudianteDAO.crear(estudiante);
			System.out.println("Estudiante creado con id " + id);
		} catch (IllegalArgumentException e) {
			System.out.println("Tipo invalido. Debe ser Pregrado o Posgrado.");
		} catch (SQLException e) {
			System.err.println("Error al crear el estudiante: " + e.getMessage());
		}
	}

	private static void listarEstudiantes() {
		try {
			List<Estudiante> estudiantes = estudianteDAO.listarTodos();
			if (estudiantes.isEmpty()) {
				System.out.println("No hay estudiantes registrados todavia.");
				return;
			}
			for (Estudiante estudiante : estudiantes) {
				System.out.println(estudiante);
			}
		} catch (SQLException e) {
			System.err.println("Error al listar los estudiantes: " + e.getMessage());
		}
	}

	private static void buscarEstudiante() {
		System.out.print("Carnet a buscar: ");
		String carnet = teclado.nextLine();

		try {
			Optional<Estudiante> estudiante = estudianteDAO.buscarPorCarnet(carnet);
			if (estudiante.isPresent()) {
				System.out.println("Encontrado: " + estudiante.get());
			} else {
				System.out.println("No existe ningun estudiante con ese carnet.");
			}
		} catch (SQLException e) {
			System.err.println("Error al buscar el estudiante: " + e.getMessage());
		}
	}

	private static void actualizarEstudiante() {

		System.out.print("Carnet del estudiante a actualizar: ");
		String carnet = teclado.nextLine();

		System.out.print("Nuevo nombre: ");
		String nuevoNombre = teclado.nextLine();

		int activo;

		do {
			System.out.print("Activo (1=activo, 0=inactivo): ");
			activo = teclado.nextInt();
			if (activo != 0 && activo != 1) {
				System.out.println("Debe ingresar solamente 1 o 0.");
			}
		} while (activo != 0 && activo != 1);

		teclado.nextLine(); // Limpiar el Enter

		System.out.print("Tipo (Pregrado/Posgrado): ");
		String tipoTexto = teclado.nextLine();

		try {
			Estudiante.Tipo tipo = Estudiante.Tipo.valueOf(tipoTexto);
			boolean actualizado = estudianteDAO.actualizarEstudiante(carnet, nuevoNombre, activo, tipo);
			if (actualizado) {
				System.out.println("Estudiante actualizado.");
			} else {
				System.out.println("No existe ningun estudiante con ese carnet.");
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Tipo invalido. Debe ser Pregrado o Posgrado.");
		} catch (SQLException e) {
			System.err.println("Error al actualizar el estudiante: " + e.getMessage());
		}
	}

	private static void eliminarEstudiante() {
		System.out.print("Carnet del estudiante a eliminar: ");
		String carnet = teclado.nextLine();

		try {
			boolean eliminado = estudianteDAO.eliminar(carnet);
			if (eliminado) {
				System.out.println("Estudiante eliminado.");
			} else {
				System.out.println("No existe ningun estudiante con ese carnet.");
			}
		} catch (SQLException e) {
			System.err.println("Error al eliminar el estudiante: " + e.getMessage());
		}
	}

	//metodo solo para mostrar inactivos
	
	private static void listarEstudiantesInactivos() {
		try {

			List<Estudiante> estudiantes = estudianteDAO.listarInactivos();

			if (estudiantes.isEmpty()) {
				System.out.println("No hay estudiantes inactivos.");
				return;
			}

			System.out.println("=== ESTUDIANTES INACTIVOS ===");

			for (Estudiante estudiante : estudiantes) {
				System.out.println(estudiante);
			}

		} catch (SQLException e) {

			System.err.println("Error al listar los estudiantes inactivos: " + e.getMessage());
		}

	}

}
