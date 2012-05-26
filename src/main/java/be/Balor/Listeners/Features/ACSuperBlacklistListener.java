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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACSuperBlacklistListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onDrop(final PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		final ItemStack itemStack = event.getItemDrop().getItemStack();
		if (!ACHelper.getInstance().inBlackListItem(player, itemStack)) {
			return;
		}
		event.setCancelled(true);
	}

	/**
	 * THANKS LATHANAEL :D idea of Lathanael of using the right click air to
	 * check the item in hand.
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUse(final PlayerInteractEvent event) {
		final Action action = event.getAction();
		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		final Player player = event.getPlayer();
		final ItemStack item = player.getItemInHand();
		if (!ACHelper.getInstance().inBlackListItem(player, item)) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPickup(final PlayerPickupItemEvent event) {
		if (!ACHelper.getInstance().inBlackListItem(event.getPlayer(),
				event.getItem().getItemStack())) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void specialBucket(final PlayerBucketEmptyEvent event) {
		if (!ACHelper.getInstance().inBlackListItem(event.getPlayer(), event.getItemStack())) {
			return;
		}
		event.setCancelled(true);
	}
}
