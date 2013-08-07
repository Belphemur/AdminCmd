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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/

package be.Balor.Manager.Commands.Player;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author JeterLP
 * 
 */

public class Head extends PlayerCommand {

	/**
	 * 
	 */
	public Head() {
		permNode = "admincmd.player.head";
		cmdName = "bal_head";
		other = false;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (!Users.isPlayer(sender)) {
			return;
		}

		final Player player = (Player) sender;
		String target = args.getString(0);

		if (target == null) {
			return;
		}

		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("%player", target);
		if (addHead(player, target)) {
			LocaleHelper.HEAD_SUCCESSFULL.sendLocale(sender, "player", target);
		} else {
			LocaleHelper.HEAD_ERROR.sendLocale(sender);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args.length == 1;
	}

	/**
	 * Adds a head to the players inventory if its not full.
	 */
	public static boolean addHead(Player player, String skullOwner) {
		PlayerInventory inv = player.getInventory();
		int firstEmpty = inv.firstEmpty();
		if (firstEmpty == -1) {
			return false;
		} else {
			inv.setItem(firstEmpty, getSkull(skullOwner));
			return true;
		}
	}

	/**
	 * Returns a skull with skullOwners skin.
	 * 
	 * @param skullOwner
	 * @return skull
	 */
	public static ItemStack getSkull(String skullOwner) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1,
				(short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(skullOwner);
		skull.setItemMeta(skullMeta);
		return skull;
	}

}
