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

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.World.ACWorld;
import be.Balor.World.IWorldFactory;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLWorldFactory implements IWorldFactory {
	private final static PreparedStatement INSERT_WORLD, GET_WORLD;
	static {
		INSERT_WORLD = Database.DATABASE
				.prepare("INSERT INTO 'ac_worlds' ('name') VALUES (?)");
		GET_WORLD = Database.DATABASE
				.prepare("SELECT id FROM ac_worlds WHERE name=?");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.IWorldFactory#createWorld(java.lang.String)
	 */
	@Override
	public synchronized ACWorld createWorld(final String worldName)
			throws WorldNotLoaded {
		final World w = ACPluginManager.getServer().getWorld(worldName);
		if (w == null) {
			throw new WorldNotLoaded(worldName);
		}
		ResultSet rs = null;
		try {
			GET_WORLD.clearParameters();
			GET_WORLD.setString(1, worldName);

			synchronized (GET_WORLD.getConnection()) {
				rs = GET_WORLD.executeQuery();
			}
			if (rs.next()) {
				return new SQLWorld(w, rs.getLong(1));
			} else {
				rs.close();
				INSERT_WORLD.clearParameters();
				INSERT_WORLD.setString(1, worldName);
				synchronized (INSERT_WORLD.getConnection()) {
					INSERT_WORLD.executeUpdate();
				}
				rs = INSERT_WORLD.getGeneratedKeys();
				if (rs.next()) {
					return new SQLWorld(w, rs.getLong(1));
				} else {
					return null;
				}
			}
		} catch (final SQLException e) {
			ACLogger.severe("Can't create an ACWorld for the World "
					+ worldName, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (final SQLException e) {
				}
			}
		}

		return null;

	}

}
