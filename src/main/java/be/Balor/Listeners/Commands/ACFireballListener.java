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

import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
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
public class ACFireballListener implements Listener {
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if ((event.getAction() != Action.LEFT_CLICK_BLOCK)
				&& (event.getAction() != Action.LEFT_CLICK_AIR)) {
			return;
		}
		final Player player = event.getPlayer();
		final ACPlayer acPlayer = ACPlayer.getPlayer(player);
		Float power;
		if ((power = acPlayer.getPower(Type.FIREBALL).getFloat(0)) == 0) {
			return;
		}
		final Location playerLoc = player.getLocation();
		final Location fbLocation = playerLoc.add(
				playerLoc
						.getDirection()
						.normalize()
						.multiply(2)
						.toLocation(player.getWorld(), playerLoc.getYaw(),
								playerLoc.getPitch())).add(0, 1D, 0);
		final Fireball f = player.getWorld().spawn(fbLocation, Fireball.class);
		f.setYield(power);
		f.setShooter(player);

	}
}
