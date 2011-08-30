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
import org.bukkit.entity.Player;

import be.Balor.Manager.CoreCommand;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class UnMute extends CoreCommand {

	/**
	 * 
	 */
	public UnMute() {
		permNode = "admincmd.player.mute";
		cmdName = "bal_unmute";
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
		Player player = sender.getServer().getPlayer(args[0]);

		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", args[0]);
		if (ACHelper.getInstance().isValueSet(Type.MUTED, args[0])) {
			ACHelper.getInstance().removeValueWithFile(Type.MUTED, args[0]);
			if (player != null) {
				Utils.sI18n(player, "muteDisabled");
				if (!player.equals(sender))
					Utils.sI18n(sender, "muteDisabledTarget", replace);
			} else
				Utils.sI18n(sender, "muteDisabledTarget", replace);
		} else
			Utils.sI18n(sender, "playerNotFound", replace);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 1;
	}

}
