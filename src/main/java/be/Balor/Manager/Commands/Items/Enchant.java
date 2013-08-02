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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Enchant extends ItemCommand {

	/**
	 * 
	 */
	public Enchant() {
		permNode = "admincmd.item.enchant";
		cmdName = "bal_enchant";
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws PlayerNotFound, ActionNotPermitedException {
		Player target;
		try {
			target = Users.getUser(sender, args, permNode);
		} catch (final PlayerNotFound e) {
			target = Users.getUserParam(sender, args, permNode);
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.YELLOW + "Echantment list :");
			sender.sendMessage(ChatColor.GOLD
					+ MaterialContainer.possibleEnchantment());
			return;
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		final MaterialContainer inHand = new MaterialContainer(
				target.getItemInHand());
		Give.setEnchantements(sender, args, inHand, target.equals(sender) ? 0
				: 1);
		final Player finalTarget = target;
		ACPluginManager.scheduleSyncTask(new Runnable() {

			@Override
			public void run() {
				finalTarget.setItemInHand(inHand.getItemStack());

			}
		});
		replace.put("item", target.getItemInHand().getType().name());
		LocaleHelper.SUCCESS_ENCHANT.sendLocale(sender, replace);
		if (!sender.equals(target)) {
			LocaleHelper.SUCCESS_ENCHANT.sendLocale(target, replace);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
