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
import be.Balor.Manager.Exceptions.CantEnchantItemException;
import be.Balor.Manager.Exceptions.EnchantmentConflictException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.MaterialContainer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.ACPluginManager;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Give extends ItemCommand {

	public static class GiveData {
		private final MaterialContainer mat;
		private final Player target;

		/**
		 * @param mat
		 * @param target
		 */
		public GiveData(final MaterialContainer mat, final Player target) {
			super();
			this.mat = mat;
			this.target = target;
		}

		/**
		 * @return the mat
		 */
		public MaterialContainer getMat() {
			return mat;
		}

		/**
		 * @return the target
		 */
		public Player getTarget() {
			return target;
		}

	}

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
		final GiveData data = getGiveData(sender, args, permNode);
		if (data == null) {
			return;
		}
		final MaterialContainer mat = data.getMat();
		final Player target = data.getTarget();

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

	/**
	 * Set enchantments given in the args on the given MaterialContainer.
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            args of the command
	 * @param mat
	 *            to which material add the enchantments
	 * @param startIndex
	 *            where to start to parse the args
	 */
	public static void setEnchantements(final CommandSender sender,
			final CommandArgs args, final MaterialContainer mat,
			final int startIndex) {
		final HashMap<String, String> replace = new HashMap<String, String>();
		for (int i = startIndex; i < args.length; i++) {
			final String enchant = args.getString(i);
			try {

				if (!mat.addEnchantment(enchant)) {
					replace.clear();
					replace.put("enchant", enchant);
					LocaleHelper.ENCHANT_EXIST.sendLocale(sender, replace);
				}
			} catch (final EnchantmentConflictException e) {
				replace.clear();
				replace.put("e1", e.getTriedEnchant().getName());
				replace.put("e2", e.getConflictEnchant().getName());
				LocaleHelper.ENCHANT_CONFLICT.sendLocale(sender, replace);
			} catch (final CantEnchantItemException e) {
				replace.clear();
				replace.put("enchant", e.getEnchant().getName());
				replace.put("item", e.getMaterial().name());
				LocaleHelper.CANT_ENCHANT.sendLocale(sender, replace);
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
		return args != null && args.length >= 1;
	}

	/**
	 * Parse all argument, check all condition and create the item that will be
	 * given to the player
	 * 
	 * @param sender
	 *            sender of the command
	 * @param args
	 *            arguments of the command
	 * @param permNode
	 *            permission needed by the command
	 * @return a {@link GiveData} containing the {@link Player} and the
	 *         {@link MaterialContainer}
	 * @throws ActionNotPermitedException
	 * @throws PlayerNotFound
	 */
	public static GiveData getGiveData(final CommandSender sender,
			final CommandArgs args, final String permNode)
			throws ActionNotPermitedException, PlayerNotFound {
		// which material?
		MaterialContainer mat = null;
		final String color = args.getValueFlag('c');
		mat = ACHelper.getInstance().checkMaterial(sender, args.getString(0));
		if (mat.isNull()) {
			return null;
		}
		if (ACHelper.getInstance().inBlackListItem(sender, mat)) {
			return null;
		}
		if (mat.getMaterial().equals(Material.AIR)) {
			Utils.sI18n(sender, "airForbidden");
			return null;
		}
		if (color != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();

			try {
				if (!mat.setColor(color)) {
					replace.put("color", color);
					replace.put("colors", MaterialContainer.possibleColors());
					LocaleHelper.COLOR_D_EXISTS.sendLocale(sender, replace);
					return null;
				}
			} catch (final IllegalArgumentException e) {
				replace.put("item", mat.getMaterial().toString());
				replace.put("items", MaterialContainer.possibleColoredItems());
				LocaleHelper.CANT_COLOR.sendLocale(sender, replace);
				return null;
			}
		}
		// amount, damage and target player
		int cnt = 1;
		Player target = null;

		if (args.length >= 2) {

			try {
				cnt = args.getInt(1);
			} catch (final Exception e) {
				cnt = 1;
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
				return null;
			}
			if (args.length >= 3) {
				target = Utils.getUser(sender, args, permNode, 2, true);
				if (target == null) {
					return null;
				}
				if (args.length >= 4) {
					setEnchantements(sender, args, mat, 3);
				}
			}
		}
		if (target == null) {
			if (Utils.isPlayer(sender)) {
				target = ((Player) sender);
			} else {
				return null;
			}
		}
		mat.setAmount(cnt);
		return new GiveData(mat, target);
	}
}
