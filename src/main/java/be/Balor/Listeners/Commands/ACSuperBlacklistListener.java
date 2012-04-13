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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACSuperBlacklistListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onDrop(final PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		final ItemStack itemStack = event.getItemDrop().getItemStack();
		if (!ACHelper.getInstance().inBlackListItem(player, itemStack))
			return;
		event.setCancelled(true);
		player.getInventory().addItem(itemStack);
	}

	@EventHandler(ignoreCancelled = true)
	public void onUse(final PlayerInteractEvent event) {
		if (event.getItem() == null)
			return;
		if (!ACHelper.getInstance().inBlackListItem(event.getPlayer(), event.getItem()))
			return;
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPickup(final PlayerPickupItemEvent event) {
		if (!ACHelper.getInstance().inBlackListItem(event.getPlayer(),
				event.getItem().getItemStack()))
			return;
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void specialBucket(final PlayerBucketEmptyEvent event) {
		if (!ACHelper.getInstance().inBlackListItem(event.getPlayer(), event.getItemStack()))
			return;
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void specialEgg(final PlayerEggThrowEvent event) {
		final ItemStack egg = new ItemStack(Material.EGG, 1, event.getHatchingType().getTypeId());
		final Player player = event.getPlayer();
		if (!ACHelper.getInstance().inBlackListItem(player, egg))
			return;
		event.setHatching(false);
		player.getInventory().addItem(egg);

	}
}
