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

import org.bukkit.World;
import org.bukkit.WorldCreator;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FileWorldFactory implements IWorldFactory {
	final String directory;

	/**
	 * 
	 */
	public FileWorldFactory(final String directory) {
		this.directory = directory;
	}

	@Override
	public ACWorld createWorld(final String worldName) throws WorldNotLoaded {
		final World w = ACPluginManager.getServer().getWorld(worldName);
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
				return createWorld(ACPluginManager.getServer().createWorld(
						new WorldCreator(worldFile.getName())));
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
	public ACWorld createWorld(final World world) {
		if (directory != null) {
			return new FileWorld(world, directory);
		} else {
			return null;
		}
	}

}
