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

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.Balor.Tools.Type;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class ACPlayer {
	private static int playerCount = 0;
	private final String name;
	private final int playerID;
	private final int hashCode;
	protected boolean isOnline = false;

	/**
	 * 
	 */
	protected ACPlayer(String name) {
		this.name = name;
		playerID = playerCount++;
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + playerID;
		hashCode = result;
	}

	/**
	 * Get an instance of the wanted player
	 * 
	 * @param name
	 * @return
	 */
	public static ACPlayer getPlayer(String name) {
		return PlayerManager.getInstance().demandACPlayer(name);
	}

	/**
	 * Get the bukkit player
	 * 
	 * @return
	 */
	public Player getHandler() {
		return ACPluginManager.getServer().getPlayer(name);
	}

	/**
	 * Get Player Name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Add a new home for the player
	 * 
	 * @param home
	 *            home name
	 * @param loc
	 *            location of the home
	 */
	public abstract void setHome(String home, Location loc);

	/**
	 * Remove a home of the player
	 * 
	 * @param home
	 *            name of the home
	 */
	public abstract void removeHome(String home);

	/**
	 * Get a home of the player
	 * 
	 * @param home
	 *            name of the home
	 */
	public abstract Location getHome(String home);

	/**
	 * Get the home containing the home of the player
	 * 
	 * @return list containing homes of the user
	 */
	public abstract List<String> getHomeList();

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
	 * Set the last location of the player before TP or dying
	 * 
	 * @param loc
	 */
	public abstract void setLastLocation(Location loc);

	/**
	 * Get the last location of the player before TP or dying
	 * 
	 * @return
	 */
	public abstract Location getLastLocation();

	/**
	 * Set the power of the user with a default value
	 * 
	 * @param power
	 */
	public abstract void setPower(Type power);

	/**
	 * Set the power of the user with a given value
	 * 
	 * @param power
	 * @param value
	 */
	public abstract void setPower(Type power, Object value);

	/**
	 * Get the power of the user
	 * 
	 * @param Power
	 * @return
	 */
	public abstract ObjectContainer getPower(Type power);

	/**
	 * Check if the player have the wanted power
	 * 
	 * @param power
	 * @return
	 */
	public abstract boolean hasPower(Type power);

	/**
	 * Remove the power of the user
	 * 
	 * @param power
	 */
	public abstract void removePower(Type power);

	/**
	 * Remove all Super Power like fly, god, etc ... but not the sanctions
	 */
	public abstract void removeAllSuperPower();

	/**
	 * Force the save
	 */
	abstract void forceSave();

	/**
	 * Update the played time of the player
	 * 
	 * @return the total played time in Long
	 */
	public long updatePlayedTime() {
		long total = getInformation("totalTime").getLong(0) + System.currentTimeMillis()
				- getInformation("lastConnection").getLong(0);
		setInformation("totalTime", total);
		return total;
	}
	/**
	 * @param isOnline the isOnline to set
	 */
	void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ACPlayer))
			return false;
		ACPlayer other = (ACPlayer) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (playerID != other.playerID)
			return false;
		return true;
	}

}
