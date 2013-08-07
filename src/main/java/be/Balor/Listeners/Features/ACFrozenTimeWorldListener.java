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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import be.Balor.Tools.Type;
import be.Balor.World.ACWorld;

/**
 * @author Antoine
 * 
 */
public class ACFrozenTimeWorldListener implements Listener {

	/**
	 * 
	 */
	public ACFrozenTimeWorldListener() {
		// TODO Auto-generated constructor stub
	}

	@EventHandler
	void onPlayerWorldChange(final PlayerChangedWorldEvent event) {
		setPlayerTime(event.getPlayer());

	}

	/**
	 * @param player
	 * @param newWorld
	 */
	private void setPlayerTime(final Player player) {
		final ACWorld acToWorld = ACWorld.getWorld(player.getLocation()
				.getWorld());
		if (acToWorld.hasInformation(Type.TIME_FROZEN)) {
			player.setPlayerTime(acToWorld.getInformation(Type.TIME_FROZEN)
					.getLong(0), false);
		} else {
			player.setPlayerTime(0, true);
		}
	}
}
