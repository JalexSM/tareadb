package edu.umg.programacion2.clase05.dao;

import edu.umg.programacion2.clase05.modelo.Estudiante;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object): concentra TODO el codigo SQL/JDBC de la tabla
 * estudiantes en un solo lugar. El resto del programa (Main) nunca vuelve a
 * escribir SQL: solo llama metodos como dao.crear(...) o dao.listarTodos().
 *
 * Cuidado: por simplicidad, cada metodo abre y cierra su propia conexion con
 * try-with-resources. En una aplicacion real con muchas operaciones seguidas se
 * usaria un "pool" de conexiones para no pagar el costo de conectar cada vez;
 * eso lo van a ver mas adelante en el curso. Para este primer ejemplo, abrir y
 * cerrar por operacion es mas facil de seguir con la lectura.
 */
public class EstudianteDAO {

	private static final String URL = "jdbc:mariadb://localhost:3306/prog2_db";
	private static final String USUARIO = "root";
	private static final String PASSWORD = "J4viermadrid";

	// 1. CREATE: inserta un estudiante nuevo y retorna el id que le asigno MySQL.
	public int crear(Estudiante estudiante) throws SQLException {
		//agregamos valores nuevos
		String sql = "INSERT INTO estudiantes (nombre, carnet, activo, tipo) VALUES (?, ?, ?, ?)";

		try (Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
				PreparedStatement statement = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, estudiante.getNombre());
			statement.setString(2, estudiante.getCarnet());
			statement.setInt(3, estudiante.getActivo());
			statement.setString(4, estudiante.getTipo().name());
			statement.executeUpdate();

			// IMPORTANTE: RETURN_GENERATED_KEYS + getGeneratedKeys() es como se
			// recupera el id autoincremental que genero MySQL, sin hacer un
			// SELECT aparte para buscarlo.
			try (ResultSet claves = statement.getGeneratedKeys()) {
				if (claves.next()) {
					return claves.getInt(1);
				}
				return -1;
			}
		}
	}

	// 2. READ (todos): retorna la lista completa de estudiantes.
	public List<Estudiante> listarTodos() throws SQLException {
		//se modifican los valores y como se llaman y solo llamar a activos
		String sql = "SELECT id, nombre, carnet, activo, tipo FROM estudiantes WHERE activo = 1 ORDER BY id";
		List<Estudiante> estudiantes = new ArrayList<>();

		try (Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
				PreparedStatement statement = conexion.prepareStatement(sql);
				ResultSet resultado = statement.executeQuery()) {

			while (resultado.next()) {
				estudiantes.add(mapearFila(resultado));
			}
		}
		return estudiantes;
	}

	// 3. READ (uno): busca un estudiante por carnet. Optional evita retornar un
	// null "silencioso" cuando no se encuentra nada; obliga a quien llama este
	// metodo a manejar explicitamente el caso "no existe".
	public Optional<Estudiante> buscarPorCarnet(String carnet) throws SQLException {
		//solo llama a estudiantes activos 
		String sql = "SELECT id, nombre, carnet, activo, tipo FROM estudiantes WHERE carnet = ? AND activo = 1";

		try (Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
				PreparedStatement statement = conexion.prepareStatement(sql)) {

			statement.setString(1, carnet);

			try (ResultSet resultado = statement.executeQuery()) {
				if (resultado.next()) {
					return Optional.of(mapearFila(resultado));
				}
				return Optional.empty();
			}
		}
	}

	// 4. UPDATE: cambia el nombre de un estudiante existente, identificado por
	// su carnet. Retorna true si se actualizo una fila, false si no existia.
	public boolean actualizarEstudiante(String carnet, String nuevoNombre, int activo, Estudiante.Tipo tipo)
			throws SQLException {
		//se actualizan los nuevos valores 
		String sql = "UPDATE estudiantes " + "SET nombre = ?, activo = ?, tipo = ? " + "WHERE carnet = ?";

		try (Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
				PreparedStatement statement = conexion.prepareStatement(sql)) {

			statement.setString(1, nuevoNombre);
			statement.setInt(2, activo);
			statement.setString(3, tipo.name());
			statement.setString(4, carnet);

			int filasAfectadas = statement.executeUpdate();

			return filasAfectadas > 0;
		}
	}

	// 5. DELETE: elimina un estudiante por carnet. Retorna true si elimino algo.
	public boolean eliminar(String carnet) throws SQLException {
		String sql = "DELETE FROM estudiantes WHERE carnet = ?";

		try (Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
				PreparedStatement statement = conexion.prepareStatement(sql)) {

			statement.setString(1, carnet);

			int filasAfectadas = statement.executeUpdate();
			return filasAfectadas > 0;
		}
	}

	// 6. Consulta para inactivos
	public List<Estudiante> listarInactivos() throws SQLException {

		String sql = "SELECT id, nombre, carnet, activo, tipo " + "FROM estudiantes " + "WHERE activo = 0 "
				+ "ORDER BY id";

		List<Estudiante> estudiantes = new ArrayList<>();

		try (Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
				PreparedStatement statement = conexion.prepareStatement(sql);
				ResultSet resultado = statement.executeQuery()) {

			while (resultado.next()) {
				estudiantes.add(mapearFila(resultado));
			}
		}

		return estudiantes;
	}

	// Metodo privado de apoyo: convierte la fila actual del ResultSet en un
	// objeto Estudiante. Evita repetir este mismo codigo en listarTodos() y en
	// buscarPorCarnet().
	private Estudiante mapearFila(ResultSet resultado) throws SQLException {
		int id = resultado.getInt("id");
		String nombre = resultado.getString("nombre");
		String carnet = resultado.getString("carnet");
		int activo = resultado.getInt("activo");

		String tipoTexto = resultado.getString("tipo");
		Estudiante.Tipo tipo = Estudiante.Tipo.valueOf(tipoTexto);

		return new Estudiante(id, nombre, carnet, activo, tipo);
	}
}
