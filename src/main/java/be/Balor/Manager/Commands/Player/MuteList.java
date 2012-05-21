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
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/

package be.Balor.Manager.Commands.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Type;
import be.Balor.bukkit.AdminCmd.LocaleHelper;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class MuteList extends PlayerCommand {

	public MuteList() {
		permNode = "admincmd.player.mutelist";
		cmdName = "bal_mutelist";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args) throws PlayerNotFound {
		final Set<ACPlayer> players = new HashSet<ACPlayer>();
		players.addAll(ACPlayer.getPlayers(Type.MUTED));
		players.addAll(ACPlayer.getPlayers(Type.MUTED_COMMAND));
		final HashMap<String, String> replace = new HashMap<String, String>();
		final TreeSet<String> toSend = new TreeSet<String>();
		for (final ACPlayer p : players) {
			replace.clear();
			if (p.hasPower(Type.MUTED)) {
				replace.put("player", p.getName());
				replace.put("msg", p.getPower(Type.MUTED).getString());
				toSend.add(LocaleHelper.MUTELIST.getLocale(replace));
			} else if (p.hasPower(Type.MUTED_COMMAND)) {
				replace.put("player", p.getName());
				replace.put("msg", p.getPower(Type.MUTED_COMMAND).getString());
				toSend.add(LocaleHelper.MUTELIST.getLocale(replace));
			} else {
				continue;
			}
		}
		for (final String s : toSend) {
			sender.sendMessage(s);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(final String... args) {
		return true;
	}

}
