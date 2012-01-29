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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Help.String.Str;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class WorldManager {
	private ConcurrentMap<String, ACWorld> worlds = new MapMaker().makeMap();
	private ACWorldFactory worldFactory;
	private static WorldManager instance = new WorldManager();

	/**
	 * 
	 */
	private WorldManager() {
	}

	/**
	 * @return the instance
	 */
	public static WorldManager getInstance() {
		return instance;
	}

	/**
	 * Add a new world
	 * 
	 * @param world
	 */
	private synchronized boolean addWorld(ACWorld world) {
		final String name = world.getName();
		if (name == null) {
			throw new NullPointerException();
		}

		ACWorld ref = worlds.get(name);
		if (ref != null)
			return false;
		worlds.put(name, world);
		return true;
	}

	/**
	 * @param worldFactory
	 *            the worldFactory to set
	 */
	public void setWorldFactory(ACWorldFactory worldFactory) {
		this.worldFactory = worldFactory;
	}

	ACWorld demandACWorld(String name) throws WorldNotLoaded {
		ACWorld result = worlds.get(name);
		if (result == null) {
			String found = Str.matchString(worlds.keySet(), name);
			if (found != null)
				return worlds.get(found);
			result = worldFactory.createWorld(name);
			addWorld(result);
			result = worlds.get(name);
		}
		return result;
	}

	/**
	 * Getting all the WarpsName using the world name as prefix
	 * 
	 * @return
	 */
	public Set<String> getAllWarpList() {
		Set<String> warps = new HashSet<String>();
		for (ACWorld world : worlds.values())
			for (String warp : world.getWarpList())
				warps.add(world.getName() + ":" + warp);
		return warps;

	}

}
