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

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Presentation extends PlayerCommand {

	/**
	 * 
	 */
	public Presentation() {
		super("bal_pres", "admincmd.player.pres");
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
			throws ActionNotPermitedException, PlayerNotFound {
		ACPlayer actarget = null;
		String pres = "";
		if (args.hasFlag('p')) {
			actarget = Utils.getACPlayer(sender, args, permNode);
			if (actarget == null) {
				return;
			}
			for (int i = 1; i < args.length; i++) {
				pres += args.getString(i) + " ";
			}
			pres = pres.trim();
		} else {
			if (Utils.isPlayer(sender)) {
				actarget = ACPlayer.getPlayer((Player) sender);
				for (int i = 0; i < args.length; i++) {
					pres += args.getString(i) + " ";
				}
				pres = pres.trim();
			}
		}
		pres = Utils.colorParser(pres);
		actarget.setPresentation(pres);
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", actarget.getName());
		replace.put("pres", pres);
		Utils.sI18n(sender, "presSet", replace);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return args != null && args.length >= 1;
	}

}
