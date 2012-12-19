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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACTpAtSeeListener implements Listener {
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if ((event.getAction() != Action.LEFT_CLICK_BLOCK)
				&& (event.getAction() != Action.LEFT_CLICK_AIR)) {
			return;
		}
		final ACPlayer player = ACPlayer.getPlayer(event.getPlayer());
		if (!player.hasPower(Type.TP_AT_SEE)) {
			return;
		}
		try {
			final Player p = player.getHandler();
			final Block toTp = p.getWorld().getBlockAt(
					p.getTargetBlock(null, ConfigEnum.RTPSEE.getInt())
							.getLocation().add(0, 1, 0));
			if (toTp.getTypeId() == 0) {
				final Location loc = toTp.getLocation().clone();
				loc.setPitch(p.getLocation().getPitch());
				loc.setYaw(p.getLocation().getYaw());
				Utils.teleportWithChunkCheck(p, loc);
			}
		} catch (final Exception e) {}
	}
}
