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
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Played extends PlayerCommand {
	/**
	 *
	 */
	public Played() {
		super("bal_played", "admincmd.player.played");
		other = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.CoreCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, CommandArgs args) {
		Player target = Utils.getUser(sender, args, permNode);
		if (target != null) {
			String playername = Utils.getPlayerName(target, sender);
			long total = ACPlayer.getPlayer(target).getCurrentPlayedTime();
			Long[] time = Utils.transformToElapsedTime(total);
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("d", time[0].toString());
			replace.put("h", time[1].toString());
			replace.put("m", time[2].toString());
			replace.put("s", time[3].toString());
			replace.put("player", playername);
			Utils.sI18n(sender, "playedTime", replace);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.CoreCommand#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

}
