/************************************************************************
 * This file is part of AdminCmd.									
 *																		
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * AdminCmd is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package belgium.Balor.SQL;

/**
 * @author Antoine
 * 
 */
// http://dev.mysql.com/doc/refman/5.6/en/sql-syntax.html
// http://sqlite.org/lang.html
public enum Statements {
	SELECT, INSERT, UPDATE, DELETE, DO, REPLACE, LOAD, HANDLER, CALL, // Data
																		// manipulation
																		// statements
	CREATE,
	ALTER,
	DROP,
	TRUNCATE,
	RENAME, // Data definition statements

	// MySQL-specific
	START,
	COMMIT,
	ROLLBACK,
	SAVEPOINT,
	LOCK,
	UNLOCK, // MySQL Transactional and Locking Statements
	PREPARE,
	EXECUTE,
	DEALLOCATE, // Prepared Statements
	SET,
	SHOW, // Database Administration
	DESCRIBE,
	EXPLAIN,
	HELP,
	USE, // Utility Statements

	// SQLite-specific
	ANALYZE,
	ATTACH,
	BEGIN,
	DETACH,
	END,
	INDEXED,
	ON,
	PRAGMA,
	REINDEX,
	RELEASE,
	VACUUM;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	/**
	 * Get which statement is used.
	 * 
	 * @param query
	 * @return used statement.
	 */
	public static Statements getStatement(final String query) {
		final String lowerQuery = query.trim().toLowerCase();
		for (final Statements stmt : Statements.values()) {
			if (lowerQuery.startsWith(stmt.toString())) {
				return stmt;
			}
		}
		return Statements.SELECT;
	}

}
