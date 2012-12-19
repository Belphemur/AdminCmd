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
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.Balor.Tools.Type;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.Tools.Debug.DebugLog;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class PlayerManager {
	private final ConcurrentMap<String, ACPlayer> players = new MapMaker()
			.concurrencyLevel(8).weakValues().makeMap();
	private final ConcurrentMap<ACPlayer, Boolean> onlinePlayers = new MapMaker()
			.concurrencyLevel(8).makeMap();
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
		ACLogger.info("Converting player to the new type.");
		for (final String name : this.playerFactory.getExistingPlayers()) {
			ACLogger.info("Convert player : " + name);
			DebugLog.INSTANCE.info("Convert player : " + name);
			factory.addExistingPlayer(name);
			final ACPlayer oldPlayer = playerFactory.createPlayer(name);
			final ACPlayer newPlayer = factory.createPlayer(name);
			DebugLog.INSTANCE.info("Convert lastLoc");
			newPlayer.setLastLocation(oldPlayer.getLastLocation());
			DebugLog.INSTANCE.info("Convert presentation");
			newPlayer.setPresentation(oldPlayer.getPresentation());
			DebugLog.INSTANCE.info("Convert homes");
			for (final String home : oldPlayer.getHomeList()) {
				final Location homeLoc = oldPlayer.getHome(home);
				if (homeLoc == null) {
					ACLogger.warning("The home "
							+ home
							+ " of player "
							+ name
							+ " has not been converted because the world is not loaded.");
				} else {
					newPlayer.setHome(home, homeLoc);
				}
			}
			DebugLog.INSTANCE.info("Convert Powers");
			for (final Entry<Type, Object> entry : oldPlayer.getPowers()
					.entrySet()) {
				newPlayer.setPower(entry.getKey(), entry.getValue());
			}
			DebugLog.INSTANCE.info("Convert Custom Powers");
			for (final Entry<String, Object> entry : oldPlayer
					.getCustomPowers().entrySet()) {
				newPlayer.setCustomPower(entry.getKey(), entry.getValue());
			}
			DebugLog.INSTANCE.info("Convert Infos");
			for (final String info : oldPlayer.getInformationsList()) {
				if (info.equals("lastLoc") || info.equals("presentation")) {
					continue;
				}
				newPlayer.setInformation(info, oldPlayer.getInformation(info)
						.getObj());
			}
			DebugLog.INSTANCE.info("Convert Kit");
			for (final String kit : oldPlayer.getKitUseList()) {
				newPlayer.setLastKitUse(kit, oldPlayer.getLastKitUse(kit));
			}
			newPlayer.forceSave();
		}
		this.playerFactory = factory;
		ACLogger.info("Conversion finished.");
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
		final ArrayList<Player> list = new ArrayList<Player>(
				onlinePlayers.size());
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
			final ACPlayer player = demandACPlayer(name);
			if (!(player instanceof EmptyPlayer)) {
				list.add(player);
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

	ACPlayer demandACPlayer(final String name) {
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

	ACPlayer demandACPlayer(final Player player) {
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

}
