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
package belgium.Balor.Workers;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.entity.Player;
import org.dynmap.DynmapAPI;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
final public class InvisibleWorker {
	protected static InvisibleWorker instance = null;
	private final ConcurrentMap<Player, Object> invisiblesPlayers = new MapMaker()
			.makeMap();
	private static final Object EMPTY = new Object();
	public static DynmapAPI dynmapAPI;

	/**
	 * 
	 */
	protected InvisibleWorker() {

	}

	/**
	 * @return the instance
	 */
	public static InvisibleWorker getInstance() {
		if (instance == null) {
			instance = new InvisibleWorker();
		}
		return instance;
	}

	public static InvisibleWorker createInstance() {
		if (instance == null) {
			instance = new InvisibleWorker();
		}
		return instance;
	}

	/**
	 * Destroy the instance
	 */
	public static void killInstance() {
		instance = null;
	}

	/**
	 * return all invisible Players
	 * 
	 * @return
	 */
	public Collection<Player> getAllInvisiblePlayers() {
		return Collections.unmodifiableCollection(invisiblesPlayers.keySet());
	}

	/**
	 * Stop the task that try to make the player invisible when he disconnect
	 * 
	 * @param toReappear
	 */
	public void onQuitEvent(final Player toReappear) {
		invisiblesPlayers.remove(toReappear);
	}

	/**
	 * Make the player reappear
	 * 
	 * @param toReappear
	 */
	public void reappear(final Player toReappear) {
		invisiblesPlayers.remove(toReappear);
		if (dynmapAPI != null) {
			dynmapAPI.setPlayerVisiblity(toReappear, true);
		}
		ACPluginManager.getScheduler().scheduleAsyncDelayedTask(
				ACPluginManager.getCorePlugin(), new Runnable() {
					@Override
					public void run() {
						for (final Player p : Utils.getOnlinePlayers()) {
							uninvisible(toReappear, p);
						}
					}
				});
		if (ConfigEnum.FQINVISIBLE.getBoolean()) {
			Utils.broadcastFakeJoin(toReappear);
		}

	}

	/**
	 * Make the player invisible.
	 * 
	 * @param hide
	 * @param hideFrom
	 */
	private void invisible(final Player hide, final Player hideFrom) {
		if (hide == null) {
			return;
		}
		if (hideFrom == null) {
			return;
		}
		if (PermissionManager.hasPerm(hideFrom, "admincmd.invisible.cansee",
				false)) {
			return;
		}
		ACPluginManager.scheduleSyncTask(new Runnable() {

			@Override
			public void run() {
				hideFrom.hidePlayer(hide);
			}
		});

	}

	/**
	 * Make the player unHide
	 * 
	 * @param unHide
	 * @param unHideFrom
	 */
	private void uninvisible(final Player unHide, final Player unHideFrom) {
		if (PermissionManager.hasPerm(unHideFrom, "admincmd.invisible.cansee",
				false)) {
			return;
		}
		ACPluginManager.scheduleSyncTask(new Runnable() {
			@Override
			public void run() {
				unHideFrom.showPlayer(unHide);
			}
		});

	}

	/**
	 * Check if the player is invisible
	 * 
	 * @param player
	 * @return
	 */
	public boolean hasInvisiblePowers(final Player player) {
		if (player == null) {
			return false;
		}
		return invisiblesPlayers.containsKey(player);
	}

	/**
	 * Make the player vanish
	 * 
	 * @param toVanish
	 *            player to vanish
	 * @param onJoinEvent
	 *            if it's done on join event.
	 */
	public void vanish(final Player toVanish, final boolean onJoinEvent) {
		if (!invisiblesPlayers.containsKey(toVanish)) {
			invisiblesPlayers.put(toVanish, EMPTY);
			if (dynmapAPI != null) {
				dynmapAPI.setPlayerVisiblity(toVanish, false);
			}
			ACPluginManager.getScheduler().scheduleAsyncDelayedTask(
					ACPluginManager.getCorePlugin(), new Runnable() {

						@Override
						public void run() {
							for (final Player p : Utils.getOnlinePlayers()) {
								invisible(toVanish, p);
							}

						}
					});
		}
		if (!onJoinEvent && ConfigEnum.FQINVISIBLE.getBoolean()) {
			Utils.broadcastFakeQuit(toVanish);
		}

	}

	/**
	 * return the nb of invisible players
	 * 
	 * @return
	 */
	public int nbInvisibles() {
		return invisiblesPlayers.size();
	}

	/**
	 * Make all invisible player invisible to the new connected player.
	 * 
	 * @param newPlayer
	 *            new connected player.
	 */
	public void makeInvisibleToPlayer(final Player newPlayer) {
		ACPluginManager.getScheduler().scheduleAsyncDelayedTask(
				ACPluginManager.getCorePlugin(), new Runnable() {

					@Override
					public void run() {
						for (final Player inv : invisiblesPlayers.keySet()) {
							invisible(inv, newPlayer);
						}

					}
				});
	}

}
