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
	public void execute(final CommandSender sender, final CommandArgs args) throws ActionNotPermitedException, PlayerNotFound {
		if (!Users.isPlayer(sender)) {
			return;
		}

		final Player player = (Player) sender;
		final String target = args.getString(0);

		if (target == null) {
			return;
		}

		if (setHead(player, target)) {
			LocaleHelper.HEAD_SUCCESSFULL.sendLocale(sender, "player", target);
		} else {
			LocaleHelper.ERROR_HEAD_INV_FULL.sendLocale(sender);
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
	 * 
	 * @param player
	 *            player to change head
	 * @param skullOwner
	 *            player to take head off
	 * @return
	 */
	public boolean setHead(final Player player, final String skullOwner) {
		final PlayerInventory inv = player.getInventory();
		final ItemStack helmet = inv.getHelmet();
		final ItemStack skull = getSkull(skullOwner);
		if (helmet == null || (helmet != null && helmet.getType() == Material.AIR)) {
			inv.setHelmet(skull);
			return true;
		}
		return inv.addItem(skull).isEmpty();
	}

	/**
	 * Returns a skull with skullOwners skin.
	 * 
	 * @param skullOwner
	 * @return skull
	 */
	public ItemStack getSkull(final String skullOwner) {
		final ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		final SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(skullOwner);
		skull.setItemMeta(skullMeta);
		return skull;
	}

}
