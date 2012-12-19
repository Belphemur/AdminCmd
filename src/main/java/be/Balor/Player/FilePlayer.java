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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import be.Balor.Tools.Type;
import be.Balor.Tools.Type.Category;
import be.Balor.Tools.Configuration.ExConfigurationSection;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.Tools.Help.String.Str;
import be.Balor.Tools.Threads.IOSaveTask;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;
import belgium.Balor.Workers.InvisibleWorker;

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
	FilePlayer(final String directory, final String name) {
		super(name);
		final File pFile = new File(directory, name + ".yml");
		try {
			Files.createParentDirs(pFile);
		} catch (final IOException e) {
		}
		datas = ExtendedConfiguration.loadConfiguration(pFile);
		informations = datas.addSection("infos");
		homes = datas.addSection("home");
		powers = datas.addSection("powers");
		kitsUse = datas.addSection("kitsUse");
		lastLoc = informations.addSection("lastLoc");

	}

	FilePlayer(final String directory, final Player player) {
		super(player);
		final File pFile = new File(directory, name + ".yml");
		try {
			Files.createParentDirs(pFile);
		} catch (final IOException e) {
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
				|| ACPluginManager.getScheduler().isQueued(ioStackTaskId)) {
			return;
		}
		final int delay = ConfigEnum.WDELAY.getInt() >= 30 ? ConfigEnum.WDELAY
				.getInt() : 30;
		ioStackTaskId = ACPluginManager
				.getScheduler()
				.runTaskTimerAsynchronously(
						ACHelper.getInstance().getCoreInstance(), IOSAVET_TASK,
						20 * 60, 20 * delay).getTaskId();
		DebugLog.INSTANCE.info("IO Save RepeatingTask created : "
				+ ioStackTaskId);
	}

	/**
	 * To stop the saving task.
	 */
	public static void stopSavingTask() {
		if (!ACPluginManager.getScheduler().isCurrentlyRunning(ioStackTaskId)
				&& !ACPluginManager.getScheduler().isQueued(ioStackTaskId)) {
			return;
		}
		ACPluginManager.getScheduler().cancelTask(ioStackTaskId);
	}

	@Override
	public void setHome(final String home, final Location loc) {
		final ConfigurationSection homeToSet = homes.createSection(home);
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
	public void removeHome(final String home) {
		homes.set(home, null);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getHome(java.lang.String)
	 */
	@Override
	public Location getHome(final String home) {
		final ConfigurationSection homeSection = homes
				.getConfigurationSection(home);
		if (homeSection == null) {
			final String found = Str.matchString(homes.getKeys(false), home);
			if (found == null) {
				return null;
			}
			return getLocation(homes.getConfigurationSection(found));
		} else {
			return getLocation(homeSection);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Player.Data.PlayerDataManager#setInformation(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setInformation(final String info, final Object value) {
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
	public void removeInformation(final String info) {
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
	public ObjectContainer getInformation(final String info) {
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
	public void setLastLocation(final Location loc) {
		if (loc == null) {
			return;
		}
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

	private Location getLocation(final ConfigurationSection node) {
		final String world = node.getString("world");
		if (world == null) {
			return null;
		}
		final World w = ACPluginManager.getServer().getWorld(world);
		if (w != null) {
			return new Location(w, node.getDouble("x", 0), node.getDouble("y",
					0), node.getDouble("z", 0), Float.parseFloat(node
					.getString("yaw")), Float.parseFloat(node
					.getString("pitch")));
		} else {
			ACLogger.warning("The world " + world + " is not loaded !");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#setPower(be.Balor.Tools.Type,
	 * java.lang.Object)
	 */

	@Override
	public void setPower(final Type power, final Object value) {
		powers.set(power.toString(), value);
		writeFile();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.Data.PlayerDataManager#getPower(be.Balor.Tools.Type)
	 */

	@Override
	public ObjectContainer getPower(final Type power) {
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
	public boolean hasPower(final Type power) {
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
	public void removePower(final Type power) {
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
	protected void forceSave() {
		try {
			IOSAVET_TASK.removeConfiguration(datas);
			datas.save();
		} catch (final IOException e) {
			ACLogger.severe("Problem while saving Player file of " + getName(),
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeAllSuperPower()
	 */
	@Override
	public void removeAllSuperPower() {
		for (final String power : powers.getKeys(false)) {
			final Type matched = Type.matchType(power);
			if (matched != null
					&& matched.getCategory().equals(Category.SUPER_POWER)) {
				powers.set(power, null);
				if (matched != Type.FLY) {
					continue;
				}
				if (handler == null) {
					continue;
				}
				handler.setFlying(false);
				handler.setAllowFlight(false);
			}
		}

		if (handler != null
				&& InvisibleWorker.getInstance().hasInvisiblePowers(handler)) {
			InvisibleWorker.getInstance().reappear(handler);
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
	public void setCustomPower(final String power, final Object value) {
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
	public ObjectContainer getCustomPower(final String power) {
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
	public boolean hasCustomPower(final String power) {
		return !getCustomPower(power).isNull();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#removeCustomPower(java.lang.String)
	 */
	@Override
	public void removeCustomPower(final String power) {
		powers.set(power, null);
		writeFile();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPlayerPowers()
	 */
	@Override
	public Map<String, String> getPowersString() {
		final TreeMap<String, String> result = new TreeMap<String, String>();
		for (final Entry<String, Object> entry : powers.getValues(false)
				.entrySet()) {
			result.put(entry.getKey(), entry.getValue().toString());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getLastKitUse(java.lang.String)
	 */
	@Override
	public long getLastKitUse(final String kit) {
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
	public void setPresentation(final String presentation) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getInformationsList()
	 */
	@Override
	public Set<String> getInformationsList() {
		return informations.getKeys(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getKitUseList()
	 */
	@Override
	public Set<String> getKitUseList() {
		return kitsUse.getKeys(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#setLastKitUse(java.lang.String, long)
	 */
	@Override
	public void setLastKitUse(final String kit, final long timestamp) {
		kitsUse.set(kit, timestamp);
		writeFile();
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getPowers()
	 */
	@Override
	public Map<Type, Object> getPowers() {
		final Map<Type, Object> result = new EnumMap<Type, Object>(Type.class);
		for (final Entry<String, Object> entry : powers.getValues(false)
				.entrySet()) {
			final Type power = Type.matchType(entry.getKey());
			if (power == null || (power != null && power == Type.CUSTOM)) {
				continue;
			}
			result.put(power, entry.getValue());
		}
		return result;
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.Player.ACPlayer#getCustomPowers()
	 */
	@Override
	public Map<String, Object> getCustomPowers() {
		final Map<String, Object> result = new HashMap<String, Object>();
		for (final Entry<String, Object> entry : powers.getValues(false)
				.entrySet()) {
			final Type power = Type.matchType(entry.getKey());
			if (power != null && power != Type.CUSTOM) {
				continue;
			}
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
