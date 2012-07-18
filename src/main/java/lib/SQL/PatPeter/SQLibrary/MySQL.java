/**
 * MySQL
 * Inherited subclass for making a connection to a MySQL server.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package lib.SQL.PatPeter.SQLibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import lib.SQL.PatPeter.SQLibrary.DatabaseConfig.DatabaseType;

public class MySQL extends Database {
	private String hostname = "localhost";
	private String portnmbr = "3306";
	private String username = "minecraft";
	private String password = "";
	private String database = "minecraft";

	public MySQL(final Logger log, final String prefix, final String hostname,
			final String portnmbr, final String database,
			final String username, final String password) {
		super(log, prefix, "[MySQL] ");
		this.hostname = hostname;
		this.portnmbr = portnmbr;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	@Override
	protected void initialize() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // Check that server's Java
													// has MySQL support.
		} catch (final ClassNotFoundException e) {
			throw new SQLException("Can't load JDBC Driver", e);
		}
	}

	@Override
	public void open() throws SQLException {
		initialize();
		String url = "";
		url = "jdbc:mysql://" + this.hostname + ":" + this.portnmbr + "/"
				+ this.database;
		this.connection = DriverManager.getConnection(url, this.username,
				this.password);
	}

	@Override
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (final Exception e) {
			this.writeError("Exception in close(): " + e.getMessage(), true);
		}
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}

	// http://forums.bukkit.org/threads/lib-tut-mysql-sqlite-bukkit-drivers.33849/page-4#post-701550
	@Override
	public boolean checkConnection() {
		if (connection != null) {
			return true;
		}
		return false;
	}

	@Override
	public ResultSet query(final String query) {
		Statement statement = null;
		ResultSet result = null;
		try {
			statement = this.connection.createStatement();
			result = statement.executeQuery("SELECT CURTIME()");

			switch (this.getStatement(query)) {
				case SELECT :
					result = statement.executeQuery(query);
					break;

				case INSERT :
				case UPDATE :
				case DELETE :
				case CREATE :
				case ALTER :
				case DROP :
				case TRUNCATE :
				case RENAME :
				case DO :
				case REPLACE :
				case LOAD :
				case HANDLER :
				case CALL :
					this.lastUpdate = statement.executeUpdate(query);
					break;

				default :
					result = statement.executeQuery(query);
			}
			return result;
		} catch (final SQLException e) {
			this.writeError("SQL exception in query(): " + e.getMessage(),
					false);
		}
		return result;
	}

	@Override
	public PreparedStatement prepare(final String query) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			return ps;
		} catch (final SQLException e) {
			if (!e.toString().contains("not return ResultSet")) {
				this.writeError(
						"SQL exception in prepare(): " + e.getMessage(), false);
			}
		}
		return ps;
	}

	@Override
	public boolean createTable(final String query) {
		Statement statement = null;
		try {
			if (query.equals("") || query == null) {
				this.writeError(
						"Parameter 'query' empty or null in createTable(): "
								+ query, true);
				return false;
			}

			statement = connection.createStatement();
			statement.execute(query);
			return true;
		} catch (final SQLException e) {
			this.writeError(e.getMessage(), true);
			return false;
		} catch (final Exception e) {
			this.writeError(e.getMessage(), true);
			return false;
		}
	}

	@Override
	public boolean checkTable(final String table) {
		try {
			final Statement statement = connection.createStatement();

			final ResultSet result = statement.executeQuery("SELECT * FROM "
					+ table);

			if (result == null) {
				return false;
			}
			if (result != null) {
				return true;
			}
		} catch (final SQLException e) {
			if (e.getMessage().contains("exist")) {
				return false;
			} else {
				this.writeError(
						"SQL exception in checkTable(): " + e.getMessage(),
						false);
			}
		}

		if (query("SELECT * FROM " + table) == null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean wipeTable(final String table) {
		Statement statement = null;
		String query = null;
		try {
			if (!this.checkTable(table)) {
				this.writeError("Table \"" + table
						+ "\" in wipeTable() does not exist.", true);
				return false;
			}
			statement = this.connection.createStatement();
			query = "DELETE FROM " + table + ";";
			statement.executeUpdate(query);

			return true;
		} catch (final SQLException e) {
			if (!e.toString().contains("not return ResultSet")) {
				return false;
			}
		}
		return false;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see lib.SQL.PatPeter.SQLibrary.Database#getType()
	 */
	@Override
	public DatabaseType getType() {
		return DatabaseType.MYSQL;
	}
}