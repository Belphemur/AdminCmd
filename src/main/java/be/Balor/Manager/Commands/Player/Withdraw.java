/*************************************************************************
 * Copyright (C) 2012 Philippe Leipold
 *
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
 *
 **************************************************************************/

package be.Balor.Manager.Commands.Player;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class Withdraw extends PlayerCommand {

	public Withdraw() {
		cmdName = "bal_withdraw";
		permNode = "admincmd.player.withdraw";
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
		Player target = null;
		final HashMap<String, String> replace = new HashMap<String, String>();
		if (args.length == 0) {
			if (Utils.isPlayer(sender, false)) {
				final ACPlayer p = ACPlayer.getPlayer((Player) sender);
				if (p == null) {
					return;
				}
				p.removeAllSuperPower();
				LocaleHelper.P_CLEARED.sendLocale(sender);
				return;
			} else {
				replace.put("arg", "-P player");
				replace.put("cmdName", "/withdraw from the console.");
				LocaleHelper.MISSING_ARG.sendLocale(sender, replace);
				return;
			}
		}
		target = Utils.getUserParam(sender, args, permNode);
		if (target == null) {
			return;
		}
		final ACPlayer p = ACPlayer.getPlayer(target);
		if (p == null) {
			return;
		}
		p.removeAllSuperPower();
		replace.put("target", Utils.getPlayerName(target));
		if (Utils.isPlayer(sender, false)) {
			replace.put("sender", Utils.getPlayerName((Player) sender));
		} else {
			replace.put("sender", "Admin");
		}
		LocaleHelper.P_CLEARED_SENDER.sendLocale(sender, replace);
		LocaleHelper.P_CLEARED_TARGET.sendLocale(target, replace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return true;
	}

}
