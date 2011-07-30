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
package be.Balor.Tools;

import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ShootFireball {
	public static void shoot(Player player, Float yield) {
		Location playerLoc = player.getLocation();
		Location fbLocation = playerLoc.add(
				playerLoc.getDirection().normalize().multiply(2)
						.toLocation(player.getWorld(), playerLoc.getYaw(), playerLoc.getPitch()))
				.add(0, 1D, 0);
		Fireball f = player.getWorld().spawn(fbLocation, Fireball.class);
		f.setYield(yield);
		f.setShooter(player);
	}
}
