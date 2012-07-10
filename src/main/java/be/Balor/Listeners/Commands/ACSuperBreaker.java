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
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACSuperBreaker implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onBlockDamage(final BlockDamageEvent event) {
		final ACPlayer player = ACPlayer.getPlayer(event.getPlayer());
		if (!player.hasPower(Type.SUPER_BREAKER)) {
			return;
		}
		final ItemStack itemInHand = event.getItemInHand();
		if (itemInHand != null
				&& itemInHand.getTypeId() == ConfigEnum.SB_ITEM.getInt()) {
			event.setInstaBreak(true);
			itemInHand.setDurability((short) 0);
		}
	}
}
