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
package be.Balor.Listeners.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACNoDropListener implements Listener {
	private final Map<String, List<ItemStack>> itemsDrops = new HashMap<String, List<ItemStack>>();

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.isCancelled())
			return;
		if (ACPlayer.getPlayer(event.getPlayer()).hasPower(Type.NO_DROP))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player p = (Player) event.getEntity();
		ACPlayer player = ACPlayer.getPlayer(p);
		if (!player.hasPower(Type.NO_DROP))
			return;
		final List<ItemStack> items = new ArrayList<ItemStack>();
		for (ItemStack item : event.getDrops()) {
			items.add(item.clone());
			item.setAmount(0);
		}
		itemsDrops.put(p.getName(), items);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		ACPlayer player = ACPlayer.getPlayer(p);
		if (!player.hasPower(Type.NO_DROP))
			return;
		List<ItemStack> items = itemsDrops.get(p.getName());
		if (items == null)
			return;
		p.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
		itemsDrops.remove(p.getName());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onQuit(PlayerQuitEvent event) {
		itemsDrops.remove(event.getPlayer().getName());
	}
}
