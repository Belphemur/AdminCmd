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

import be.Balor.Manager.Commands.ACCommandContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class ACPlayer {
	private final String name;
	private final int hashCode;
	protected boolean isOnline = false;
	protected ACCommandContainer lastCmd = null;
	protected Player handler = null;

	/**
	 *
	 */
	protected ACPlayer(String name) {
		this.name = name;
		final int prime = 41;
		int result = 7;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		hashCode = result;
		handler = ACPluginManager.getServer().getPlayer(this.name);
	}

	/**
	 * Get an instance of the wanted player
	 * 
	 * @param name
	 *            name of the player
	 * @return
	 */
	public static ACPlayer getPlayer(String name) {
		return PlayerManager.getInstance().demandACPlayer(name);
	}

	/**
	 * Get an instance of the wanted player
	 * 
	 * @param player
	 *            instance of bukkit player
	 * @return
	 */
	public static ACPlayer getPlayer(Player player) {
		return PlayerManager.getInstance().demandACPlayer(player.getName());
	}

	/**
	 * Get the bukkit player
	 * 
	 * @return
	 */
	public Player getHandler() {
		return handler;
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
	public void setPower(Type power) {
		setPower(power, true);
	}

	/**
	 * Set the power of the user with a given value
	 * 
	 * @param power
	 * @param value
	 */
	public abstract void setPower(Type power, Object value);

	/**
	 * Set the custom power of the user with a default value
	 * 
	 * @param custom
	 *            power
	 */
	public void setCustomPower(String power) {
		setCustomPower(power, true);
	}

	/**
	 * Set the custom power of the user with a given value
	 * 
	 * @param custom
	 *            power
	 * @param value
	 */
	public abstract void setCustomPower(String power, Object value);

	/**
	 * Get the custom power of the user
	 * 
	 * @param Power
	 * @return
	 */
	public abstract ObjectContainer getCustomPower(String power);

	/**
	 * Check if the player have the wanted custom power
	 * 
	 * @param power
	 * @return
	 */
	public abstract boolean hasCustomPower(String power);

	/**
	 * Remove the custom power of the user
	 * 
	 * @param power
	 */
	public abstract void removeCustomPower(String power);

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
				- getInformation("lastConnection").getLong(System.currentTimeMillis());
		setInformation("totalTime", total);
		return total;
	}

	/**
	 * @param isOnline
	 *            the isOnline to set
	 */
	void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
		if (!this.isOnline)
			handler = null;
	}

	/**
	 * @param lastCmd
	 *            the last Command to set
	 */
	public void setLastCmd(ACCommandContainer lastCmd) {
		this.lastCmd = lastCmd;
	}

	/**
	 * Execute the last command
	 * 
	 * @throws NullPointerException
	 *             if last command is not defined
	 */
	public void executeLastCmd() throws NullPointerException {
		if (this.lastCmd == null)
			throw new NullPointerException();
		this.lastCmd.execute();
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
		return true;
	}

}
