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

import org.bukkit.Bukkit;
import org.bukkit.World;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Debug.DebugLog;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class AbstractWorldFactory {

	public ACWorld createWorld(final String worldName) throws WorldNotLoaded {
		DebugLog.beginInfo("Loading World " + worldName);
		try {
			World w = Bukkit.getServer().getWorld(worldName);

			if (w == null) {
				// World isn't loaded by Bukkit or other multi-world plugins so we don't allow unloaded world files to be loaded (possible security issue)
				throw new WorldNotLoaded(worldName);
			}
			return createWorld(w);
		} finally {
			DebugLog.endInfo();
		}
	}

	protected abstract ACWorld createWorld(World world);
}
