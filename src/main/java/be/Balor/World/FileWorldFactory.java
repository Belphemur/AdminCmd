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

import org.bukkit.World;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FileWorldFactory extends AbstractWorldFactory {
	final String directory;

	/**
	 * 
	 */
	public FileWorldFactory(final String directory) {
		this.directory = directory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.IWorldFactory#createWorld(org.bukkit.World)
	 */
	@Override
	public synchronized ACWorld createWorld(final World world) {
		if (directory != null) {
			return new FileWorld(world, directory);
		} else {
			return null;
		}
	}

}
