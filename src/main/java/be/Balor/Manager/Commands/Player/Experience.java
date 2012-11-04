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

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class Experience extends PlayerCommand {

	public Experience() {
		permNode = "admincmd.player.experience";
		cmdName = "bal_exp";
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
		float amount = 0;
		Player target = null;
		final HashMap<String, String> replace = new HashMap<String, String>();
		boolean self = false;
		if (args.hasFlag('p')) {
				target = Utils.getPlayer(args.getValueFlag('p'));
		} else {
			target = (Player) sender;
			self = true;
		}
		if (0 < args.length) {
			if (!args.hasFlag('t')) {
				try {
					amount = args.getFloat(0);
				} catch (final NumberFormatException e) {
					replace.put("number", args.getString(0));
					Utils.I18n("NaN", replace);
					return;
				}
			}
		} else {
			if (Utils.isPlayer(sender, true)) {
				if (args.hasFlag('t')) {
					target = (Player) sender;
					replace.put("exp",
							String.valueOf(target.getTotalExperience()));
					sender.sendMessage(Utils.I18n("expTotal", replace));
					return;
				}
			} else {
				return;
			}
		}
		if (target == null) {
			return;
		}
		replace.put("amount", String.valueOf(amount));
		final Player taskTarget = target;
		final float amountXp = amount;
		if (args.hasFlag('d')) {
			final Location loc = target.getLocation();
			loc.setX(loc.getX() + 2);
			ACPluginManager.scheduleSyncTask(new Runnable() {
				@Override
				public void run() {
					taskTarget.getLocation().getWorld()
							.spawn(loc, ExperienceOrb.class)
							.setExperience((int) amountXp);
				}
			});

			if (self) {
				target.sendMessage(Utils.I18n("expDropped", replace));
			} else {
				replace.put("target", Utils.getPlayerName(target));
				target.sendMessage(Utils.I18n("expDropped", replace));
				sender.sendMessage(Utils.I18n("expDroppedTarget", replace));
			}
		} else if (args.hasFlag('a')) {

			ACPluginManager.scheduleSyncTask(new Runnable() {
				@Override
				public void run() {
					taskTarget.giveExp((int) amountXp);
				}
			});
			if (self) {
				target.sendMessage(Utils.I18n("expAdded", replace));
			} else {
				replace.put("target", Utils.getPlayerName(target));
				sender.sendMessage(Utils.I18n("expAddedTarget", replace));
				target.sendMessage(Utils.I18n("expAdded", replace));
			}
		} else if (args.hasFlag('b')) {
			final float exp = (amount > 1 ? 1 : amount);
			ACPluginManager.scheduleSyncTask(new Runnable() {
				@Override
				public void run() {
					taskTarget.setExp(exp);
				}
			});
			replace.put("amount", String.valueOf(exp * 100.0F));
			if (self) {
				target.sendMessage(Utils.I18n("expProgressionSet", replace));
			} else {
				replace.put("target", Utils.getPlayerName(target));
				target.sendMessage(Utils.I18n("expProgressionSet", replace));
				sender.sendMessage(Utils.I18n("expProgressionSetTarget",
						replace));
			}
		} else if (args.hasFlag('l')) {
			ACPluginManager.scheduleSyncTask(new Runnable() {
				@Override
				public void run() {
					taskTarget.setLevel((int) amountXp);
				}
			});
			if (self) {
				target.sendMessage(Utils.I18n("expLevelSet", replace));
			} else {
				replace.put("target", Utils.getPlayerName(target));
				target.sendMessage(Utils.I18n("expLevelSet", replace));
				sender.sendMessage(Utils.I18n("expLevelSetTarget", replace));
			}
		} else if (args.hasFlag('t')) {
			replace.put("exp", String.valueOf(target.getTotalExperience()));
			if (self) {
				sender.sendMessage(Utils.I18n("expTotal", replace));
			} else {
				replace.put("target", Utils.getPlayerName(target));
				sender.sendMessage(Utils.I18n("expTotalTarget", replace));
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

}
