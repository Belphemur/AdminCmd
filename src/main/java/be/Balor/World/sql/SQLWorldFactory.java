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

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SQLWorldFactory extends AbstractWorldFactory {
	private final PreparedStatement insertWorld, getWorld;

	/**
 * 
 */
	public SQLWorldFactory() {
		insertWorld = Database.DATABASE
				.prepare("INSERT INTO `ac_worlds` (`name`) VALUES (?)");
		getWorld = Database.DATABASE
				.prepare("SELECT id FROM ac_worlds WHERE name=?");

	}

	@Override
	public synchronized ACWorld createWorld(final World world) {
		ResultSet rs = null;
		final String worldName = world.getName();
		try {
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
