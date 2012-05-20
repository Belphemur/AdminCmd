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

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import org.bukkit.command.CommandSender;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.Tools.Type;
import be.Balor.Tools.Utils;
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

	/* (non-Javadoc)
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) throws PlayerNotFound {
		final Collection<ACPlayer> players = PlayerManager.getInstance().getOnlineACPlayers();
		final HashMap<String, String> replace = new HashMap<String, String>();
		final TreeSet<String> toSend = new TreeSet<String>();
		for (final ACPlayer p : players) {
			replace.clear();
			if (p.hasPower(Type.MUTED)) {
				replace.put("player", Utils.getPlayerName(p.getHandler()));
				replace.put("msg", p.getPower(Type.MUTED).getString());
				toSend.add(LocaleHelper.MUTELIST.getLocale(replace));
			} else if (p.hasPower(Type.MUTED_COMMAND)) {
				replace.put("player", Utils.getPlayerName(p.getHandler()));
				replace.put("msg", p.getPower(Type.MUTED_COMMAND).getString());
				toSend.add(LocaleHelper.MUTELIST.getLocale(replace));
			} else
				continue;
		}
		for (final String s : toSend) {
			sender.sendMessage(s);
		}
	}

	/* (non-Javadoc)
	 * @see be.Balor.Manager.Commands.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return true;
	}

}
