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
package be.Balor.World.OpenInv;

import net.minecraft.server.ContainerPlayer;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PlayerInventory;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPlayerInventory extends PlayerInventory {

	/**
	 * @param entityhuman
	 */
	public ACPlayerInventory(final EntityHuman entityhuman) {
		super(entityhuman);
	}

	@Override
	public boolean a(final EntityHuman entityhuman) {
		return this.player.dead ? false : true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.minecraft.server.PlayerInventory#onClose(org.bukkit.craftbukkit.entity
	 * .CraftHumanEntity)
	 */
	@Override
	public void onClose(final CraftHumanEntity who) {
		super.onClose(who);
		if (who.getHandle().equals(player)) {
			return;
		}
		final EntityPlayer player = (EntityPlayer) this.player;
		player.updateInventory(new ContainerPlayer(this));
	}

}
