/*************************************************************************
 * This file is part of AdminCmd.
 *
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminCmd is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd. If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package be.Balor.Manager.Commands.Player;

import java.net.InetAddress;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.Commands.CommandArgs;
import be.Balor.Manager.Exceptions.PlayerNotFound;
import be.Balor.Manager.Permissions.ActionNotPermitedException;
import be.Balor.Player.ACPlayer;
import be.Balor.Player.PlayerManager;
import be.Balor.bukkit.AdminCmd.ACPluginManager;

import com.google.common.base.Joiner;

/**
 * @author Lathanael (aka Philippe Leipold)
 * 
 */
public class Search extends PlayerCommand {

	public Search() {
		cmdName = "bal_search";
		permNode = "admincmd.player.search";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.Commands.CoreCommand#execute(org.bukkit.command.
	 * CommandSender, be.Balor.Manager.Commands.CommandArgs)
	 */
	@Override
	public void execute(final CommandSender sender, final CommandArgs args) throws PlayerNotFound, ActionNotPermitedException {
		if (args.hasFlag('i')) {
			final String ip = args.getValueFlag('i');
			if (ip == null || ip.equals("") || ip.length() == 0) {
				return;
			}
			final Player[] onPlayers = ACPluginManager.getServer().getOnlinePlayers();
			final List<ACPlayer> exPlayers = PlayerManager.getInstance().getExistingPlayers();
			final TreeSet<String> players = new TreeSet<String>();
			final String on = "[ON] ", off = "[OFF] ";
			InetAddress ipAdress;
			for (final Player p : onPlayers) {
				ipAdress = p.getAddress().getAddress();
				if (ipAdress != null && ipAdress.toString().equals(ip)) {
					players.add(on + p.getName());
				}
			}
			String ip2;
			for (final ACPlayer p : exPlayers) {
				ip2 = p.getInformation("last-ip").getString();
				if (ip2 != null && ip2.toString().equals(ip)) {
					players.add(off + p.getName());
				}
			}
			final String found = Joiner.on(", ").join(players);
			sender.sendMessage(found);
			return;
		}
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
