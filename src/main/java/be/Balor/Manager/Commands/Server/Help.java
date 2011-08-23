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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.ACCommand;
import be.Balor.Tools.Utils;
import be.Balor.Tools.Help.HelpLister;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Help extends ACCommand {

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
	public void execute(CommandSender sender, String... args) {
		if (args.length == 0) {
			HelpLister.getInstance().sendHelpPage("AdminCmd", 1, sender);
			return;
		}
		if (args[0].equals("list")) {
			String msg = "";
			sender.sendMessage(ChatColor.DARK_AQUA
					+ ACMinecraftFontWidthCalculator.strPadCenterChat(ChatColor.WHITE + " Plugins "
							+ ChatColor.DARK_AQUA, '-'));
			for (String plugin : HelpLister.getInstance().getPluginList())
				msg += plugin + ", ";
			if (!msg.equals("")) {
				if (msg.endsWith(", "))
					msg = msg.substring(0, msg.lastIndexOf(","));
				sender.sendMessage(msg);
			}
			return;
		}
		int page = 1;
		try {
			page = Integer.parseInt(args[0]);
			HelpLister.getInstance().sendHelpPage("AdminCmd", page, sender);
		} catch (NumberFormatException e) {
			if (args.length == 1) {
				if (!HelpLister.getInstance().sendHelpPage(args[0], 1, sender))
					Utils.sI18n(sender, "pluginNotFound", "plugin", args[0]);
			} else {
				try {
					page = Integer.parseInt(args[0]);
					if (!HelpLister.getInstance().sendHelpPage(args[0], page, sender))
						Utils.sI18n(sender, "pluginNotFound", "plugin", args[0]);
				} catch (NumberFormatException e1) {
					Utils.sI18n(sender, "NaN", "number", args[1]);
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
	public boolean argsCheck(String... args) {
		return args != null;
	}
}
