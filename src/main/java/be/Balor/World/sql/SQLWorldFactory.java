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
package be.Balor.World.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lib.SQL.PatPeter.SQLibrary.Database;

import org.bukkit.World;

import be.Balor.Tools.Debug.ACLogger;
import be.Balor.World.ACWorld;
import be.Balor.World.AbstractWorldFactory;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLWorldFactory extends AbstractWorldFactory {
	private static PreparedStatement insertWorld, getWorld;

	/**
 * 
 */
	public SQLWorldFactory() {
		initPrepStmt();

	}

	/**
	 * 
	 */
	public static void initPrepStmt() {
		insertWorld = Database.DATABASE.prepare("INSERT INTO `ac_worlds` (`name`) VALUES (?)");
		getWorld = Database.DATABASE.prepare("SELECT id FROM ac_worlds WHERE name=?");
	}

	@Override
	public synchronized ACWorld createWorld(final World world) {
		final String worldName = world.getName();
		try {
			return getDBWorld(world);
		} catch (final CommunicationsException e) {
			initPrepStmt();
			try {
				getDBWorld(world);
			} catch (final SQLException e1) {
				ACLogger.severe("Can't create an ACWorld for the World " + worldName, e);
			}
		} catch (final SQLException e) {
			ACLogger.severe("Can't create an ACWorld for the World " + worldName, e);
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * @param world
	 * @return
	 * @throws SQLException
	 */
	private ACWorld getDBWorld(final World world) throws SQLException {
		ResultSet rs = null;
		final String worldName = world.getName();
		getWorld.clearParameters();
		getWorld.setString(1, worldName);

		synchronized (getWorld.getConnection()) {
			rs = getWorld.executeQuery();
		}
		if (rs.next()) {
			return new SQLWorld(world, rs.getLong(1));
		} else {
			rs.close();
			insertWorld.clearParameters();
			insertWorld.setString(1, worldName);
			synchronized (insertWorld.getConnection()) {
				insertWorld.executeUpdate();
			}
			rs = insertWorld.getGeneratedKeys();
			if (rs.next()) {
				return new SQLWorld(world, rs.getLong(1));
			} else {
				return null;
			}
		}
	}

}
