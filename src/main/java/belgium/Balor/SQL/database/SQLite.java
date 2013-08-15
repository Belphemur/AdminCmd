package belgium.Balor.SQL.database;

/*
 * SQLite
 */
import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import belgium.Balor.SQL.Database;
import belgium.Balor.SQL.DatabaseConfig.DatabaseType;
import belgium.Balor.SQL.Statements;

public class SQLite extends Database {
	public String location;
	public String name;
	private final File sqlFile;

	public SQLite(final Logger log, final String prefix, final String name, final String location) {
		super(log, prefix, "[SQLite] ");
		this.name = name;
		this.location = location;
		final File folder = new File(this.location);
		if (this.name.contains("/") || this.name.contains("\\") || this.name.endsWith(".db")) {
			this.writeError("The database name cannot contain: /, \\, or .db", true);
		}
		if (!folder.exists()) {
			folder.mkdir();
		}

		sqlFile = new File(folder, name + ".db");
	}

	@Override
	protected void initialize() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			if (!sqlFile.exists()) {
				try {
					sqlFile.createNewFile();
				} catch (final IOException e) {
				}
			}
		} catch (final ClassNotFoundException e) {
			throw new SQLException("Can't load JDBC Driver", e);
		}
	}

	@Override
	public void open() throws SQLException {
		initialize();
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.getAbsolutePath());

	}

	@Override
	public ResultSet query(final String query) {
		Statement statement = null;
		ResultSet result = null;

		try {
			synchronized (connection) {
				statement = connection.createStatement();

				switch (Statements.getStatement(query)) {
				case SELECT:
					result = statement.executeQuery(query);
					break;

				case INSERT:
				case UPDATE:
				case DELETE:
				case CREATE:
				case ALTER:
				case DROP:
				case TRUNCATE:
				case RENAME:
				case DO:
				case REPLACE:
				case LOAD:
				case HANDLER:
				case CALL:
					this.lastUpdate = statement.executeUpdate(query);
					break;

				default:
					result = statement.executeQuery(query);

				}
			}
			return result;
		} catch (final SQLException e) {
			if (e.getMessage().toLowerCase().contains("locking") || e.getMessage().toLowerCase().contains("locked")) {
				return retry(query);
			} else {
				this.writeError("SQL exception in query(): " + e.getMessage(), false);
			}

		}
		return null;
	}

	@Override
	public boolean wipeTable(final String table) {
		Statement statement = null;
		String query = null;
		try {
			if (!this.checkTable(table)) {
				this.writeError("Table \"" + table + "\" in wipeTable() does not exist.", true);
				return false;
			}
			query = "DELETE FROM " + table + ";";
			synchronized (connection) {
				statement = connection.createStatement();
				statement.executeQuery(query);
			}
			return true;
		} catch (final SQLException ex) {
			if (!(ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked"))
					&& !ex.toString().contains("not return ResultSet")) {
				this.writeError("Error at SQL Wipe Table Query: " + ex, false);
			}
			return false;
		}
	}

	/**
	 * <b>retry</b><br>
	 * <br>
	 * Retries a statement and returns a ResultSet. <br>
	 * <br>
	 * 
	 * @param query
	 *            The SQL query to retry.
	 * 
	 * @return The SQL query result.
	 */
	private ResultSet retry(final String query) {
		Statement statement = null;
		ResultSet result = null;

		try {
			synchronized (connection) {
				statement = connection.createStatement();
				result = statement.executeQuery(query);
			}
			return result;
		} catch (final SQLException ex) {
			if (ex.getMessage().toLowerCase().contains("locking") || ex.getMessage().toLowerCase().contains("locked")) {
				this.writeError("Please close your previous ResultSet to run the query: \n\t" + query, false);
			} else {
				this.writeError("SQL exception in retry(): " + ex.getMessage(), false);
			}
		}

		return null;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see lib.SQL.PatPeter.SQLibrary.Database#getType()
	 */
	@Override
	public DatabaseType getType() {
		return DatabaseType.SQLITE;
	}
}