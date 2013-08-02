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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.CommandUtils.Users;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Repair extends ItemCommand {
	/**
	 *
	 */
	public Repair() {
		permNode = "admincmd.item.repair";
		cmdName = "bal_repair";
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
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (Users.isPlayer(sender)) {
			final Player player = Users.getUser(sender, args, permNode);
			if (player == null) {
				return;
			}
			final ItemStack item = player.getItemInHand();
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("type", item.getType().toString());
			if (item != null
					&& ACHelper.getInstance().repairable(item.getTypeId())) {
				ACPluginManager.scheduleSyncTask(new Runnable() {
					@Override
					public void run() {
						item.setDurability((short) 0);
					}
				});
				replace.put("player", Users.getPlayerName(player));
				if (!sender.equals(player)) {
					LocaleManager.sI18n(sender, "repair", replace);
				}
				LocaleManager.sI18n(player, "repairTarget", replace);
			} else {
				LocaleManager.sI18n(sender, "errorRepair", replace);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
