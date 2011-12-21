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
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Tools.Utils;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class Experience extends CoreCommand {

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
	public void execute(CommandSender sender, CommandArgs args) {
		float amount = 0;
		Player target = null;
		boolean self = false;
		if (args.length < 2) {
			if (Utils.isPlayer(sender, true)) {
				target = (Player) sender;
				self = true;
				if (!args.hasFlag('t'))
					try {
						amount = args.getFloat(0);
					} catch (NumberFormatException e) {
						HashMap<String, String> replace = new HashMap<String, String>();
						replace.put("number", args.getString(0));
						Utils.I18n("NaN", replace);
						return;
					}
			} else
				return;
		} else {
			target = Utils.getPlayer(args.getString(0));
			if (!args.hasFlag('t'))
				try {
					amount = args.getFloat(1);
				} catch (NumberFormatException e) {
					HashMap<String, String> replace = new HashMap<String, String>();
					replace.put("number", args.getString(0));
					Utils.I18n("NaN", replace);
					return;
				}
		}
		if (target == null)
			return;
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("amount", String.valueOf(amount));
		if (args.hasFlag('d')) {
			Location loc = target.getLocation();
			loc.setX(loc.getX() +2);
			target.getLocation().getWorld().spawn(loc, ExperienceOrb.class).setExperience((int)amount);
			if (self) {
				target.sendMessage(Utils.I18n("expDropped", replace));
			} else {
				replace.put("target", Utils.getPlayerName(target));
				target.sendMessage(Utils.I18n("expDropped", replace));
				sender.sendMessage(Utils.I18n("expDroppedTarget", replace));
			}
		} else if (args.hasFlag('a')) {
			target.giveExp((int)amount);
			if (self) {
				target.sendMessage(Utils.I18n("expAdded", replace));
			} else {
				replace.put("target", Utils.getPlayerName(target));
				sender.sendMessage(Utils.I18n("expAddedTarget", replace));
				target.sendMessage(Utils.I18n("expAdded", replace));
			}
		} else if (args.hasFlag('p')) {
			float exp = (amount > 1 ? 1: amount);
			target.setExp(exp);
			replace.put("amount", String.valueOf(exp*100.0F));
			if (self) {
				target.sendMessage(Utils.I18n("expProgressionSet", replace));
			} else {
				replace.put("target", Utils.getPlayerName(target));
				target.sendMessage(Utils.I18n("expProgressionSet", replace));
				sender.sendMessage(Utils.I18n("expProgressionSetTarget", replace));
			}
		} else if (args.hasFlag('l')) {
			target.setLevel((int)amount);
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
	public boolean argsCheck(String... args) {
		return args.length >= 1;
	}

}
