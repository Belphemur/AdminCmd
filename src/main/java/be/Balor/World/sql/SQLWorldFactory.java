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

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import lib.SQL.PatPeter.SQLibrary.Database;

import org.bukkit.World;
import org.bukkit.WorldCreator;

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
	private final PreparedStatement insertWorld, getWorld;
	private final Map<String, World> bukkitWorlds = new HashMap<String, World>();

	/**
 * 
 */
	public SQLWorldFactory() {
		insertWorld = Database.DATABASE
				.prepare("INSERT INTO `ac_worlds` (`name`) VALUES (?)");
		getWorld = Database.DATABASE
				.prepare("SELECT id FROM ac_worlds WHERE name=?");
		for (final World w : ACPluginManager.getServer().getWorlds()) {
			bukkitWorlds.put(w.getName().toLowerCase(), w);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.IWorldFactory#createWorld(java.lang.String)
	 */
	@Override
	public synchronized ACWorld createWorld(final String worldName)
			throws WorldNotLoaded {
		World w = bukkitWorlds.get(worldName.toLowerCase());
		if (w == null) {
			File worldFile = new File(ACPluginManager.getServer()
					.getWorldContainer(), worldName);
			if (!isExistingWorld(worldFile)) {
				worldFile = new File(ACPluginManager.getServer()
						.getWorldContainer(), worldName.toLowerCase());
			}
			if (!isExistingWorld(worldFile)) {
				worldFile = new File(ACPluginManager.getServer()
						.getWorldContainer(), worldName.substring(0, 1)
						.toUpperCase() + worldName.substring(1).toLowerCase());
			}
			if (isExistingWorld(worldFile)) {
				w = ACPluginManager.getServer().createWorld(
						new WorldCreator(worldFile.getName()));
				bukkitWorlds.put(w.getName().toLowerCase(), w);
				return createWorld(w);
			}
			throw new WorldNotLoaded(worldName);
		}
		return createWorld(w);
	}

	/**
	 * @param worldFile
	 * @return
	 */
	private boolean isExistingWorld(final File worldFile) {
		return worldFile.exists() && worldFile.isDirectory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.IWorldFactory#createWorld(org.bukkit.World)
	 */
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
