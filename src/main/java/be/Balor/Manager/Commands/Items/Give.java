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

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Give extends ItemCommand {

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
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		// which material?
		MaterialContainer mat = null;
		mat = ACHelper.getInstance().checkMaterial(sender, args.getString(0));
		if (mat.isNull()) {
			return;
		}
		if (ACHelper.getInstance().inBlackListItem(sender, mat)) {
			return;
		}
		if (mat.getMaterial().equals(Material.AIR)) {
			Utils.sI18n(sender, "airForbidden");
			return;
		}
		// amount, damage and target player
		int cnt = 1;
		Player target = null;
		if (args.length >= 2) {

			try {
				cnt = args.getInt(1);
			} catch (final Exception e) {
				return;
			}
			if (cnt > ACHelper.getInstance().getLimit(sender,
					Type.Limit.MAX_ITEMS)
					&& !(sender.hasPermission("admincmd.item.infinity"))) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put(
						"limit",
						String.valueOf(ACHelper.getInstance().getLimit(sender,
								Type.Limit.MAX_ITEMS)));
				Utils.sI18n(sender, "itemLimit", replace);
				return;
			}
			if (args.length >= 3) {
				target = Utils.getUser(sender, args, permNode, 2, true);
				if (target == null) {
					return;
				}
			}
		}
		if (target == null) {
			if (Utils.isPlayer(sender)) {
				target = ((Player) sender);
			} else {
				return;
			}
		}
		mat.setAmount(cnt);
		final ItemStack stack = mat.getItemStack();
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("amount", String.valueOf(mat.getAmount()));
		replace.put("material", mat.getMaterial().toString());
		if (Utils.isPlayer(sender, false)) {
			if (!target.equals(sender)) {
				replace.put("sender", Utils.getPlayerName((Player) sender));
				Utils.sI18n(target, "giveItemOtherPlayer", replace);
				replace.remove("sender");
				replace.put("target", Utils.getPlayerName(target));
				Utils.sI18n(sender, "giveItemCommandSender", replace);
			} else {
				Utils.sI18n(sender, "giveItemYourself", replace);
			}
		} else {
			replace.put("sender", "Server Admin");
			Utils.sI18n(target, "giveItemOtherPlayer", replace);
			replace.remove("sender");
			replace.put("target", Utils.getPlayerName(target));
			Utils.sI18n(sender, "giveItemCommandSender", replace);
		}
		final Player taskTarget = target;
		ACPluginManager.scheduleSyncTask(new Runnable() {
			@Override
			public void run() {
				taskTarget.getInventory().addItem(stack);
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

}
