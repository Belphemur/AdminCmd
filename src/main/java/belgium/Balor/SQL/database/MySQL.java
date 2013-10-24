package belgium.Balor.SQL.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import be.Balor.Tools.Debug.DebugLog;
import belgium.Balor.SQL.Database;
import belgium.Balor.SQL.DatabaseConfig.DatabaseType;
import belgium.Balor.SQL.JdbcConnectionPool;
import belgium.Balor.SQL.Statements;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class MySQL extends Database {
	private final String hostname;
	private final String portnmbr;
	private final String username;
	private final String password;
	private final String database;
	private JdbcConnectionPool pool;

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
		final MysqlConnectionPoolDataSource mysqlPool = new MysqlConnectionPoolDataSource();
		mysqlPool.setAutoClosePStmtStreams(true);
		mysqlPool.setAutoReconnect(true);
		mysqlPool.setAutoReconnectForConnectionPools(true);
		mysqlPool.setAutoReconnectForPools(true);
		mysqlPool.setDatabaseName(this.database);
		mysqlPool.setUser(this.username);
		mysqlPool.setPassword(this.password);
		mysqlPool.setServerName(this.hostname);
		mysqlPool.setPort(Integer.parseInt(this.portnmbr));
		pool = JdbcConnectionPool.create(mysqlPool);
	}

	@Override
	public ResultSet query(final String query) {
		Statement statement = null;
		ResultSet result = null;
		try {
			autoReconnect();
			statement = this.getConnection().createStatement();

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
			statement = getConnection().createStatement();
			statement.executeQuery(query);

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
	public PreparedStatement prepare(final String query, final Connection conn) {
		try {
			final PreparedStatement ps;
			autoReconnect();
			if (Statements.getStatement(query) == Statements.INSERT) {
				ps = conn.prepareStatement(query,
						PreparedStatement.RETURN_GENERATED_KEYS);
			} else {
				ps = conn.prepareStatement(query);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see belgium.Balor.SQL.Database#getConnection()
	 */
	@Override
	protected Connection getConnection() {
		try {
			return pool.getConnection();
		} catch (final SQLException e) {
			throw new RuntimeException("Can't get a connection", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see belgium.Balor.SQL.Database#closePrepStmt(java.sql.PreparedStatement)
	 */
	@Override
	public void closePrepStmt(final PreparedStatement prepStmt) {
		try {
			prepStmt.close();
		} catch (final Exception e) {
			DebugLog.addException("Exeception when closing stmt", e);
		}
		try {
			prepStmt.getConnection().close();
		} catch (final Exception e) {
			DebugLog.addException("Exeception when closing connection of stmt",
					e);
		}

	}
}