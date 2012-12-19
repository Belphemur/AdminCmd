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
package be.Balor.Listeners.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACTeleportEvent extends PlayerTeleportEvent {

	/**
	 * @param player
	 * @param from
	 * @param to
	 */
	public ACTeleportEvent(final Player player, final Location from,
			final Location to) {
		super(player, from, to);
	}

	/**
	 * @param player
	 * @param from
	 * @param to
	 * @param cause
	 */
	public ACTeleportEvent(final Player player, final Location from,
			final Location to, final TeleportCause cause) {
		super(player, from, to, cause);
	}

}
