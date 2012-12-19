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

import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.BannedPlayer;
import be.Balor.Player.IBan;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class UnBan extends PlayerCommand {

	/**
	 * 
	 */
	public UnBan() {
		permNode = "admincmd.player.ban";
		cmdName = "bal_unban";
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
		final String unban = args.getString(0);
		final IBan ban = ACHelper.getInstance().getBan(unban);
		if (ban != null) {
			if (ban instanceof BannedPlayer
					&& !Utils.checkImmunity(sender, args, 0)) {
				Utils.sI18n(sender, "insufficientLvl");
				return;
			}
			ACHelper.getInstance().unBanPlayer(ban);
			final String unbanMsg = Utils.I18n("unban", "player", unban);
			if (unbanMsg != null) {
				Utils.broadcastMessage(unbanMsg);
			}
		} else {
			final HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("ban", unban);
			LocaleHelper.NO_BAN_FOUND.sendLocale(sender, replace);
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
