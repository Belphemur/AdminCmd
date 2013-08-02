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
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Help.String.ACMinecraftFontWidthCalculator;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

import com.google.common.base.Joiner;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ListValues extends ServerCommand {

	/**
	 * 
	 */
	public ListValues() {
		permNode = "admincmd.server.list";
		cmdName = "bal_list";
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
			sender.sendMessage(ChatColor.DARK_AQUA + "Possibles Types :");
			sender.sendMessage(Joiner
					.on(ChatColor.WHITE + ", " + ChatColor.RED).join(
							Type.values()));
			return;
		}
		for (final String arg : args) {
			final Type type = Type.matchType(arg);
			if (type == null) {
				final HashMap<String, String> replace = new HashMap<String, String>();
				replace.put("value", arg);
				replace.put("type", "Power");
				LocaleHelper.DONT_EXISTS.sendLocale(sender, replace);
				continue;
			}
			final List<ACPlayer> list = ACPlayer.getPlayers(type);
			if (list != null) {
				sender.sendMessage(ChatColor.AQUA + type.display()
						+ ChatColor.WHITE + " (" + list.size() + ") "
						+ ChatColor.AQUA + ":");
				final String buffer = ChatColor.GOLD
						+ Joiner.on(ChatColor.WHITE + ", " + ChatColor.GOLD)
								.join(list);
				if (buffer.length() > ACMinecraftFontWidthCalculator.chatwidth) {
					sender.sendMessage(buffer.substring(0,
							ACMinecraftFontWidthCalculator.chatwidth));
					sender.sendMessage(buffer
							.substring(ACMinecraftFontWidthCalculator.chatwidth));
				} else {
					sender.sendMessage(buffer);
				}
			} else {
				LocaleManager.sI18n(sender, "emptyList");
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
