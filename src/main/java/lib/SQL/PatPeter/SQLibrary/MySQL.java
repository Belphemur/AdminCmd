/**
 * MySQL
 * Inherited subclass for making a connection to a MySQL server.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package lib.SQL.PatPeter.SQLibrary;

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
	public ResultSet query(final String query) {
		Statement statement = null;
		ResultSet result = null;
		try {
			synchronized (connection) {
				statement = this.connection.createStatement();

				switch (this.getStatement(query)) {
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
			this.writeError("SQL exception in query(): " + e.getMessage(),
					false);
		}
		return result;
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
			query = "DELETE FROM " + table + ";";
			synchronized (connection) {
				statement = connection.createStatement();
				statement.executeQuery(query);
			}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see lib.SQL.PatPeter.SQLibrary.Database#prepare(java.lang.String)
	 */
	@Override
	public PreparedStatement prepare(final String query) {
		try {
			final PreparedStatement ps;
			synchronized (connection) {
				if (getStatement(query) == Statements.INSERT) {
					ps = connection.prepareStatement(query,
							PreparedStatement.RETURN_GENERATED_KEYS);
				} else {
					ps = connection.prepareStatement(query);
				}
			}
			return ps;
		} catch (final SQLException e) {
			if (!e.toString().contains("not return ResultSet")) {
				this.writeError(
						"SQL exception in prepare(): " + e.getMessage(), false);
			}
		}
		return null;
	}
}