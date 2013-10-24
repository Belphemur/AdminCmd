/**
 * Database Handler
 * Abstract superclass for all subclass database files.
 * 
 * Date Created: 2011-08-26 19:08
 * @author PatPeter
 */
package belgium.Balor.SQL;

/*
 *  MySQL
 */
// import java.net.MalformedURLException;

/*
 *  SQLLite
 */
//import java.io.File;
//import java.sql.DatabaseMetaData;

/*
 *  Both
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;

import be.Balor.Player.sql.SQLPlayer;
import be.Balor.Player.sql.SQLPlayerFactory;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.World.sql.SQLWorld;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import belgium.Balor.SQL.DatabaseConfig.DatabaseType;
import belgium.Balor.SQL.DatabaseConfig.Parameter;

public abstract class Database {
	public static Database DATABASE;
	protected Logger log;
	protected final String PREFIX;
	protected final String DATABASE_PREFIX;
	protected Connection connection;
	static {
		initDb();
	}

	public static void initDb() {
		if (DATABASE != null) {
			DATABASE.close();
		}
		final DatabaseConfig config = new DatabaseConfig();
		Database db;
		final String dbWrapper = ConfigEnum.DATA_WRAPPER.getString();
		if (dbWrapper.equalsIgnoreCase("mysql")) {
			config.setType(DatabaseType.MYSQL);
			try {
				config.setParameter(Parameter.HOSTNAME,
						ConfigEnum.MYSQL_HOST.getString());
				config.setParameter(Parameter.PASSWORD,
						ConfigEnum.MYSQL_PASS.getString());
				config.setParameter(Parameter.USER,
						ConfigEnum.MYSQL_USER.getString());
				config.setParameter(Parameter.PORT_NUMBER, "3306");
				config.setParameter(Parameter.DATABASE,
						ConfigEnum.MYSQL_DB.getString());
			} catch (final NullPointerException e) {
			} catch (final InvalidConfigurationException e) {
			}
		} else if (dbWrapper.equalsIgnoreCase("sqlite")) {
			config.setType(DatabaseType.SQLITE);
			try {
				config.setParameter(Parameter.DB_LOCATION, ACHelper
						.getInstance().getCoreInstance().getDataFolder()
						.getAbsolutePath());
				config.setParameter(Parameter.DB_NAME, "admincmd");

			} catch (final NullPointerException e) {
			} catch (final InvalidConfigurationException e) {
			}
		}
		config.setLog(ACHelper.getInstance().getCoreInstance().getLogger());
		try {
			config.setParameter(Parameter.DB_PREFIX, "[AdminCmd Database]");
		} catch (final NullPointerException e1) {

		} catch (final InvalidConfigurationException e1) {

		}

		try {
			if (config.getType() == null) {
				db = null;
			} else {
				db = DatabaseFactory.createDatabase(config);
			}
		} catch (final InvalidConfigurationException e) {
			ACLogger.severe("Problem while trying to load the Database", e);
			db = null;
		}
		DATABASE = db;
		DebugLog.INSTANCE.info("Database initialization done");
	}

	public int lastUpdate;

	/*
	 * MySQL, SQLite
	 */
	public Database(final Logger log, final String prefix, final String dp) {
		this.log = log;
		this.PREFIX = prefix;
		this.DATABASE_PREFIX = dp;
		this.connection = null;
	}

	/**
	 * <b>writeInfo</b><br>
	 * <br>
	 * &nbsp;&nbsp;Writes information to the console. <br>
	 * <br>
	 * 
	 * @param toWrite
	 *            - the <a href=
	 *            "http://download.oracle.com/javase/6/docs/api/java/lang/String.html"
	 *            >String</a> of content to write to the console.
	 */
	protected void writeInfo(final String toWrite) {
		if (toWrite != null) {
			this.log.info(this.PREFIX + this.DATABASE_PREFIX + toWrite);
		}
	}

	/**
	 * <b>writeError</b><br>
	 * <br>
	 * &nbsp;&nbsp;Writes either errors or warnings to the console. <br>
	 * <br>
	 * 
	 * @param toWrite
	 *            - the <a href=
	 *            "http://download.oracle.com/javase/6/docs/api/java/lang/String.html"
	 *            >String</a> written to the console.
	 * @param severe
	 *            - whether console output should appear as an error or warning.
	 */
	protected void writeError(final String toWrite, final boolean severe) {
		if (toWrite != null) {
			if (severe) {
				this.log.severe(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			} else {
				this.log.warning(this.PREFIX + this.DATABASE_PREFIX + toWrite);
			}
		}
	}

	/**
	 * <b>initialize</b><br>
	 * <br>
	 * &nbsp;&nbsp;Used to check whether the class for the SQL engine is
	 * installed. <br>
	 * <br>
	 * 
	 * @throws SQLException
	 *             if there is a problem when trying to load the JDBC driver.
	 */
	protected abstract void initialize() throws SQLException;

	/**
	 * <b>open</b><br>
	 * <br>
	 * &nbsp;&nbsp;Opens a connection with the database. <br>
	 * <br>
	 * 
	 * @return the success of the method.
	 * @throws SQLException
	 *             if can't open the connection.
	 */
	public abstract void open() throws SQLException;

	/**
	 * <b>close</b><br>
	 * <br>
	 * &nbsp;&nbsp;Closes a connection with the database. <br>
	 * <br>
	 */
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (final SQLException ex) {
				this.writeError("SQL exception in close(): " + ex, true);
			}
		}
	}

	/**
	 * <b>getConnection</b><br>
	 * <br>
	 * &nbsp;&nbsp;Gets the connection variable <br>
	 * <br>
	 * 
	 * @return the <a href=
	 *         "http://download.oracle.com/javase/6/docs/api/java/sql/Connection.html"
	 *         >Connection</a> variable.
	 */
	public Connection getConnection() {
		return this.connection;
	}

	/**
	 * <b>query</b><br>
	 * &nbsp;&nbsp;Sends a query to the SQL database. <br>
	 * <br>
	 * 
	 * @param query
	 *            - the SQL query to send to the database.
	 * @return the table of results from the query.
	 */
	public abstract ResultSet query(String query);

	/**
	 * <b>prepare</b><br>
	 * &nbsp;&nbsp;Prepares to send a query to the database. <br>
	 * <br>
	 * 
	 * @param query
	 *            - the SQL query to prepare to send to the database.
	 * @return the prepared statement.
	 */
	public PreparedStatement prepare(final String query) {
		try {
			final PreparedStatement ps;
			ps = connection.prepareStatement(query);
			return ps;
		} catch (final SQLException e) {
			if (!e.toString().contains("not return ResultSet")) {
				this.writeError(
						"SQL exception in prepare(): " + e.getMessage(), false);
			}
		}
		return null;
	}

	/**
	 * <b>createTable</b><br>
	 * <br>
	 * &nbsp;&nbsp;Creates a table in the database based on a specified query. <br>
	 * <br>
	 * 
	 * @param query
	 *            - the SQL query for creating a table.
	 * @return the success of the method.
	 */
	public boolean createTable(final String query) {
		Statement statement = null;
		try {
			if (query.equals("") || query == null) {
				this.writeError(
						"Parameter 'query' empty or null in createTable().",
						true);
				return false;
			}
			statement = connection.createStatement();
			statement.execute(query);
			return true;
		} catch (final SQLException ex) {
			this.writeError(ex.getMessage(), true);
			return false;
		}
	}

	/**
	 * <b>checkTable</b><br>
	 * <br>
	 * &nbsp;&nbsp;Checks a table in a database based on the table's name. <br>
	 * <br>
	 * 
	 * @param table
	 *            - name of the table to check.
	 * @return success of the method.
	 */
	public boolean checkTable(final String table) {
		DatabaseMetaData dbm = null;
		try {
			dbm = this.connection.getMetaData();

			final ResultSet tables = dbm.getTables(null, null, table, null);
			if (tables.next()) {
				return true;
			} else {
				return false;
			}
		} catch (final SQLException e) {
			this.writeError("Failed to check if table \"" + table
					+ "\" exists: " + e.getMessage(), true);
			return false;
		}
	}

	/**
	 * <b>wipeTable</b><br>
	 * <br>
	 * &nbsp;&nbsp;Wipes a table given its name. <br>
	 * <br>
	 * 
	 * @param table
	 *            - name of the table to wipe.
	 * @return success of the method.
	 */
	public abstract boolean wipeTable(String table);

	/**
	 * Get type of the database
	 * 
	 * @return type of the database
	 */
	public abstract DatabaseType getType();

	/**
	 * Check if the connection is valid
	 * 
	 * @return true if it is
	 */
	private boolean isConnectionValid() {

		if (this.connection != null) {
			try {
					return !connection.isClosed() && connection.isValid(3);
			} catch (final SQLException e) {
				DebugLog.INSTANCE.log(Level.INFO,
						"Problem when checking connection state", e);
			}
		}

		return false;
	}

	/**
	 * Try to reconnect.
	 */
	private void reconnect() {
		try {
			this.connection.close();
		} catch (final SQLException e) {
		}
		this.connection = null;
		try {
			open();
			SQLPlayer.initPrepStmt();
			SQLPlayerFactory.initPrepStmt();
			SQLWorld.initPrepStmt();
		} catch (final SQLException e) {
			writeError(
					"Problem while reconnection to the database :\n"
							+ e.getMessage(), true);
		}
	}

	/**
	 * Check if the connection is valid, if not try to reconnect.
	 */
	public void autoReconnect() {
		if (!isConnectionValid()) {
			reconnect();
		}
	}
}