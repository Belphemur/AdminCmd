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
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.String.Str;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

import com.google.common.base.Joiner;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Enchant extends ItemCommand {
	private final static List<String> enchantList = new ArrayList<String>();
	static {
		for (final Enchantment enchant : Enchantment.values()) {
			enchantList.add(enchant.getName());
		}
	}

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
		final Player target = Utils.getUserParam(sender, args, permNode);

		if (args.length == 0) {
			sender.sendMessage(ChatColor.YELLOW + "Echantment list :");
			sender.sendMessage(ChatColor.GOLD
					+ Joiner.on(", ").skipNulls().join(enchantList)
							.toLowerCase());
			return;
		}
		final String enchantString = args.getString(0);
		int lvl;
		try {
			lvl = args.getInt(1);
		} catch (final NumberFormatException e) {
			lvl = 1;
		}
		final String found = Str.matchString(enchantList, enchantString);
		final HashMap<String, String> replace = new HashMap<String, String>();
		if (found == null) {
			replace.put("value", enchantString);
			replace.put("type", LocaleHelper.TYPE_ENCHANTMENT.getLocale());
			LocaleHelper.DONT_EXISTS.sendLocale(sender, replace);
			sender.sendMessage(ChatColor.YELLOW + "Echantment list :");
			sender.sendMessage(ChatColor.GOLD
					+ Joiner.on(", ").skipNulls().join(enchantList)
							.toLowerCase());
			return;
		}
		final Enchantment enchantment = Enchantment.getByName(found);
		final ItemStack itemInHand = target.getItemInHand();
		if (!enchantment.canEnchantItem(itemInHand)) {
			replace.put("item", itemInHand.getType().toString());
			replace.put("enchant", enchantment.getName());
			LocaleHelper.CANT_ENCHANT.sendLocale(sender, replace);
			return;
		}
		itemInHand.addUnsafeEnchantment(enchantment, lvl);
		replace.put("item", itemInHand.getType().toString());
		replace.put("enchant", enchantment.getName());
		replace.put("lvl", String.valueOf(lvl));
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
