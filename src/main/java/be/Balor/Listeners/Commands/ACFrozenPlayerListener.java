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

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACFrozenPlayerListener implements Listener {
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onMove(final PlayerMoveEvent event) {
		if (!ACPlayer.getPlayer(event.getPlayer()).hasPower(Type.FROZEN)) {
			return;
		}
		event.setCancelled(true);

	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onDrop(final PlayerDropItemEvent event) {
		if (ACPlayer.getPlayer(event.getPlayer()).hasPower(Type.FROZEN)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onPickup(final PlayerPickupItemEvent event) {
		if (ACPlayer.getPlayer(event.getPlayer()).hasPower(Type.FROZEN)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onInteract(final PlayerInteractEvent event) {
		if (ACPlayer.getPlayer(event.getPlayer()).hasPower(Type.FROZEN)) {
			event.setCancelled(true);
		}
	}
}
