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

import java.util.LinkedList;
import java.util.concurrent.ConcurrentMap;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.UpdateInvisible;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
final public class InvisibleWorker {
	protected static InvisibleWorker instance = null;
	private ConcurrentMap<String, Integer> invisblesWithTaskIds = new MapMaker().makeMap();
	private long maxRange = 262144;
	private int tickCheck = 400;

	/**
	 * 
	 */
	protected InvisibleWorker() {

	}

	/**
	 * @return the instance
	 */
	public static InvisibleWorker getInstance() {
		if (instance == null)
			instance = new InvisibleWorker();
		return instance;
	}

	public static InvisibleWorker createInstance() {
		if (instance == null)
			instance = new InvisibleWorker();
		return instance;
	}

	/**
	 * Destroy the instance
	 */
	public static void killInstance() {
		instance = null;
	}

	/**
	 * @param maxRange
	 *            the maxRange to set
	 */
	public void setMaxRange(long maxRange) {
		this.maxRange = maxRange ^ 2;
	}

	/**
	 * @param tickCheck
	 *            the tickCheck to set
	 */
	public void setTickCheck(int tickCheck) {
		this.tickCheck = tickCheck * 20;
	}

	/**
	 * return all invisible Players
	 * 
	 * @return
	 */
	public LinkedList<Player> getAllInvisiblePlayers() {
		LinkedList<Player> result = new LinkedList<Player>();
		for (String p : invisblesWithTaskIds.keySet())
			result.add(ACPluginManager.getServer().getPlayerExact(p));
		return result;
	}

	/**
	 * Make the player reappear
	 * 
	 * @param toReappear
	 */
	public void reappear(final Player toReappear) {
		String name = toReappear.getName();
		if (invisblesWithTaskIds.containsKey(name)) {
			ACPluginManager.getServer().getScheduler().cancelTask(invisblesWithTaskIds.get(name));
			invisblesWithTaskIds.remove(name);

			ACPluginManager
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(ACHelper.getInstance().getCoreInstance(),
							new Runnable() {
								@Override
								public void run() {
									for (Player p : Utils.getOnlinePlayers())
										uninvisible(toReappear, p);
								}
							});
			if (ACHelper.getInstance().getConfBoolean("fakeQuitWhenInvisible")) {
				Utils.broadcastFakeJoin(toReappear);
				Utils.addPlayerInOnlineList(toReappear);
			}
		}

	}

	/**
	 * Make the player invisible.
	 * 
	 * @param hide
	 * @param hideFrom
	 */
	public void invisible(Player hide, Player hideFrom) {
		if (hide == null) {
			return;
		}
		if (hideFrom == null) {
			return;
		}
		if (PermissionManager.hasPerm(hideFrom, "admincmd.invisible.cansee", false))
			return;
		if (hide.getName().equals(hideFrom.getName()))
			return;

		if (Utils.getDistanceSquared(hide, hideFrom) > maxRange)
			return;
		EntityPlayer craftFrom = ((CraftPlayer) hideFrom).getHandle();
		craftFrom.netServerHandler.sendPacket(new Packet29DestroyEntity(hide.getEntityId()));

	}

	/**
	 * Make the player unHide
	 * 
	 * @param unHide
	 * @param unHideFrom
	 */
	private void uninvisible(Player unHide, Player unHideFrom) {
		if (unHide.equals(unHideFrom))
			return;

		if (Utils.getDistanceSquared(unHide, unHideFrom) > maxRange)
			return;

		if (PermissionManager.hasPerm(unHideFrom, "admincmd.invisible.cansee", false))
			return;
		EntityPlayer craftFrom = ((CraftPlayer) unHideFrom).getHandle();
		EntityPlayer UnHidePlayer = ((CraftPlayer) unHide).getHandle();
		craftFrom.netServerHandler.sendPacket(new Packet29DestroyEntity(unHide.getEntityId()));
		craftFrom.netServerHandler.sendPacket(new Packet20NamedEntitySpawn(UnHidePlayer));
	}

	/**
	 * Check if the player is invisible
	 * 
	 * @param player
	 * @return
	 */
	public boolean hasInvisiblePowers(String player) {
		return invisblesWithTaskIds.containsKey(player);
	}

	/**
	 * Make the player vanish
	 * 
	 * @param toVanish
	 */
	public void vanish(final Player toVanish) {
		String name = toVanish.getName();
		ACPluginManager
				.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(ACHelper.getInstance().getCoreInstance(),
						new UpdateInvisible(toVanish));
		if (!invisblesWithTaskIds.containsKey(name)) {
			invisblesWithTaskIds.put(
					name,
					ACPluginManager
							.getServer()
							.getScheduler()
							.scheduleAsyncRepeatingTask(ACHelper.getInstance().getCoreInstance(),
									new UpdateInvisible(toVanish), tickCheck / 2, tickCheck));
		}
		if (ACHelper.getInstance().getConfBoolean("fakeQuitWhenInvisible")) {
			Utils.broadcastFakeQuit(toVanish);
			Utils.removePlayerFromOnlineList(toVanish);
		}

	}

	/**
	 * return the nb of invisible players
	 * 
	 * @return
	 */
	public int nbInvisibles() {
		return invisblesWithTaskIds.size();
	}

}
