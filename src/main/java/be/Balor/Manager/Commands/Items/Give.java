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
package be.Balor.Manager.Commands.Items;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Balor.bukkit.AdminCmd.AdminCmdWorker;
import com.Balor.files.utils.MaterialContainer;

import be.Balor.Manager.ACCommands;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Give extends ACCommands {

	/**
	 * 
	 */
	public Give() {
		permNode = "admincmd.item.add";
		cmdName = "bal_item";
		other = true;
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
		// which material?
		MaterialContainer mat = null;
		AdminCmdWorker worker = AdminCmdWorker.getInstance();
		mat = worker.checkMaterial(args[0]);
		if (mat.isNull())
			return;
		if (!worker.hasPerm(sender, "admincmd.item.noblacklist")
				&& worker.inBlackList(mat.material.getId())) {
			sender.sendMessage(ChatColor.DARK_RED + "This item (" + ChatColor.WHITE + mat.display()
					+ ChatColor.DARK_RED + ") is black listed.");
			return;
		}
		// amount, damage and target player
		int cnt = 1;
		Player target = null;
		if (args.length >= 2) {
			try {
				cnt = Integer.parseInt(args[1]);
			} catch (Exception e) {
				return;
			}
			if (args.length >= 3) {
				target = AdminCmdWorker.getInstance().getUser(args, permNode, 2);
				if (target == null) {
					return;
				}
			}
		}
		if (target == null) {
			if (AdminCmdWorker.getInstance().isPlayer())
				target = ((Player) sender);
			else
				return;
		}
		ItemStack stack = new ItemStack(mat.material, cnt, mat.dmg);
		if (AdminCmdWorker.getInstance().isPlayer(false)) {
			if (!target.getName().equals(((Player) sender).getName())) {
				target.sendMessage(ChatColor.RED + "[" + ((Player) sender).getName() + "]"
						+ ChatColor.WHITE + " send you " + ChatColor.GOLD + cnt + " "
						+ mat.material);

				sender.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " "
						+ mat.material + " to " + ChatColor.WHITE + target.getName()
						+ "'s inventory");
			} else
				sender.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " "
						+ mat.material + " to " + ChatColor.WHITE + "your inventory");
		} else {
			target.sendMessage(ChatColor.RED + "[Server Admin]" + ChatColor.WHITE + " send you "
					+ ChatColor.GOLD + cnt + " " + mat.material);
			sender.sendMessage(ChatColor.RED + "Added " + ChatColor.GOLD + cnt + " " + mat.material
					+ " to " + ChatColor.WHITE + target.getName() + "'s inventory");
		}
		target.getInventory().addItem(stack);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 1;
	}

}
