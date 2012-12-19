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
package be.Balor.Manager.Commands.Server;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Help extends ServerCommand {

	/**
	 *
	 */
	public Help() {
		permNode = "admincmd.server.help";
		cmdName = "bal_help";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args)
			throws ActionNotPermitedException, PlayerNotFound {
		if (args.length == 0) {
			HelpLister.getInstance().sendHelpPage("AdminCmd", 1, sender);
			return;
		}
		if (args.getString(0).equalsIgnoreCase("list")
				|| args.getString(0).equalsIgnoreCase("plugins")) {
			String msg = "";
			sender.sendMessage(ChatColor.DARK_AQUA
					+ ACMinecraftFontWidthCalculator
							.strPadCenterChat(ChatColor.WHITE + " Plugins "
									+ ChatColor.DARK_AQUA, '-'));
			for (final String plugin : HelpLister.getInstance().getPluginList()) {
				msg += plugin + ", ";
			}
			if (!msg.equals("")) {
				if (msg.endsWith(", ")) {
					msg = msg.substring(0, msg.lastIndexOf(","));
				}
				sender.sendMessage(msg);
			}
			return;
		}
		final String cmd = args.getValueFlag('s');
		if (cmd != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("type", LocaleHelper.TYPE_CMD.getLocale());
			replace.put("value", cmd);
			if (!HelpLister.getInstance().sendHelpCmd(args.getString(0), cmd,
					sender)) {
				LocaleHelper.DONT_EXISTS.sendLocale(sender, replace);
			}
			return;
		}
		final String cmd2 = args.getValueFlag('d');
		if (cmd2 != null) {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("type", LocaleHelper.TYPE_CMD.getLocale());
			replace.put("value", cmd2);
			if (!HelpLister.getInstance().sendHelpCmd(args.getString(0), cmd2,
					sender, true)) {
				LocaleHelper.DONT_EXISTS.sendLocale(sender, replace);
			}
			return;
		}
		int page = 1;
		try {
			page = args.getInt(0);
			HelpLister.getInstance().sendHelpPage("AdminCmd", page, sender);
		} catch (final NumberFormatException e) {
			if (args.length == 1) {
				if (!HelpLister.getInstance().sendHelpPage(args.getString(0),
						1, sender)) {
					Utils.sI18n(sender, "pluginNotFound", "plugin",
							args.getString(0));
				}
			} else {
				try {
					page = args.getInt(1);
					if (!HelpLister.getInstance().sendHelpPage(
							args.getString(0), page, sender)) {
						Utils.sI18n(sender, "pluginNotFound", "plugin",
								args.getString(0));
					}
				} catch (final NumberFormatException e1) {
					Utils.sI18n(sender, "NaN", "number", args.getString(1));
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null;
	}
}
