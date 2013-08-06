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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import be.Balor.Tools.Type;
import be.Balor.Tools.Converter.PlayerConverter;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerManager {
	private final ConcurrentMap<String, ACPlayer> players = new MapMaker().concurrencyLevel(8).weakValues().makeMap();
	private final ConcurrentMap<ACPlayer, Boolean> onlinePlayers = new MapMaker().concurrencyLevel(8).makeMap();
	private final static PlayerManager INSTANCE = new PlayerManager();
	private IPlayerFactory playerFactory;

	/**
	 *
	 */
	private PlayerManager() {
		final EmptyPlayer console = new EmptyPlayer("serverConsole");
		onlinePlayers.put(console, true);
		addPlayer(console);
	}

	/**
	 * @return the instance
	 */
	public static PlayerManager getInstance() {
		return INSTANCE;
	}

	/**
	 * @param playerFactory
	 *            the playerFactory to set
	 */
	public void setPlayerFactory(final IPlayerFactory playerFactory) {
		this.playerFactory = playerFactory;
	}

	/**
	 * Convert the ACPlayer
	 * 
	 * @param playerFactory
	 */
	public void convertFactory(final IPlayerFactory factory) {
		buildConverter(factory).convert();
	}

	/**
	 * Build a PlayerConverter with the current and the new factory
	 * 
	 * @param newFactory
	 * @return
	 */
	public PlayerConverter buildConverter(final IPlayerFactory newFactory) {
		return new PlayerConverter(playerFactory, newFactory);
	}

	/**
	 * Add a new player
	 * 
	 * @param player
	 */
	private synchronized boolean addPlayer(final ACPlayer player) {
		final String name = player.getName();
		if (name == null) {
			throw new NullPointerException();
		}

		final ACPlayer ref = players.get(name);
		if (ref != null) {
			return false;
		}
		players.put(name, player);
		return true;
	}

	/**
	 * Return online AC players
	 * 
	 * @return
	 */
	public Set<ACPlayer> getOnlineACPlayers() {
		return Collections.unmodifiableSet(onlinePlayers.keySet());
	}

	/**
	 * Get Online Bukkit Player
	 * 
	 * @return
	 */
	public List<Player> getOnlinePlayers() {
		final ArrayList<Player> list = new ArrayList<Player>(onlinePlayers.size());
		for (final ACPlayer p : onlinePlayers.keySet()) {
			final Player handler = p.getHandler();
			if (handler != null && handler.isOnline()) {
				list.add(handler);
			}
		}
		return list;
	}

	/**
	 * Get the list of AC Player having the wanted custom power
	 * 
	 * @param power
	 * @return
	 */
	List<ACPlayer> getACPlayerHavingPower(final String power) {
		final ArrayList<ACPlayer> list = new ArrayList<ACPlayer>();
		for (final ACPlayer p : getExistingPlayers()) {
			if (p.hasCustomPower(power)) {
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * Get the list of AC Player having the wanted power
	 * 
	 * @param power
	 * @return
	 */
	List<ACPlayer> getACPlayerHavingPower(final Type power) {
		final ArrayList<ACPlayer> list = new ArrayList<ACPlayer>();
		for (final ACPlayer p : getExistingPlayers()) {
			if (p.hasPower(power)) {
				list.add(p);
			}
		}
		return list;
	}

	public List<ACPlayer> getExistingPlayers() {
		final ArrayList<ACPlayer> list = new ArrayList<ACPlayer>();
		for (final String name : playerFactory.getExistingPlayers()) {
			try {
				final ACPlayer player = demandACPlayer(name);
				if (!(player instanceof EmptyPlayer)) {
					list.add(player);
				}
			} catch (final Exception e) {
				DebugLog.INSTANCE.log(Level.WARNING, "Problem with instancing ACPlayer : " + name, e);
			}
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
	private synchronized ACPlayer getPlayer(final String name) {
		final ACPlayer result = players.get(name);
		return result;
	}

	/**
	 * Set Offline an online player. The player will lost his strong reference,
	 * when the gc will be called, the reference will be deleted.
	 * 
	 * @param player
	 *            player to setOffline
	 * @return
	 */
	public boolean setOffline(final ACPlayer player) {
		player.updatePlayedTime();
		player.forceSave();
		player.setOnline(false);
		return onlinePlayers.remove(player) != null;
	}

	public ACPlayer setOnline(final Player player) {
		playerFactory.addExistingPlayer(player.getName());
		final ACPlayer acPlayer = demandACPlayer(player);
		onlinePlayers.put(acPlayer, true);
		acPlayer.setOnline(true);
		DebugLog.INSTANCE.info(player.getName() + " is put online.");
		return acPlayer;
	}

	synchronized ACPlayer demandACPlayer(final String name) {
		if (name == null) {
			return getPlayer("serverConsole");
		}
		ACPlayer result = getPlayer(name);
		if (result == null) {
			result = playerFactory.createPlayer(name);
			addPlayer(result);
			result = getPlayer(name);
		} else if (result instanceof EmptyPlayer) {
			final ACPlayer tmp = playerFactory.createPlayer(name);
			if (tmp instanceof EmptyPlayer) {
				return result;
			}
			players.remove(name);
			onlinePlayers.remove(result);
			result = tmp;
			addPlayer(result);
			result = getPlayer(name);
		}
		return result;
	}

	synchronized ACPlayer demandACPlayer(final Player player) {
		if (player == null) {
			return getPlayer("serverConsole");
		}
		final String playerName = player.getName();
		ACPlayer result = getPlayer(playerName);
		if (result == null) {
			result = playerFactory.createPlayer(player);
			addPlayer(result);
			result = getPlayer(playerName);
		} else if (result instanceof EmptyPlayer) {
			final ACPlayer tmp = playerFactory.createPlayer(playerName);
			if (tmp instanceof EmptyPlayer) {
				return result;
			}
			players.remove(playerName);
			onlinePlayers.remove(result);
			result = tmp;
			addPlayer(result);
			result = getPlayer(playerName);
		}
		return result;
	}

	/**
	 * @param newFactory
	 */
	synchronized void afterFactoryConversion(final IPlayerFactory newFactory) {
		DebugLog.INSTANCE.info("After Conversion launched");
		final Map<ACPlayer, Boolean> onlineCopy = new HashMap<ACPlayer, Boolean>();
		onlineCopy.putAll(onlinePlayers);
		ACPluginManager.scheduleSyncTask(new Runnable() {

			@Override
			public void run() {
				ACLogger.info("Update status of online players : " + onlineCopy.size());
				for (final ACPlayer p : onlineCopy.keySet()) {
					if (!(p instanceof EmptyPlayer)) {
						new PlayerConvertTask(newFactory, p).run();
					}
				}
				ACLogger.info("Players conversion Finished");

			}
		});
		this.onlinePlayers.clear();
		this.players.clear();
		this.playerFactory = newFactory;
		DebugLog.INSTANCE.info("Player Conversion Finished (ASYNC)");

	}

}
