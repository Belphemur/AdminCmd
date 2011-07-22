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
package be.Balor.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.Balor.bukkit.AdminCmd.AdminCmdWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACEntityListener extends org.bukkit.event.entity.EntityListener {
	AdminCmdWorker worker;

	/**
	 * 
	 */
	public ACEntityListener(AdminCmdWorker admin) {
		worker = admin;
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();

		if (worker.hasGodPowers(player.getName())) {
			if (event.getCause().equals(DamageCause.FIRE)
					|| event.getCause().equals(DamageCause.FIRE_TICK))
				player.setFireTicks(0);
			event.setCancelled(true);
			event.setDamage(0);
		}

	}
}
