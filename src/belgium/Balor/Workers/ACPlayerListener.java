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
package belgium.Balor.Workers;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.Balor.bukkit.AdminCmd.AdminCmdWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPlayerListener extends PlayerListener {
	AdminCmdWorker worker;

	/**
	 * 
	 */
	public ACPlayerListener(AdminCmdWorker worker) {
		this.worker = worker;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR))) {
			if ((worker.hasThorPowers(event.getPlayer().getName())))
				event.getPlayer().getWorld()
						.strikeLightning(event.getPlayer().getTargetBlock(null, 600).getLocation());
			Float power = null;
			if ((power = worker.getVulcainExplosionPower(event.getPlayer().getName())) != null)
				event.getPlayer()
						.getWorld()
						.createExplosion(event.getPlayer().getTargetBlock(null, 600).getLocation(),
								power, true);
		}
	}

}
