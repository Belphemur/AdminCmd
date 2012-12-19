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
package be.Balor.Manager.Commands.Player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ClearInventory extends PlayerCommand {

	/**
	 *
	 */
	public ClearInventory() {
		permNode = "admincmd.player.clear";
		cmdName = "bal_pclear";
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
			throws PlayerNotFound, ActionNotPermitedException {
		final Player target = Utils.getUserParam(sender, args, permNode);
		if (target == null) {
			return;
		}
		if (args.length == 1) {
			final MaterialContainer mc = ACHelper.getInstance().checkMaterial(
					sender, args.getString(0));
			if (mc.isNull()) {
				return;
			}
			ACPluginManager.scheduleSyncTask(new Runnable() {

				@Override
				public void run() {
					target.getInventory().remove(mc.getMaterial());

				}
			});

		} else if (args.length >= 2) {
			final HashMap<Integer, ? extends ItemStack> stacks;
			final int startAmount = args.getInt(1);

			final MaterialContainer mc = ACHelper.getInstance().checkMaterial(
					sender, args.getString(0));
			if (mc.isNull()) {
				return;
			}
			stacks = target.getInventory().all(mc.getMaterial());
			ACPluginManager.scheduleSyncTask(new Runnable() {

				@Override
				public void run() {
					int current = 0;
					int tempSlot = 0;
					int amount = startAmount;
					ItemStack tempStack;
					for (final Map.Entry<Integer, ? extends ItemStack> stacksEntry : stacks
							.entrySet()) {
						current = stacksEntry.getValue().getAmount();
						tempSlot = stacksEntry.getKey();
						if (amount < current) {
							tempStack = target.getInventory().getItem(tempSlot);
							tempStack.setAmount(current - amount);
							break;
						} else if (amount == current) {
							target.getInventory().clear(tempSlot);
							break;
						} else {
							target.getInventory().clear(tempSlot);
							amount = amount - current;
						}
					}
				}
			});
		} else {
			ACPluginManager.scheduleSyncTask(new Runnable() {

				@Override
				public void run() {
					target.getInventory().clear();
					target.getInventory().setHelmet(null);
					target.getInventory().setChestplate(null);
					target.getInventory().setLeggings(null);
					target.getInventory().setBoots(null);
				}
			});
		}
		if (!sender.equals(target)) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", Utils.getPlayerName(target));
			Utils.sI18n(sender, "clearTarget", replace);
		}
		Utils.sI18n(target, "clear");

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
