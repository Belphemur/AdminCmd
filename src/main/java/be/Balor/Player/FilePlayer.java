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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.common.io.Files;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Type;
import be.Balor.Tools.Type.Category;
import be.Balor.Tools.Configuration.ExConfigurationSection;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.Tools.Help.String.Str;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FilePlayer extends ACPlayer {

	private ExtendedConfiguration datas;
	private ExConfigurationSection informations;
	private ExConfigurationSection homes;
	private ExConfigurationSection powers;
	private ExConfigurationSection kitsUse;
	private int saveCount = 0;
	private final static int SAVE_BEFORE_WRITE = 8;

	/**
 * 
 */
	public FilePlayer(String directory, String name) {
		super(name);
		initFile(directory);

	}
	public FilePlayer(String directory, Player player) {
		super(player);
		initFile(directory);

	}

	private void initFile(String directory) {
		File pFile = new File(directory, name + ".yml");
		try {
			Files.createParentDirs(pFile);
		} catch (IOException e) {
		}
		datas = ExtendedConfiguration.loadConfiguration(pFile);
		informations = datas.addSection("infos");
		homes = datas.addSection("home");
		powers = datas.addSection("powers");
		kitsUse = datas.addSection("kitsUse");
	}

	@Override
	public void setHome(String home, Location loc) {
		ConfigurationSection homeToSet = homes.createSection(home);
		homeToSet.set("world", loc.getWorld().getName());
		homeToSet.set("x", loc.getX());
		homeToSet.set("y", loc.getY());
		homeToSet.set("z", loc.getZ());
		homeToSet.set("yaw", loc.getYaw());
		homeToSet.set("pitch", loc.getPitch());
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#removeHome(java.lang.String)
	 */
	@Override
	public void removeHome(String home) {
		homes.set(home, null);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getHome(java.lang.String)
	 */
	@Override
	public Location getHome(String home) {
		Location loc = getLocation("home." + home);
		if (loc == null) {
			String name = Str.matchString(homes.getKeys(false), home);
			if (name == null)
				return null;
			loc = getLocation("home." + name);
		}
		return loc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#setInformation(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setInformation(String info, Object value) {
		informations.set(info, value);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#removeInformation(java.lang.String
	 * )
	 */
	@Override
	public void removeInformation(String info) {
		informations.set(info, null);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#getInformation(java.lang.String)
	 */
	@Override
	public ObjectContainer getInformation(String info) {
		return new ObjectContainer(informations.get(info));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#setLastLocation(org.bukkit.Location
	 * )
	 */
	@Override
	public void setLastLocation(Location loc) {
		if (loc == null)
			datas.set("lastLoc", null);
		else {
			ConfigurationSection lastLoc = datas.createSection("lastLoc");
			lastLoc.set("world", loc.getWorld().getName());
			lastLoc.set("x", loc.getX());
			lastLoc.set("y", loc.getY());
			lastLoc.set("z", loc.getZ());
			lastLoc.set("yaw", loc.getYaw());
			lastLoc.set("pitch", loc.getPitch());
		}

		writeFile();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getLastLocation()
	 */

	@Override
	public Location getLastLocation() {
		return getLocation("lastLoc");
	}

	private Location getLocation(String location) throws WorldNotLoaded {
		ConfigurationSection node = datas.getConfigurationSection(location);
		if (node == null)
			return null;
		if (node.get("world") == null) {
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

	@Override
	public void setPower(Type power, Object value) {
		powers.set(power.toString(), value);
		writeFile();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getPower(be.Balor.Tools.Type)
	 */

	@Override
	public ObjectContainer getPower(Type power) {
		return new ObjectContainer(powers.get(power.toString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#hasPower(be.Balor.Tools.Type)
	 */
	@Override
	public boolean hasPower(Type power) {
		return powers.contains(power.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#removePower(be.Balor.Tools.Type)
	 */

	@Override
	public void removePower(Type power) {
		powers.set(power.toString(), null);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getHomeList()
	 */

	@Override
	public Set<String> getHomeList() {
		return homes.getKeys(false);
	}

	private void writeFile() {
		if (saveCount == SAVE_BEFORE_WRITE || !isOnline) {
			forceSave();
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
		try {
			datas.save();
		} catch (IOException e) {
			ACLogger.severe("Problem while saving Player file of " + getName(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeAllSuperPower()
	 */
	@Override
	public void removeAllSuperPower() {
		for (String power : powers.getKeys(false)) {
			Type matched = Type.matchType(power);
			if (matched != null && matched.getCategory().equals(Category.SUPER_POWER))
				powers.set(power, null);
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
		powers.set(power, value);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getCustomPower(java.lang.String)
	 */
	@Override
	public ObjectContainer getCustomPower(String power) {
		return new ObjectContainer(powers.get(power));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#hasCustomPower(java.lang.String)
	 */
	@Override
	public boolean hasCustomPower(String power) {
		return !getCustomPower(power).isNull();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeCustomPower(java.lang.String)
	 */
	@Override
	public void removeCustomPower(String power) {
		powers.set(power, null);
		writeFile();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPlayerPowers()
	 */
	@Override
	public Map<String, String> getPowers() {
		TreeMap<String, String> result = new TreeMap<String, String>();
		for (Entry<String, Object> entry : powers.getValues(false).entrySet())
			result.put(entry.getKey(), entry.getValue().toString());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#updateLastKitUse(java.lang.String)
	 */
	@Override
	public void updateLastKitUse(String kit) {
		kitsUse.set(kit, System.currentTimeMillis());
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getLastKitUse(java.lang.String)
	 */
	@Override
	public long getLastKitUse(String kit) {
		return kitsUse.getLong(kit, 0L);
	}

}
