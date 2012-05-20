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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACVulcanListener implements Listener {
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if ((event.getAction() != Action.LEFT_CLICK_BLOCK)
				&& (event.getAction() != Action.LEFT_CLICK_AIR)) {
			return;
		}
		final ACPlayer player = ACPlayer.getPlayer(event.getPlayer());
		Float power = null;
		if ((power = player.getPower(Type.VULCAN).getFloat(0)) == 0) {
			return;
		}
		player.getHandler()
				.getWorld()
				.createExplosion(player.getHandler().getTargetBlock(null, 600).getLocation(),
						power, true);

	}
}
