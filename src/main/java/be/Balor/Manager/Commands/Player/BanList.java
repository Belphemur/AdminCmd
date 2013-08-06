/*************************************************************************
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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.ActionNotPermitedException;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Tools.Lister.EmptyListException;
import be.Balor.Tools.Lister.Lister;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class BanList extends PlayerCommand {

	public BanList() {
		cmdName = "bal_banlist";
		permNode = "admincmd.player.banlist";
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
		int page = 1;
		if (args.length == 1) {
			try {
				page = args.getInt(0);
			} catch (NumberFormatException e) {
				String msg = LocaleManager.I18n("NaN", "number", args.getString(0));
				sender.sendMessage(msg + ChatColor.RED
						+ " Used default page 1!");
				page = 1;
			}
		}

		try {
			for (final String s : Lister.getLister(Lister.List.BAN).getPage(
					page)) {
				sender.sendMessage(s);
			}
		} catch (final EmptyListException e) {
			LocaleHelper.NO_BANNED.sendLocale(sender);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}

}
