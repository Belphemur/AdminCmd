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

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import be.Balor.Manager.ACCommand;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class More extends ACCommand {

	/**
	 * 
	 */
	public More() {
		permNode = "admincmd.item.more";
		cmdName = "bal_itemmore";
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
			ItemStack hand = ((Player) sender).getItemInHand();
			if (hand == null || hand.getType() == Material.AIR) {
				Utils.sI18n(sender, "errorHolding");
				return;
			}
			if (ACHelper.getInstance().inBlackList(sender, hand))
				return;
			if (args.length == 0)
				hand.setAmount(64);
			else {
				int toAdd;
				try {
					toAdd = Integer.parseInt(args[0]);
				} catch (Exception e) {
					return;
				}
				if ((hand.getAmount() + toAdd) > hand.getMaxStackSize()) {
					int inInventory = (hand.getAmount() + toAdd) - hand.getMaxStackSize();
					hand.setAmount(hand.getMaxStackSize());
					((Player) sender).getInventory().addItem(
							new ItemStack(hand.getType(), inInventory));
					HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("amount", String.valueOf(inInventory));
					Utils.sI18n(sender, "moreTooMuch", replace);

				} else
					hand.setAmount(hand.getAmount() + toAdd);
			}
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
