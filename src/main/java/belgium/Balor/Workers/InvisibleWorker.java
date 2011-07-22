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

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.Balor.bukkit.AdminCmd.AdminCmd;
import com.Balor.bukkit.AdminCmd.ACHelper;
import com.Balor.files.utils.UpdateInvisible;
import com.Balor.files.utils.Utils;
import com.google.common.collect.MapMaker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class InvisibleWorker {
	protected static InvisibleWorker instance = null;
	private ConcurrentMap<String, Integer> invisblesWithTaskIds = new MapMaker().makeMap();
	private long maxRange = 16384;

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

	/**
	 * @param maxRange
	 *            the maxRange to set
	 */
	public void setMaxRange(long maxRange) {
		this.maxRange = maxRange;
	}

	/**
	 * return all invisible Players
	 * 
	 * @return
	 */
	public LinkedList<Player> getAllInvisiblePlayers() {
		LinkedList<Player> result = new LinkedList<Player>();
		for (String p : invisblesWithTaskIds.keySet())
			result.add(AdminCmd.getBukkitServer().getPlayer(p));
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
			AdminCmd.getBukkitServer().getScheduler().cancelTask(invisblesWithTaskIds.get(name));
			invisblesWithTaskIds.remove(name);
		}
		AdminCmd.getBukkitServer()
				.getScheduler()
				.scheduleAsyncDelayedTask(ACHelper.getInstance().getPluginInstance(),
						new Runnable() {
							public void run() {
								for (Player p : AdminCmd.getBukkitServer().getOnlinePlayers())
									uninvisible(toReappear, p);
							}
						});

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
		if (hide.getName().equals(hideFrom.getName()))
			return;

		if (Utils.getDistanceSquared(hide, hideFrom) > maxRange)
			return;

		((CraftPlayer) hideFrom).getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(
				hide.getEntityId()));
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

		((CraftPlayer) unHideFrom).getHandle().netServerHandler
				.sendPacket(new Packet29DestroyEntity(unHide.getEntityId()));
		((CraftPlayer) unHideFrom).getHandle().netServerHandler
				.sendPacket(new Packet20NamedEntitySpawn(((CraftPlayer) unHide).getHandle()));
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
		AdminCmd.getBukkitServer()
				.getScheduler()
				.scheduleAsyncDelayedTask(ACHelper.getInstance().getPluginInstance(),
						new UpdateInvisible(toVanish));
		if (!invisblesWithTaskIds.containsKey(name))
			invisblesWithTaskIds.put(
					name,
					(Integer) AdminCmd
							.getBukkitServer()
							.getScheduler()
							.scheduleAsyncRepeatingTask(
									ACHelper.getInstance().getPluginInstance(),
									new UpdateInvisible(toVanish), 200, 400));

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
