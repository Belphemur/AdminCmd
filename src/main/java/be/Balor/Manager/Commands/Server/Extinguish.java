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
package be.Balor.Manager.Commands.Server;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.ACCommands;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Extinguish extends ACCommands {

	/**
	 * 
	 */
	public Extinguish() {
		permNode = "admincmd.server.extinguish";
		cmdName = "bal_extinguish";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, String... args) {
		if (Utils.isPlayer(sender)) {
			int range = 20;
			if (args.length >= 1) {
				try {
					range = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
				}

			}
			Block block = ((Player) sender).getLocation().getBlock();
			int count = 0;
			int limitX = block.getX() + range;
			int limitY = block.getY() + range;
			int limitZ = block.getZ() + range;
			Block current;
			for (int x = block.getX() - range; x <= limitX; x++)
				for (int y = block.getY() - range; y <= limitY; y++)
					for (int z = block.getZ() - range; z <= limitZ; z++) {
						current = block.getWorld().getBlockAt(x, y, z);
						if (current.getType().equals(Material.FIRE)) {
							current.setType(Material.AIR);
							count++;
						}

					}
			Utils.sI18n(sender, "extinguish", "nb", String.valueOf(count));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

}
