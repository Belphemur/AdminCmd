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

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Commands.CoreCommand;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.Tools.Utils;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */

public class Set extends CoreCommand {

	/**
	 *
	 */
	public Set() {
		cmdName = "bal_set";
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
		String message = "";
		if (args.hasFlag('m')) {
			if (PermissionManager.hasPerm(sender, "admincmd.server.set.motd")) {
				for (String msg : args)
					message += msg + " ";
				message = message.trim();
				String result = Utils.colorParser(message);
				if (result == null)
					result = message;
				LocaleManager.getInstance().addLocale("MOTD", result, true);
				LocaleManager.getInstance().save();
				Utils.sI18n(sender, "MOTDset", "motd", result);
			}
		} else if (args.hasFlag('n')) {
			if (PermissionManager.hasPerm(sender, "admincmd.server.set.news")) {
				for (String msg : args)
					message += msg + " ";
				message = message.trim();
				String result = Utils.colorParser(message);
				if (result == null)
					result = message;
				LocaleManager.getInstance().addLocale("NEWS", result, true);
				LocaleManager.getInstance().save();
				Utils.sI18n(sender, "NEWSset", "news", result);
			}
		} else if (args.hasFlag('r')) {
			if (PermissionManager.hasPerm(sender, "admincmd.server.set.rules")) {
				for (String msg : args)
					message += msg + " ";
				message = message.trim();
				String result = Utils.colorParser(message);
				if (result == null)
					result = message;
				LocaleManager.getInstance().addLocale("Rules", result, true);
				LocaleManager.getInstance().save();
				Utils.sI18n(sender, "RulesSet", "rules", result);
			}
		} else if (args.hasFlag('u')) {
			if (PermissionManager.hasPerm(sender, "admincmd.server.set.motd")) {
				for (String msg : args)
					message += msg + " ";
				message = message.trim();
				String result = Utils.colorParser(message);
				if (result == null)
					result = message;
				LocaleManager.getInstance().addLocale("MOTDNewUser", result, true);
				LocaleManager.getInstance().save();
				Utils.sI18n(sender, "MOTDset", "motd", result);
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
		return args != null && args.length >= 2;
	}

	/* (non-Javadoc)
	 * @see be.Balor.Manager.Commands.CoreCommand#registerBukkitPerm()
	 */
	@Override
	public void registerBukkitPerm() {		
		plugin.getPermissionLinker().addPermChild("admincmd.server.set.motd");
		plugin.getPermissionLinker().addPermChild("admincmd.server.set.news");
		plugin.getPermissionLinker().addPermChild("admincmd.server.set.rules");
		super.registerBukkitPerm();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.Commands.CoreCommand#permissionCheck(org.bukkit.command
	 * .CommandSender)
	 */
	@Override
	public boolean permissionCheck(CommandSender sender) {
		return true;
	}

}
