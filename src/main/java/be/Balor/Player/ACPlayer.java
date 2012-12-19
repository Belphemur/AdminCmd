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
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.ACCommandContainer;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.TpRequest;
import be.Balor.Tools.Type;
import be.Balor.Tools.Files.ObjectContainer;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public abstract class ACPlayer {
	protected final String name;
	private final int hashCode;
	protected boolean online = false;
	protected ACCommandContainer lastCmd = null;
	protected Player handler = null;
	protected TpRequest tpRequest = null;

	/**
	 *
	 */
	protected ACPlayer(final String name) {
		this.name = name;
		final int prime = 41;
		int result = 7;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		hashCode = result;
		handler = ACPluginManager.getServer().getPlayer(this.name);
	}

	protected ACPlayer(final Player p) {
		this.name = p.getName();
		final int prime = 41;
		int result = 7;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		hashCode = result;
		handler = p;
	}

	/**
	 * Get an instance of the wanted player
	 * 
	 * @param name
	 *            name of the player
	 * @return
	 */
	public static synchronized ACPlayer getPlayer(final String name) {
		return PlayerManager.getInstance().demandACPlayer(name);
	}

	/**
	 * Get an instance of the wanted player
	 * 
	 * @param player
	 *            instance of bukkit player
	 * @return
	 */
	public static synchronized ACPlayer getPlayer(final Player player) {
		return PlayerManager.getInstance().demandACPlayer(player);
	}

	/**
	 * Get all player having the select power
	 * 
	 * @param power
	 *            power to check
	 * @return
	 */
	public static List<ACPlayer> getPlayers(final Type power) {
		return PlayerManager.getInstance().getACPlayerHavingPower(power);
	}

	/**
	 * Get all player having the select power
	 * 
	 * @param power
	 *            power to check
	 * @return
	 */
	public static List<ACPlayer> getPlayers(final String power) {
		return PlayerManager.getInstance().getACPlayerHavingPower(power);
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
	public abstract Set<String> getHomeList();

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
	 * Get the list of all informations
	 * 
	 * @return
	 */
	public abstract Set<String> getInformationsList();

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
	public void setPower(final Type power) {
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
	public void setCustomPower(final String power) {
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
	 * Update the timestamp representing the last use of the kit
	 * 
	 * @param kit
	 *            name of the kit
	 */
	public void updateLastKitUse(final String kit) {
		setLastKitUse(kit, System.currentTimeMillis());
	}

	/**
	 * Set the timestamp representing the last use of the kit
	 * 
	 * @param kit
	 * @param timestamp
	 */
	public abstract void setLastKitUse(String kit, long timestamp);

	/**
	 * Get the last use of the kit
	 * 
	 * @param kit
	 *            name of the kit
	 * @return timestamp representing the last use of the kit
	 */
	public abstract long getLastKitUse(String kit);

	/**
	 * Get the list of every kit used.
	 * 
	 * @return
	 */
	public abstract Set<String> getKitUseList();

	/**
	 * Force the save
	 */
	protected abstract void forceSave();

	/**
	 * Update the played time of the player
	 * 
	 */
	public void updatePlayedTime() {
		setInformation("totalTime", getCurrentPlayedTime());
	}

	/**
	 * Get a power's list with all there values
	 * 
	 * @return
	 */
	public abstract Map<String, String> getPowersString();

	/**
	 * 
	 * @return the total played time in Long
	 */
	public long getCurrentPlayedTime() {
		if (this.isOnline()) {
			return (getInformation("totalTime").getLong(0)
					+ System.currentTimeMillis() - getInformation(
						"lastConnection").getLong(System.currentTimeMillis()));
		} else {
			return getInformation("totalTime").getLong(0);
		}
	}

	/**
	 * Set the player presentation
	 * 
	 * @param presentation
	 *            a little text to present the player
	 */
	public abstract void setPresentation(String presentation);

	/**
	 * Get the presentation of the player
	 * 
	 * @return text to present the player
	 */
	public abstract String getPresentation();
	/**
	 * Get all the powers of the player
	 * 
	 * @return powers of the player
	 */
	public abstract Map<Type, Object> getPowers();
	/**
	 * Get all the custom powers of the player
	 * 
	 * @return powers of the player
	 */
	public abstract Map<String, Object> getCustomPowers();

	/**
	 * @param isOnline
	 *            the isOnline to set
	 */
	void setOnline(final boolean isOnline) {
		this.online = isOnline;
		if (!this.online) {
			this.handler = null;
		} else if (handler == null) {
			this.handler = ACPluginManager.getServer().getPlayer(this.name);
		}
	}

	/**
	 * @param lastCmd
	 *            the last Command to set
	 */
	public void setLastCmd(final ACCommandContainer lastCmd) {
		this.lastCmd = lastCmd;
	}

	/**
	 * Execute the last command
	 * 
	 * @throws NullPointerException
	 *             if last command is not defined
	 * @throws PlayerNotFound
	 */
	public void executeLastCmd() throws NullPointerException, PlayerNotFound {
		if (this.lastCmd == null) {
			throw new NullPointerException();
		}
		try {
			this.lastCmd.execute();
		} catch (final ActionNotPermitedException e) {
			e.sendMessage();
		}
	}

	/**
	 * @param tpRequest
	 *            the tpRequest to set
	 */
	public void setTpRequest(final TpRequest tpRequest) {
		this.tpRequest = tpRequest;
	}

	/**
	 * @return the tpRequest
	 */
	public TpRequest getTpRequest() {
		return tpRequest;
	}

	public void removeTpRequest() {
		tpRequest = null;
	}

	/**
	 * @return if the player is online.
	 */
	public boolean isOnline() {
		return online;
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
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ACPlayer)) {
			return false;
		}
		final ACPlayer other = (ACPlayer) obj;
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
