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
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class More extends ItemCommand {

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
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (Utils.isPlayer(sender)) {
			final ItemStack hand = ((Player) sender).getItemInHand();
			if (hand == null || hand.getType() == Material.AIR) {
				Utils.sI18n(sender, "errorHolding");
				return;
			}
			if (ACHelper.getInstance().inBlackListItem(sender, hand)) {
				return;
			}
			if (args.length == 0) {
				ACPluginManager.scheduleSyncTask(new HandSetAmount(hand, 64));
			} else {
				int toAdd;
				try {
					toAdd = Integer.parseInt(args.getString(0));
				} catch (final Exception e) {
					return;
				}
				if ((hand.getAmount() + toAdd) > hand.getMaxStackSize()) {
					final int inInventory = (hand.getAmount() + toAdd)
							- hand.getMaxStackSize();
					ACPluginManager.scheduleSyncTask(new HandSetAmount(hand,
							hand.getMaxStackSize()));
					((Player) sender).getInventory().addItem(
							new ItemStack(hand.getType(), inInventory, hand
									.getDurability()));
					final HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("amount", String.valueOf(inInventory));
					Utils.sI18n(sender, "moreTooMuch", replace);

				} else {
					ACPluginManager.scheduleSyncTask(new HandSetAmount(hand,
							hand.getAmount() + toAdd));
				}
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

	private class HandSetAmount implements Runnable {
		private final ItemStack hand;
		private final int amount;

		/**
		 * @param hand
		 * @param amount
		 */
		public HandSetAmount(final ItemStack hand, final int amount) {
			super();
			this.hand = hand;
			this.amount = amount;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			hand.setAmount(amount);
		}

	}

}
