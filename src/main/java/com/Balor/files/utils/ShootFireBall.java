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
package com.Balor.files.utils;

import net.minecraft.server.EntityFireball;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ShootFireBall implements Runnable {

	CraftPlayer player = null;

	/**
	 * 
	 */
	public ShootFireBall(Player p) {
		player = (CraftPlayer) p;
	}

	public void run() {

		// Define direction of fireball. Multiplying by 10 gives better
		// accuracy.
		Vector lookat = player.getLocation().getDirection().multiply(10);
		WorldServer world = ((CraftWorld) player.getWorld()).getHandle();

		// Define location of the player.
		Location loc = player.getLocation();

		EntityFireball fball = new EntityFireball(world, player.getHandle(), lookat.getX(),
				lookat.getY(), lookat.getZ());

		// Make the fireball spawn slightly out and away from the player.
		fball.locX = loc.getX() + (lookat.getX() / 5.0) + 0.25;
		fball.locY = loc.getY() + (player.getEyeHeight() / 2.0) + 0.5;
		fball.locZ = loc.getZ() + (lookat.getZ() / 5.0);

		world.addEntity(fball);

	}

}
