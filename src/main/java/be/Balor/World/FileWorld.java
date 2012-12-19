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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Warp;
import be.Balor.Tools.Configuration.ExConfigurationSection;
import be.Balor.Tools.Configuration.File.ExtendedConfiguration;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.Tools.Help.String.Str;

import com.google.common.io.Files;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class FileWorld extends ACWorld {
	private final ExtendedConfiguration datas;
	private final ConfigurationSection warps;
	private final ExConfigurationSection informations;
	private final ExConfigurationSection mobLimits;
	private final ExConfigurationSection spawns;

	/**
	 * @param name
	 */
	FileWorld(final World world, final String directory) {
		super(world);
		final File wFile = new File(directory, world.getName() + ".yml");
		try {
			Files.createParentDirs(wFile);
		} catch (final IOException e) {
		}
		datas = ExtendedConfiguration.loadConfiguration(wFile);

		warps = datas.addSection("warps");
		informations = datas.addSection("informations");
		mobLimits = informations.addSection("mobLimits");
		spawns = datas.addSection("spawns");
		forceSave();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setSpawn(org.bukkit.Location)
	 */
	@Override
	public void setSpawn(final Location loc) {
		datas.set("spawn", new SimpleLocation(loc));
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getSpawn()
	 */
	@Override
	public Location getSpawn() throws WorldNotLoaded {
		final Object spawn = datas.get("spawn");
		if (spawn == null) {
			return handler.getSpawnLocation();
		} else if (spawn instanceof SimpleLocation) {
			return ((SimpleLocation) spawn).getLocation();
		} else {
			return handler.getSpawnLocation();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#addWarp(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void addWarp(final String name, final Location loc) {
		warps.set(name, new SimpleLocation(loc));
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarp(java.lang.String)
	 */
	@Override
	public Warp getWarp(final String name) throws WorldNotLoaded,
			IllegalArgumentException {
		if (name == null || (name != null && name.isEmpty())) {
			throw new IllegalArgumentException("Name can't be null or Empty");
		}
		Object warp = warps.get(name);
		String warpName = name;
		if (warp == null) {
			warpName = Str.matchString(warps.getKeys(false), name);
			if (warpName == null) {
				return null;
			}
			warp = warps.get(warpName);
		}
		try {
			return new Warp(warpName, ((SimpleLocation) warp).getLocation());
		} catch (ClassCastException e) {
			return new Warp(warpName, ((PermLocation) warp).getLocation(), ((PermLocation) warp).getPerm());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getWarpList()
	 */
	@Override
	public Set<String> getWarpList() {
		return warps.getKeys(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeWarp(java.lang.String)
	 */
	@Override
	public void removeWarp(final String name) {
		warps.set(name, null);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * @see be.Balor.World.ACWorld#addPermWarp(java.lang.String, org.bukkit.Location, java.lang.String)
	 */
	@Override
	public void addPermWarp(String name, Location loc, String perm) {
		warps.set(name, new PermLocation(loc, perm));
		writeFile();		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setInformation(java.lang.String,
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
	 * @see be.Balor.World.ACWorld#removeInformation(java.lang.String)
	 */
	@Override
	public void removeInformation(final String info) {
		informations.set(info, null);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformation(java.lang.String)
	 */
	@Override
	public ObjectContainer getInformation(final String info) {
		return new ObjectContainer(informations.get(info));
	}

	/**
	 *
	 */
	private void writeFile() {
		forceSave();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#forceSave()
	 */
	@Override
	protected void forceSave() {
		try {
			datas.save();
		} catch (final IOException e) {
			ACLogger.severe("Problem when saving the World File of "
					+ getName(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformations()
	 */
	@Override
	public Map<String, String> getInformationsList() {
		final TreeMap<String, String> result = new TreeMap<String, String>();
		for (final Entry<String, Object> entry : informations.getValues(false)
				.entrySet()) {
			result.put(entry.getKey(), entry.getValue().toString());
		}
		for (final Entry<String, Object> entry : mobLimits.getValues(false)
				.entrySet()) {
			result.put("Limit on " + entry.getKey(), entry.getValue()
					.toString());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setMobLimit(java.lang.String, int)
	 */
	@Override
	public void setMobLimit(final String mob, final int limit) {
		mobLimits.set(mob, limit);
		writeFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#removeMobLimit(java.lang.String)
	 */
	@Override
	public boolean removeMobLimit(final String mob) {
		if (mobLimits.isSet(mob)) {
			mobLimits.remove(mob);
			writeFile();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getMobLimit(java.lang.String)
	 */
	@Override
	public int getMobLimit(final String mob) {
		return mobLimits.getInt(mob, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getMobLimitList()
	 */
	@Override
	public Set<String> getMobLimitList() {
		return mobLimits.getKeys(false);
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getGroupSpawn(java.lang.String)
	 */
	@Override
	public Location getGroupSpawn(final String group) {
		if (group == null) {
			return getSpawn();
		}
		final Object spawn = spawns.get(group);
		if (spawn == null) {
			return getSpawn();
		} else if (spawn instanceof SimpleLocation) {
			return ((SimpleLocation) spawn).getLocation();
		} else {
			return getSpawn();
		}
	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#setGroupSpawn(java.lang.String,
	 * org.bukkit.Location)
	 */
	@Override
	public void setGroupSpawn(final String group, final Location spawn) {
		spawns.set(group, new SimpleLocation(spawn));

	}

	/*
	 * (Non javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getGroupSpawns()
	 */
	@Override
	protected Map<String, Location> getGroupSpawns() {
		final Map<String, Location> result = new HashMap<String, Location>();
		for (final Entry<String, Object> entry : spawns.getValues(false)
				.entrySet()) {
			final Object obj = entry.getValue();
			if (!(obj instanceof SimpleLocation)) {
				continue;
			}
			result.put(entry.getKey(), ((SimpleLocation) obj).getLocation());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.World.ACWorld#getInformations()
	 */
	@Override
	protected Map<String, Object> getInformations() {
		final Map<String, Object> result = new HashMap<String, Object>();
		for (final Entry<String, Object> entry : informations.getValues(false)
				.entrySet()) {
			if (entry.getKey().contains("mobLimits")) {
				continue;
			}
			result.put(entry.getKey(), entry.getValue());
		}
		for (final Entry<String, Object> entry : mobLimits.getValues(false)
				.entrySet()) {
			result.put("mobLimit:" + entry.getKey(), entry.getValue()
					.toString());
		}
		return result;
	}
}
