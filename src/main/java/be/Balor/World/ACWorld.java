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

import java.util.Map;
import java.util.Set;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;

import be.Balor.Manager.Exceptions.WorldNotLoaded;
import be.Balor.Tools.Warp;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class ACWorld {
	private final String name;
	protected final World handler;
	private final int hashCode;

	/**
	 *
	 */
	protected ACWorld(final World world) {
		final int prime = 37;
		int result = 5;
		handler = world;
		name = world.getName();
		result = prime * result + ((handler == null) ? 0 : handler.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		hashCode = result;
	}

	/**
	 * @return the handler
	 */
	public World getHandler() {
		return handler;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the wanted world
	 * 
	 * @param name
	 *            name of the wanted world
	 * @return the ACWorld
	 * @throws WorldNotLoaded
	 *             if the world is not loaded in bukkit or don't exist
	 */
	public static ACWorld getWorld(final String name) throws WorldNotLoaded {
		return WorldManager.getInstance().demandACWorld(name);
	}

	/**
	 * Get the wanted world
	 * 
	 * @param world
	 *            loaded bukkit world
	 * @return the ACWorld
	 */
	public static ACWorld getWorld(final World world) {
		return WorldManager.getInstance().demandACWorld(world);
	}

	/**
	 * Set the spawn location
	 * 
	 * @param loc
	 *            location of the spawn
	 */
	public abstract void setSpawn(Location loc);

	/**
	 * Get the spawn location
	 * 
	 * @return the spawn location
	 */
	public abstract Location getSpawn();

	/**
	 * Get the worlds difficulty
	 * 
	 * @return The difficulty
	 */
	public Difficulty getDifficulty() {
		final Difficulty dif = getInformation("difficulty").getDifficulty();
		if (dif == null) {
			return handler.getDifficulty();
		} else {
			return dif;
		}
	}

	/**
	 * Set the difficulty of the world.
	 * 
	 * @param dif
	 *            The difficulty to set.
	 */
	public void setDifficulty(final Difficulty dif) {
		ACPluginManager.scheduleSyncTask(new Runnable() {
			@Override
			public void run() {
				handler.setDifficulty(dif);
			}
		});
		setInformation("difficulty", dif);
	}

	/**
	 * Add a warp point
	 * 
	 * @param name
	 *            name of the warp
	 * @param loc
	 *            location of the warp
	 */
	public abstract void addWarp(String name, Location loc);
	
	/**
	 * Add a warp point with a permission
	 * 
	 * @param name
	 *            name of the warp
	 * @param loc
	 *            location of the warp
	 * @param perm
	 * ...........permission needed to use this warp
	 */
	public abstract void addPermWarp(String name, Location loc, String perm);

	/**
	 * Get the location of the Warp
	 * 
	 * @param name
	 *            name of the Warp
	 * @return location of the Warp
	 * @throws WorldNotLoaded
	 *             if the location's world is not loaded
	 * @throws IllegalArgumentException
	 *             if the name is null or empty
	 */
	public abstract Warp getWarp(String name) throws WorldNotLoaded,
			IllegalArgumentException;

	/**
	 * List of the warps' name
	 * 
	 * @return a List containing the name of each warp of the World
	 */
	public abstract Set<String> getWarpList();
	
	/**
	 * Remove the warp
	 * 
	 * @param name
	 *            name of the warp to remove
	 */
	public abstract void removeWarp(String name);

	/**
	 * Set player information
	 * 
	 * @param info
	 *            key of the information
	 * @param value
	 *            information to set
	 */
	public abstract void setInformation(String info, Object value);

	/**
	 * Remove the information
	 * 
	 * @param info
	 *            key of the information
	 */
	public abstract void removeInformation(String info);

	/**
	 * Get the information
	 * 
	 * @param info
	 *            key of the information
	 */
	public abstract ObjectContainer getInformation(String info);

	/**
	 * Get all informations about the world
	 * 
	 * @return
	 */
	public abstract Map<String, String> getInformationsList();

	/**
	 * Force the save
	 */
	protected abstract void forceSave();

	/**
	 * To set a limit for a specific mob
	 * 
	 * @param mob
	 *            name of the mob
	 * @param limit
	 *            limit for that mob
	 */
	public abstract void setMobLimit(String mob, int limit);

	/**
	 * To remove the limit set on a specific mob
	 * 
	 * @param mob
	 *            name of the mob
	 * @return true if the limit is removed, false if there is no limit
	 */
	public abstract boolean removeMobLimit(String mob);

	/**
	 * Get the limit for the given mob.
	 * 
	 * @param mob
	 *            name of the mob
	 * @return the limit if is set, else return -1.
	 */
	public abstract int getMobLimit(String mob);

	/**
	 * Get the list of every limited mobs
	 * 
	 * @return
	 */
	public abstract Set<String> getMobLimitList();

	/**
	 * Get the location of the spawn for the wanted group
	 * 
	 * @param group
	 *            name of the group. If null, return the location of the default
	 *            spawn.
	 * @return location of the spawn point. If no location has been set for the
	 *         group, return the default spawn.
	 */
	public abstract Location getGroupSpawn(String group);

	/**
	 * Set the location of the spawn for the given group
	 * 
	 * @param group
	 *            name of the group.
	 * @param spawn
	 *            location of the new spawn.
	 */
	public abstract void setGroupSpawn(String group, Location spawn);

	/**
	 * Get all group spawn
	 * 
	 * @return
	 */
	protected abstract Map<String, Location> getGroupSpawns();

	/**
	 * Get all Informations
	 * 
	 * @return
	 */
	protected abstract Map<String, Object> getInformations();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ACWorld)) {
			return false;
		}
		final ACWorld other = (ACWorld) obj;
		if (handler == null) {
			if (other.handler != null) {
				return false;
			}
		} else if (!handler.equals(other.handler)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
