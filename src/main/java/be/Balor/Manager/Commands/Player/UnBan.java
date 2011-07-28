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

import org.bukkit.command.CommandSender;

import com.Balor.bukkit.AdminCmd.ACHelper;

import be.Balor.Manager.ACCommands;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 *
 */
public class UnBan extends ACCommands {

	/**
	 * 
	 */
	public UnBan() {
		permNode = "admincmd.player.ban";
		cmdName = "bal_unban";
	}

	/* (non-Javadoc)
	 * @see be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender, java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, String... args) {
		String unban = args[0];
		if(ACHelper.getInstance().isPowerUser("banned", unban))
		{
			ACHelper.getInstance().removePowerUserWithFile("banned", unban);
			sender.getServer().broadcastMessage(Utils.I18n("unban", "player", unban));
		}
		else
			Utils.sI18n(sender, "playerNotFound", "player", unban);

	}

	/* (non-Javadoc)
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {		
		return args!=null && args.length>=1;
	}

}
