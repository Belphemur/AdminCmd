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

import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import be.Balor.Tools.Debug.ACLogger;
import be.Balor.World.ACWorld;
import be.Balor.bukkit.AdminCmd.ConfigEnum;

/**
 * @author Antoine
 * 
 */
public class ACRespawnWorldFeature implements Listener {

	/**
	 * 
	 */
	public ACRespawnWorldFeature() {
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onRespawn(final PlayerRespawnEvent event) {
		final Environment worldEnv = event.getPlayer().getWorld()
				.getEnvironment();

		if (worldEnv.equals(Environment.NORMAL)) {
			return;
		}

		try {
			event.setRespawnLocation(ACWorld.getWorld(
					ConfigEnum.RESPAWN_WORLD.getString()).getSpawn());
		} catch (final Exception e) {
			ACLogger.warning("The respawn.changeDestination is set to true, but the world "
					+ ConfigEnum.RESPAWN_WORLD.getString() + " don't exists.");
		}
	}

}
