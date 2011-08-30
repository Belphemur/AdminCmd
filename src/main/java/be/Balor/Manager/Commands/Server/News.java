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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.CoreCommand;
import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class News extends CoreCommand {

	/**
	 * 
	 */
	public News() {
		permNode = "admincmd.server.news";
		cmdName = "bal_news";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, String... args) {
		String message = "";
		if (args.length == 0) {
			if (Utils.isPlayer(sender, false))
				Utils.sParsedLocale((Player) sender,"NEWS");
			else
				Utils.sI18n(sender, "NEWS");
			return;
		}
		if (PermissionManager.hasPerm(sender, "admincmd.server.news.edit")) {
			for (int i = 0; i < args.length; i++)
				message += args[i] + " ";
			message = message.trim();
			String result = Utils.colorParser(message);
			if (result == null)
				result = message;
			LocaleManager.getInstance().addLocale("NEWS", result, true);
			LocaleManager.getInstance().save();
			Utils.sI18n(sender, "NEWSset", "news", result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {
		super.registerBukkitPerm();
		plugin.getPermissionLinker().addPermChild("admincmd.server.news.edit");
	}

}
