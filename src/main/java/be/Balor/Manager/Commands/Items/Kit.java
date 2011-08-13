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

import java.util.ArrayList;
import java.util.HashMap;

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
public class Kit extends ACCommand {

	/**
	 * 
	 */
	public Kit() {
		permNode = "admincmd.item.add";
		cmdName = "bal_kit";
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, String... args) {
		// which material?
		Player target;
		if (args.length == 0) {
			Utils.sI18n(sender, "kitList", "list", ACHelper.getInstance().getKitList());
			return;
		}
		ArrayList<ItemStack> items = ACHelper.getInstance().getKit(args[0]);
		if(items==null)
		{
			Utils.sI18n(sender, "kitNotFound", "kit", args[0]);
			return;
		}
		target = Utils.getUser(sender, args, permNode, 1, true);
		if (target == null) {
			return;
		}

		HashMap<String, String> replace = new HashMap<String, String>();
		if (Utils.isPlayer(sender, false)) {
			if (!target.equals(sender)) {
				replace.put("sender", ((Player) sender).getName());
				Utils.sI18n(target, "kitOtherPlayer", replace);
				replace.remove("sender");
				replace.put("target", target.getName());
				Utils.sI18n(sender, "kitCommandSender", replace);
			} else
				Utils.sI18n(sender, "kitYourself", replace);
		} else {
			replace.put("sender", "Server Admin");
			Utils.sI18n(target, "kitOtherPlayer", replace);
			replace.remove("sender");
			replace.put("target", target.getName());
			Utils.sI18n(sender, "kitCommandSender", replace);
		}
		target.getInventory().addItem(items.toArray(new ItemStack[] {}));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

}
