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
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.Tools.Lister.Lister;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class UnMuteAll extends PlayerCommand {

	/**
	 * 
	 */
	public UnMuteAll() {
		super("bal_unmuteall", "admincmd.player.unmuteall");
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
		final Set<ACPlayer> players = new HashSet<ACPlayer>();
		players.addAll(ACPlayer.getPlayers(Type.MUTED));
		players.addAll(ACPlayer.getPlayers(Type.MUTED_COMMAND));
		if (players.isEmpty()) {
			LocaleHelper.NO_MUTED.sendLocale(sender);
			return;
		}
		for (final ACPlayer p : players) {
			p.removePower(Type.MUTED);
			p.removePower(Type.MUTED_COMMAND);
		}
		final HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("nb", String.valueOf(players.size()));
		LocaleHelper.UNMUTED_PLAYERS.sendLocale(sender, replace);
		final Lister list = Lister.getLister(Lister.List.MUTE, false);
		if (list != null) {
			list.update();
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
