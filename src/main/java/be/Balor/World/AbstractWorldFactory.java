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
package be.Balor.World;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class AbstractWorldFactory {
	private final Map<String, World> bukkitWorlds = Collections
			.synchronizedMap(new HashMap<String, World>());

	/**
	 * 
	 */
	protected AbstractWorldFactory() {
		for (final World w : ACPluginManager.getServer().getWorlds()) {
			bukkitWorlds.put(w.getName().toLowerCase(), w);
		}
	}

	public ACWorld createWorld(final String worldName) throws WorldNotLoaded {
		World w = bukkitWorlds.get(worldName.toLowerCase());
		if (w == null) {
			w = Bukkit.getServer().getWorld(worldName);
		}
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
		} else {
			bukkitWorlds.put(worldName.toLowerCase(), w);
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

	protected abstract ACWorld createWorld(World world);
}
