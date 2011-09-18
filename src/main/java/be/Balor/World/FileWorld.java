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
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Configuration.ExtendedConfiguration;
import be.Balor.Tools.Configuration.ExtendedNode;
import be.Balor.Tools.Files.ObjectContainer;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FileWorld extends ACWorld {
	private final ExtendedConfiguration datas;
	private final ExtendedNode warps;
	private final ExtendedNode informations;
	private int saveCount = 0;
	private int SAVE_BEFORE_WRITE = 5;

	/**
	 * @param name
	 */
	public FileWorld(World world, String directory) {
		super(world);
		File wFile = new File(directory, world.getName() + ".yml");
		if (!wFile.getParentFile().exists())
			wFile.getParentFile().mkdirs();
		if (!wFile.exists())
			try {
				wFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		datas = new ExtendedConfiguration(wFile);
		datas.registerClass(SimpleLocation.class);
		datas.load();
		datas.save();
		warps = datas.createNode("warps");
		informations = datas.createNode("informations");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setSpawn(org.bukkit.Location)
	 */
	@Override
	public void setSpawn(Location loc) {
		datas.setProperty("spawn", new SimpleLocation(loc));
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getSpawn()
	 */
	@Override
	public Location getSpawn() throws WorldNotLoaded {
		Object spawn = datas.getProperty("spawn");
		if (spawn == null)
			return handler.getSpawnLocation();
		else if (spawn instanceof SimpleLocation)
			return ((SimpleLocation) spawn).getLocation();
		else
			return handler.getSpawnLocation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#addWarp(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void addWarp(String name, Location loc) {
		warps.setProperty(name, new SimpleLocation(loc));
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarp(java.lang.String)
	 */
	@Override
	public Location getWarp(String name) throws WorldNotLoaded {
		Object warp = warps.getProperty(name);
		if (warp == null)
			return null;
		return ((SimpleLocation) warp).getLocation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarpList()
	 */
	@Override
	public List<String> getWarpList() {
		return warps.getKeys();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeWarp(java.lang.String)
	 */
	@Override
	public void removeWarp(String name) {
		warps.removeProperty(name);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setInformation(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setInformation(String info, Object value) {
		informations.setProperty(info, value);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeInformation(java.lang.String)
	 */
	@Override
	public void removeInformation(String info) {
		informations.removeProperty(info);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformation(java.lang.String)
	 */
	@Override
	public ObjectContainer getInformation(String info) {
		return new ObjectContainer(informations.getProperty(info));
	}

	/**
	 * 
	 */
	private void writeFile() {
		if (saveCount == SAVE_BEFORE_WRITE) {
			datas.save();
			saveCount = 0;
		} else
			saveCount++;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#forceSave()
	 */
	@Override
	void forceSave() {
		datas.save();
	}

}
