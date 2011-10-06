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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.entity.Player;

import be.Balor.Tools.Type;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerManager {
	private ConcurrentMap<String, ACPlayer> players = new MapMaker().concurrencyLevel(8)
			.weakValues().makeMap();
	private ConcurrentMap<ACPlayer, Boolean> onlinePlayers = new MapMaker().concurrencyLevel(8)
			.weakValues().makeMap();
	private static PlayerManager instance = null;
	private ACPlayerFactory playerFactory;

	/**
	 * 
	 */
	private PlayerManager() {
		EmptyPlayer console = new EmptyPlayer("serverConsole");
		onlinePlayers.put(console, true);
		addPlayer(console);
	}

	/**
	 * @return the instance
	 */
	public static PlayerManager getInstance() {
		if (instance == null)
			instance = new PlayerManager();
		return instance;
	}

	/**
	 * @param playerFactory
	 *            the playerFactory to set
	 */
	public void setPlayerFactory(ACPlayerFactory playerFactory) {
		this.playerFactory = playerFactory;
	}

	/**
	 * Add a new player
	 * 
	 * @param player
	 */
	private synchronized boolean addPlayer(ACPlayer player) {
		final String name = player.getName();
		if (name == null) {
			throw new NullPointerException();
		}

		ACPlayer ref = players.get(name);
		if (ref != null)
			return false;
		players.put(name, player);
		if (player.getHandler() != null) {
			onlinePlayers.put(player, true);
			player.setOnline(true);
		}
		return true;
	}

	/**
	 * Return online AC players
	 * 
	 * @return
	 */
	public List<ACPlayer> getOnlineACPlayers() {
		return new ArrayList<ACPlayer>(onlinePlayers.keySet());
	}

	/**
	 * Get Online Bukkit Player
	 * 
	 * @return
	 */
	public List<Player> getOnlinePlayers() {
		ArrayList<Player> list = new ArrayList<Player>(onlinePlayers.size());
		for (ACPlayer p : onlinePlayers.keySet()) {
			Player handler = p.getHandler();
			if (handler != null)
				list.add(handler);
		}
		return list;
	}

	/**
	 * Get the list of AC Player having the wanted custom power
	 * 
	 * @param power
	 * @return
	 */
	List<ACPlayer> getACPlayerHavingPower(String power) {
		ArrayList<ACPlayer> list = new ArrayList<ACPlayer>();
		for (ACPlayer p : getExistingPlayers()) {
			if (p.hasCustomPower(power))
				list.add(p);
		}
		return list;
	}

	/**
	 * Get the list of AC Player having the wanted power
	 * 
	 * @param power
	 * @return
	 */
	List<ACPlayer> getACPlayerHavingPower(Type power) {
		ArrayList<ACPlayer> list = new ArrayList<ACPlayer>();
		for (ACPlayer p : getExistingPlayers()) {
			if (p.hasPower(power))
				list.add(p);
		}
		return list;
	}

	private List<ACPlayer> getExistingPlayers() {
		ArrayList<ACPlayer> list = new ArrayList<ACPlayer>();
		for (String name : playerFactory.getExistingPlayers()) {
			ACPlayer player = demandACPlayer(name);
			if (!(player instanceof EmptyPlayer))
				list.add(player);
		}
		return list;
	}

	/**
	 * Get the wanted player
	 * 
	 * @param name
	 *            name of the player
	 * @return the ACPlayer if found, else null
	 */
	private synchronized ACPlayer getPlayer(String name) {
		return players.get(name);
	}

	/**
	 * Set Offline an online player. The player will lost his strong reference,
	 * when the gc will be called, the reference will be deleted.
	 * 
	 * @param player
	 *            player to setOffline
	 * @return
	 */
	public boolean setOffline(ACPlayer player) {
		player.updatePlayedTime();
		player.forceSave();
		player.setOnline(false);
		return onlinePlayers.remove(player) != null;
	}

	public void setOnline(String player) {
		playerFactory.addExistingPlayer(player);
	}

	ACPlayer demandACPlayer(String name) {
		if (name == null)
			return getPlayer("serverConsole");
		ACPlayer result = getPlayer(name);
		if (result == null) {
			result = playerFactory.createPlayer(name);
			addPlayer(result);
			result = getPlayer(name);
		} else if (result instanceof EmptyPlayer) {
			ACPlayer tmp = playerFactory.createPlayer(name);
			if (tmp.equals(result))
				return result;
			result = tmp;
			players.remove(name);
			addPlayer(result);
			result = getPlayer(name);
		}

		return result;
	}

}
