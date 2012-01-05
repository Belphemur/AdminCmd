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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Type;
import be.Balor.Tools.Type.Category;
import be.Balor.Tools.Configuration.ExConfigurationSection;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.Tools.Threads.IOSaveTask;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

import com.google.common.io.Files;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FilePlayer extends ACPlayer {

	private final ExtendedConfiguration datas;
	private final ExConfigurationSection informations;
	private final ExConfigurationSection homes;
	private final ExConfigurationSection powers;
	private final ExConfigurationSection kitsUse;
	private final ExConfigurationSection lastLoc;
	private final static IOSaveTask IOSAVET_TASK = new IOSaveTask();
	private static int ioStackTaskId = -1;

	/**
 * 
 */
	public FilePlayer(String directory, String name) {
		super(name);
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
		lastLoc = informations.addSection("lastLoc");

	}

	public FilePlayer(String directory, Player player) {
		super(player);
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
		lastLoc = informations.addSection("lastLoc");

	}

	/**
	 * To be sure that all file waiting to be write, will be write when this is
	 * called
	 */
	public static void forceSaveList() {
		IOSAVET_TASK.run();
	}

	/**
	 * To Schedule the Async task
	 */
	public static void scheduleAsyncSave() {
		if (ACPluginManager.getScheduler().isCurrentlyRunning(ioStackTaskId)
				|| ACPluginManager.getScheduler().isQueued(ioStackTaskId))
			return;
		int delay = ACHelper.getInstance().getConfInt("delayBeforeWriteUserFileInSec") >= 30 ? ACHelper
				.getInstance().getConfInt("delayBeforeWriteUserFileInSec") : 120;
		ioStackTaskId = ACPluginManager.getScheduler().scheduleAsyncRepeatingTask(
				ACHelper.getInstance().getCoreInstance(), IOSAVET_TASK, 20 * 60, 20 * delay);
		DebugLog.INSTANCE.info("IO Save RepeatingTask created : " + ioStackTaskId);
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
		ConfigurationSection homeSection = homes.getConfigurationSection(home);
		if (homeSection == null)
			return null;
		else
			return getLocation(homeSection);
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
		informations.remove(info);
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
		Object infoObject;
		infoObject = informations.get(info);
		return new ObjectContainer(infoObject);
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
			return;
		lastLoc.set("world", loc.getWorld().getName());
		lastLoc.set("x", loc.getX());
		lastLoc.set("y", loc.getY());
		lastLoc.set("z", loc.getZ());
		lastLoc.set("yaw", loc.getYaw());
		lastLoc.set("pitch", loc.getPitch());
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getLastLocation()
	 */

	@Override
	public Location getLastLocation() {
		Location loc;
		loc = getLocation(lastLoc);
		return loc;
	}

	private Location getLocation(ConfigurationSection node) throws WorldNotLoaded {
		World w = ACPluginManager.getServer().getWorld(node.getString("world"));
		if (w != null)
			return new Location(w, node.getDouble("x", 0), node.getDouble("y", 0), node.getDouble(
					"z", 0), Float.parseFloat(node.getString("yaw")), Float.parseFloat(node
					.getString("pitch")));
		else
			throw new WorldNotLoaded(node.getString("world"));
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
		Object result;
		result = powers.get(power.toString());
		return new ObjectContainer(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#hasPower(be.Balor.Tools.Type)
	 */
	@Override
	public boolean hasPower(Type power) {
		boolean contain = false;
		contain = powers.contains(power.toString());
		return contain;
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
		Set<String> result = new HashSet<String>();
		result = homes.getKeys(false);
		return result;
	}

	private void writeFile() {
		IOSAVET_TASK.addConfigurationToSave(datas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#forceSave()
	 */
	@Override
	void forceSave() {
		try {
			IOSAVET_TASK.removeConfiguration(datas);
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
		Object powerObject;
		powerObject = powers.get(power);
		return new ObjectContainer(powerObject);
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
		long use = 0L;
		use = kitsUse.getLong(kit, 0L);
		return use;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setPresentation(java.lang.String)
	 */
	@Override
	public void setPresentation(String presentation) {
		informations.set("presentation", presentation);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPresentation()
	 */
	@Override
	public String getPresentation() {
		String pres = "";
		pres = informations.getString("presentation", "");
		return pres;
	}

}
