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
package be.Balor.Listeners.Features;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import be.Balor.Tools.Utils;
import be.Balor.Tools.Debug.ACLogger;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACIpCheckListener implements Listener {
	private final ConcurrentMap<String, Player> ips = new MapMaker().makeMap();

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		final InetAddress address = p.getAddress().getAddress();
		final HashMap<String, String> replace = new HashMap<String, String>();
		final Player sameIP = addIP(p, address);
		if (sameIP != null) {
			replace.put("player", Utils.getPlayerName(p));
			replace.put("player2", Utils.getPlayerName(sameIP));
			replace.put("ip", address.toString().substring(1));
			broadcastIP(replace);
		}

	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		removePlayer(event);
	}

	@EventHandler
	public void onKick(final PlayerKickEvent event) {
		removePlayer(event);
	}

	/**
	 * Broadcasts a message if someone joined from the same IP to all players
	 * with a permission and to the console.
	 * 
	 * @param message
	 *            - The message
	 * @param players
	 *            - List of all players with the permission
	 * @author Lathanael (aka Philippe Leipold)
	 */
	private void broadcastIP(final HashMap<String, String> replace) {
		final String message = LocaleHelper.IP_BROADCAST.getLocale(replace);
		if (message == null) {
			return;
		}
		Bukkit.getServer().broadcast(message, "admincmd.spec.ipbroadcast");
		ACLogger.info(message);
	}

	private void removePlayer(final PlayerEvent event) {
		final Player player = event.getPlayer();
		updateIP(player, player.getAddress().getAddress());
	}

	/**
	 * If a player quits loop through the online players and see if a second
	 * player uses the same IP and replace the quitting player with the online
	 * player. Otherwise remove the player from the list!
	 * 
	 * @param quits
	 * @param address
	 */
	private void updateIP(final Player quits, final InetAddress address) {
		ACPluginManager.runTaskLaterAsynchronously(new Runnable() {
			@Override
			public void run() {
				for (final Player p : Utils.getOnlinePlayers()) {
					if (p.equals(quits)) {
						continue;
					}
					if (p.getAddress().getAddress().equals(address)) {
						ips.replace(address.toString().substring(1), p);
						return;
					}
				}
				ips.remove(address.toString().substring(1));

			}
		});

	}

	/**
	 * Adds a player to the ip list if his ip is not already in.
	 * 
	 * @param player
	 * @param address
	 * @return The player who also uses this IP or null if there was none
	 *         previously
	 */
	private Player addIP(final Player player, final InetAddress address) {
		final Player p = ips.putIfAbsent(address.toString().substring(1),
				player);
		return p;
	}

	/**
	 * Checks if an IP is already in use
	 * 
	 * @param address
	 * @return {@code true} if an IP is found, {@code false} otherwise.
	 */
	public boolean ipInUse(final InetAddress address) {
		if (ips.containsKey(address.toString().substring(1))) {
			return true;
		}
		return false;
	}

}
