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
package be.Balor.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import be.Balor.Tools.Type;
import be.Balor.Tools.Type.Category;
import be.Balor.Tools.Configuration.ExtendedConfiguration;
import be.Balor.Tools.Configuration.ExtendedNode;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.Tools.Files.WorldNotLoaded;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FilePlayer extends ACPlayer {

	private final ExtendedConfiguration datas;
	private final ExtendedNode informations;
	private final ExtendedNode homes;
	private final ExtendedNode powers;
	private int saveCount = 0;
	private int SAVE_BEFORE_WRITE = 5;

	/**
 * 
 */
	public FilePlayer(String directory, String name) {
		super(name);
		File pFile = new File(directory, name + ".yml");
		if (!pFile.getParentFile().exists())
			pFile.getParentFile().mkdirs();
		if (!pFile.exists())
			try {
				pFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		datas = new ExtendedConfiguration(pFile);
		datas.load();
		datas.save();
		informations = datas.createNode("infos");
		homes = datas.createNode("home");
		powers = datas.createNode("powers");
	}

	public void setHome(String home, Location loc) {
		ExtendedNode homeToSet = homes.createNode(home);
		homeToSet.setProperty("world", loc.getWorld().getName());
		homeToSet.setProperty("x", loc.getX());
		homeToSet.setProperty("y", loc.getY());
		homeToSet.setProperty("z", loc.getZ());
		homeToSet.setProperty("yaw", loc.getYaw());
		homeToSet.setProperty("pitch", loc.getPitch());
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#removeHome(java.lang.String)
	 */
	public void removeHome(String home) {
		homes.removeProperty(home);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getHome(java.lang.String)
	 */
	public Location getHome(String home) {
		return getLocation("home." + home);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#setInformation(java.lang.String,
	 * java.lang.Object)
	 */
	public void setInformation(String info, Object value) {
		informations.setProperty(info, value);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#removeInformation(java.lang.String
	 * )
	 */
	public void removeInformation(String info) {
		informations.removeProperty(info);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#getInformation(java.lang.String)
	 */
	public ObjectContainer getInformation(String info) {
		return new ObjectContainer(informations.getProperty(info));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#setLastLocation(org.bukkit.Location
	 * )
	 */
	public void setLastLocation(Location loc) {
		if (loc == null)
			datas.removeProperty("lastLoc");
		else {
			ExtendedNode lastLoc = datas.createNode("lastLoc");
			lastLoc.setProperty("world", loc.getWorld().getName());
			lastLoc.setProperty("x", loc.getX());
			lastLoc.setProperty("y", loc.getY());
			lastLoc.setProperty("z", loc.getZ());
			lastLoc.setProperty("yaw", loc.getYaw());
			lastLoc.setProperty("pitch", loc.getPitch());
		}

		writeFile();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getLastLocation()
	 */

	public Location getLastLocation() {
		return getLocation("lastLoc");
	}

	private Location getLocation(String location) throws WorldNotLoaded {
		ExtendedNode node = datas.getNode(location);
		if (node == null)
			return null;
		if (node.getProperty("world") == null) {
			Location loc = parseLocation(location);
			if (loc != null)
				setHome(location, loc);
			return loc;
		} else {
			World w = ACPluginManager.getServer().getWorld(node.getString("world"));
			if (w != null)
				return new Location(w, node.getDouble("x", 0), node.getDouble("y", 0),
						node.getDouble("z", 0), Float.parseFloat(node.getString("yaw")),
						Float.parseFloat(node.getString("pitch")));
			else
				throw new WorldNotLoaded(node.getString("world"));

		}
	}

	/**
	 * Parse String to create a location
	 * 
	 * @param property
	 * @param conf
	 * @return
	 */
	private Location parseLocation(String home) {
		String toParse = datas.getString("home." + home);
		if (toParse == null)
			return null;
		if (toParse.isEmpty())
			return null;
		String infos[] = new String[5];
		Double coords[] = new Double[3];
		Float direction[] = new Float[2];
		infos = toParse.split(";");
		for (int i = 0; i < coords.length; i++)
			try {
				coords[i] = Double.parseDouble(infos[i]);
			} catch (NumberFormatException e) {
				return null;
			}
		for (int i = 3; i < infos.length - 1; i++)
			try {
				direction[i - 3] = Float.parseFloat(infos[i]);
			} catch (NumberFormatException e) {
				return null;
			}
		return new Location(ACPluginManager.getServer().getWorld(infos[5]), coords[0], coords[1],
				coords[2], direction[0], direction[1]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#setPower(be.Balor.Tools.Type,
	 * java.lang.Object)
	 */

	public void setPower(Type power, Object value) {
		powers.setProperty(power.toString(), value);
		writeFile();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getPower(be.Balor.Tools.Type)
	 */

	public ObjectContainer getPower(Type power) {
		return new ObjectContainer(powers.getProperty(power.toString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#hasPower(be.Balor.Tools.Type)
	 */
	@Override
	public boolean hasPower(Type power) {
		return !getPower(power).isNull();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#removePower(be.Balor.Tools.Type)
	 */

	public void removePower(Type power) {
		powers.removeProperty(power.toString());
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getHomeList()
	 */

	public List<String> getHomeList() {
		return homes.getKeys();
	}

	private void writeFile() {
		if (saveCount == SAVE_BEFORE_WRITE || !isOnline) {
			datas.save();
			saveCount = 0;
		} else
			saveCount++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#forceSave()
	 */
	@Override
	void forceSave() {
		datas.save();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeAllSuperPower()
	 */
	@Override
	public void removeAllSuperPower() {
		for (String power : powers.getKeys()) {
			Type matched = Type.matchType(power);
			if (matched != null && matched.getCategory().equals(Category.SUPER_POWER))
				powers.removeProperty(power);
		}
		writeFile();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setCustomPower(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setCustomPower(String power, Object value) {
		Type.addCustomPower(power);
		powers.setProperty(power, value);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getCustomPower(java.lang.String)
	 */
	@Override
	public ObjectContainer getCustomPower(String power) {
		return new ObjectContainer(powers.getProperty(power));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#hasCustomPower(java.lang.String)
	 */
	@Override
	public boolean hasCustomPower(String power) {
		return getCustomPower(power) != null;
	}

	/* (non-Javadoc)
	 * @see be.Balor.Player.ACPlayer#removeCustomPower(java.lang.String)
	 */
	@Override
	public void removeCustomPower(String power) {
		powers.removeProperty(power);
		writeFile();
		
	}

}
